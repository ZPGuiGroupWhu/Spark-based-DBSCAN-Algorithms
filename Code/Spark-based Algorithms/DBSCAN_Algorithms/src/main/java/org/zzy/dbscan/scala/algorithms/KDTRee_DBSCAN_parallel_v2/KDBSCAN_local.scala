package org.zzy.dbscan.scala.algorithms.KDTRee_DBSCAN_parallel_v2

import org.apache.spark.graphx.{Edge, Graph}
import org.slf4j.LoggerFactory
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint
import org.zzy.dbscan.java.index.classical_KDTree_v2.balanced_KDTree.KDTree_v2
import org.zzy.dbscan.scala.merge.DBSCANGraph

import scala.collection.JavaConverters._

class KDBSCAN_local(points:Iterable[KDBSCANPoint],eps: Double,minPoints:Int)extends Serializable {

  protected final val logger_kdbscan=LoggerFactory.getLogger(this.getClass)
  logger_kdbscan.info(s"About to start fitting")
  val kdtreePoints=KDTree_v2.build(points.toList.asJava)
  def fit():Iterable[KDBSCANPoint] ={
    val labeledPoints=kdtreePoints.getNodes.asScala.toArray
    val geoPoints=labeledPoints.map{point=>
      val pid=point.getId
      (pid,point)
    }
//    geoPoints.foreach(println);
//    println(geoPoints.length)
    //找到待合并的点——所有的核心点及其领域点
   val mergePoints=geoPoints.map(tuple=>{
     val point=tuple._2
     val neighborsWithPoint = kdtreePoints.rangeSearch(point,eps).asScala.toArray  //得到point及point的邻域
     (point,neighborsWithPoint)
   }).filter(_._2.size>=minPoints).map({
     case (a,b)=>(a.getId,(b.map(change=>(change.getCluster,change))).toIterable)
   })
//    println(mergePoints.length)
//    val cccccccc=geoPoints.map(tuple=>{
//      val point=tuple._2
//      val neighborsWithPoint = kdtreePoints.rangeSearch(point,eps).asScala.toArray  //得到point及point的邻域
//      (point,neighborsWithPoint)
//    }).filter(_._2.size>=minPoints).map({
//      case (a,b)=>(a.getId,(b.map(change=>(change.getCluster,change))).toIterable)
//    })
//    cccccccc.foreach(println)



    val sssssss=mergePoints.flatMap(ccc=>ccc._2) //得到全部类簇中的点，这里的点包含冗余
//    sssssss.foreach(println)
      val zz=sssssss.map(zzz=>zzz._2.getId)
//    println(zz.length)
//    println(zz.distinct.length)
//    zz.foreach(println)
    val abc=for {
      point<-geoPoints.map{p=>
        var point =p._2
        point
      }
      if zz.contains(point.getId)
    }yield (point.getId,point)
    val result=sssssss++abc
//    println(abc.size)
//    println(result.size)






    //从合并点中找出有多个名字的点
    val adjacencies =findAdjacencies(sssssss)
    println(adjacencies.size)
//adjacencies.foreach(println)
    //生成连通图
    val adjacencyGraph = adjacencies.foldLeft(DBSCANGraph[Int]()) {
      case (graph, (from, to)) => graph.connect(from, to)
    }
    //有多少个不同的簇
    val localClusterIds =mergePoints.map(s=>s._1).distinct.toList
//    println(localClusterIds)
    //为所有的簇标记全局id
    // (0, Map[ClusterId, Int]())初始值，(id, map)迭代值，clusterId是localClusterIds里面的值————(partitionID，clusterID)
    // clusterIdToGlobalId形式——————Map((partitionID,clusterID),全局clusterID)
    val (total, clusterIdToGlobalId) = localClusterIds.foldLeft((0, Map[Int, Int]())) {
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

    val clusterIds=clusterIdToGlobalId //合并之后应该有的clusterid
    val labeledPointsFinal=sssssss.map {
      case (partition, point) => {
        if (point.getCluster != 0) {
          point.setCluster(clusterIds(point.getCluster))
        }
        point
      }
    }


    labeledPointsFinal.toIterable
  }
  // 这里的partition指的是 每个核心点及其邻域组成的区域
  private def findAdjacencies(partition: Iterable[(Int, KDBSCANPoint)]): Set[(Int, Int)] = {
    val zero = (Map[String, Int](), Set[(Int, Int)]())
    //zero是初始值，(seen,adjacencies)是迭代值，(partition, point)是partition里面的值
    val (seen, adjacencies) = partition.foldLeft(zero)({
      case ((seen, adjacencies), (partition, point)) => {
          val clusterId = partition //每一个小的类簇的clusterid就是核心点的编号
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

