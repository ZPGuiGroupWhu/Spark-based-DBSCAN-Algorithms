package org.zzy.dbscan.scala.algorithms.KDTRee_DBSCAN_parallel

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.index.balanced_KDTree.{DBSCANRectangle, KDBSCANPoint, KDTree}
import org.zzy.dbscan.scala.algorithms.KDTree_DBSCAN.DBSCAN_KDTree
import org.zzy.dbscan.scala.merge.DBSCANGraph
import scala.collection.JavaConverters._
object KD_DBSCAN extends Serializable {
  type Margins=(DBSCANRectangle,DBSCANRectangle,DBSCANRectangle)// inner、main、outer
  type ClusterId=(Int,Int) // 定义类簇格式(partition,localClusterID)
  def main(args: Array[String]): Unit = {
    //System.setProperty("hadoop.home.dir","D:/kdsg/")//本地缺少hadoop环境，所以要加上
    val master=args(0)
    val eps=args(1).toDouble
    val minpts=args(2).toInt
    val inPath=args(3)
    val outPath=args(4)
    val numPartition=args(5).toInt
    val sampleRate=args(6).toDouble
    //    val jars: Array[String]=Array(args(7))
    val conf=new SparkConf()
    conf.setAppName("KDBSCAN")
      .setMaster(master)
        .set("spark.executor.memory",args(7))
        .set("spark.driver.memory",args(8))
//            .setJars(jars)

//    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
//    val master="local[*]"
//    val eps="0.01".toDouble
//    val minpts="1000".toInt
//    val inPath="D:/KDBSCAN/in/POI.csv"
//    val outPath="D:/KDBSCAN/out/POI"
//    val numPartition="128".toInt
//    val sampleRate="0.01".toDouble
//    val conf=new SparkConf()
//    conf.setAppName("KDBSCAN")
//      .setMaster(master)

    val sc=new SparkContext(conf)
    val lines=sc.textFile(inPath)
    // 读取数据并转化为向量形式
    val points=lines.map{line=>
      val parts=Vectors.dense(line.split(",").map(_.toDouble))
      parts
    }
    var startTime=System.currentTimeMillis()
    println("开始时间："+startTime)
//    println("生成采样点之前时间："+System.currentTimeMillis())
    // 生成采样点，采样率为(0,1]
    val sampleVectors=points.sample(false,sampleRate)
    val originPoints=points.map{vector=>
      var point=new KDBSCANPoint()
      point.setValue(Array(vector(1),vector(2)))
      point
    }
    val samplePoints=sampleVectors.map{vector=>
      var point=new KDBSCANPoint()
      point.setValue(Array(vector(1),vector(2)))
      point
    }
//    println("生成采样点之后时间："+System.currentTimeMillis())

    //获取分区矩形
    val rectangleList=KDTree.build(samplePoints.toLocalIterator.toList.asJava).getRectangle(numPartition).asScala.toList
//    println("获取分区矩形时间："+System.currentTimeMillis())

    //生成内中外矩形
    val localMargins=rectangleList.map(p=>(p.shrink(eps), p, p.shrink(-eps))).zipWithIndex
    //广播分区到集群
    val margins=points.context.broadcast(localMargins)

//    println("进行数据分区之前时间："+System.currentTimeMillis())
    //分配点到各自对应的分区
    val duplicated = for {
      point <- points.map{vector=>
        val point=new KDBSCANPoint()
            point.setValue(Array(vector(1),vector(2)))
        point
      }
      ((inner, main, outer), id) <- margins.value
      if outer.contains(point) //包含边界
    } yield (id, point)  // 这一步有一些点是冗余的=>边界点
    // yield关键字的作用是记录下每一次的循环产生的值，循环结束后将所有的yield值组成集合返回
//    println("进行数据分区之后时间："+System.currentTimeMillis())

    val numOfPartitions=rectangleList.size

//    println("本地聚类之前时间："+System.currentTimeMillis())
    //本地聚类
    val clustered =
      duplicated
        .groupByKey(numOfPartitions) // 将相同的key的值分组为单个序列，将结果分为numOfPartitions个分区
        .flatMapValues(points =>
        new DBSCAN_KDTree.KDBSCAN(points,eps,minpts).fit())
        .cache()
//    clustered.foreach(println)

    //    println("本地聚类之后时间："+System.currentTimeMillis())

    //找到所有的待合并点
    val mergePoints =
      clustered
        .flatMap({
          case (partition, point) =>
            margins.value
              .filter({
                case ((inner, main, outer), _) => outer.contains(point) && !inner.almostContains(point)
              })// 得到所有在主矩形内部但是不在内矩形的点
              .map({
              case (_, newPartition) => (newPartition, (partition, point))
            })
        })
        .groupByKey()
//    val s=mergePoints.flatMapValues(findAdjacencies)
//    val ss=s.values
//    val sss=ss.collect()
    //从合并点中找出有多个名字的点
    val adjacencies =
      mergePoints
        .flatMapValues(findAdjacencies)
        .values
        .collect()
//    adjacencies.foreach(println)
    //生成连通图
    val adjacencyGraph = adjacencies.foldLeft(DBSCANGraph[ClusterId]()) {
      case (graph, (from, to)) => graph.connect(from, to)
    }

    //找到所有类簇的id——(partitionID，clusterID)
    val localClusterIds =
      clustered
        .filter({ case (_, point) => point.getFlag != KDBSCANPoint.Flag.Noise })
        .mapValues(_.getCluster)
        .distinct()
        .collect()
        .toList

    //为所有的簇标记全局id
    // (0, Map[ClusterId, Int]())初始值，(id, map)迭代值，clusterId是localClusterIds里面的值————(partitionID，clusterID)
    // clusterIdToGlobalId形式——————Map((partitionID,clusterID),全局clusterID)
    val (total, clusterIdToGlobalId) = localClusterIds.foldLeft((0, Map[ClusterId, Int]())) {
      case ((id, map), clusterId) => {
        map.get(clusterId) match {
          case None => {
            val nextId = id + 1
            val connectedClusters = adjacencyGraph.getConnected(clusterId) + clusterId
            val toadd = connectedClusters.map((_, nextId)).toMap
            (nextId, map ++ toadd)
          }
          case Some(x) =>
            (id, map)
        }
      }
    }

    val clusterIds = points.context.broadcast(clusterIdToGlobalId)

    // reable points
    val labeledMain =
      clustered
        .filter(isMainPoint(_, margins.value))
        .map {
          case (partition, point) => {
            if (point.getFlag != KDBSCANPoint.Flag.Noise) {
              point.setCluster(clusterIds.value((partition, point.getCluster)))
            }
            (partition, point)
          }
        }.values
//    println("全局类簇生成之后时间："+System.currentTimeMillis())

    labeledMain.repartition(1).saveAsTextFile(outPath)
    var endTime=System.currentTimeMillis()
    println("结束时间："+endTime)
    println("聚类时间："+(endTime-startTime)/1000+"秒")
//    println("数据输出之后时间："+System.currentTimeMillis())
    sc.stop()
  }
  private def isMainPoint(
                            entry: (Int, KDBSCANPoint),
                            margins: List[(Margins, Int)]): Boolean = {
    entry match {
      case (partition, point) =>
        val ((inner, main, _), _) = margins.filter({
          case (_, id) => id == partition
        }).head
        main.contains(point)
    }
  }
  // 发现多个名称的点
  private def findAdjacencies(partition: Iterable[(Int, KDBSCANPoint)]): Set[((Int, Int), (Int, Int))]  = {
    val zero = (Map[String, ClusterId](), Set[(ClusterId, ClusterId)]())
    //zero是初始值，(seen,adjacencies)是迭代值，(partition, point)是partition里面的值
    val (seen, adjacencies) = partition.foldLeft(zero)({
      case ((seen, adjacencies), (partition, point)) =>
        // noise points are not relevant for adjacencies
        if (point.getFlag ==KDBSCANPoint.Flag.Noise) {
          (seen, adjacencies)
        } else {
          val clusterId = (partition, point.getCluster) //当前点拼成(partitionID,clusterID)的形式
          val value=point.getValue//获取当前点的值
          val key=value(0)+","+value(1)//拼接为字符串
          seen.get(key) match {
            case None                => (seen + (key -> clusterId), adjacencies) // 做当前点值与类簇编号的映射
            case Some(prevClusterId) => (seen, adjacencies + ((prevClusterId, clusterId)))// 如果key，匹配完成则返回前后两组类簇编号
          }
        }
    })
    adjacencies
  }
}
