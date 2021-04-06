package org.zzy.dbscan.scala.algorithms.RTree_DBSCAN

import archery.{Box, Entry, Point, RTree}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.zzy.dbscan.scala.utils.DBSCANLabeledPoint.Flag
import org.zzy.dbscan.scala.utils.{DBSCANLabeledPoint, DBSCANPoint}

import scala.collection.mutable.Queue

object RTree_DBSCAN {
  def main(args: Array[String]): Unit = {
        val master=args(0)
        val eps=args(1).toDouble
        val minpts=args(2).toInt
        val inPath=args(3)
        val outPath=args(4)
        val sampleRate=args(5).toDouble
        val conf=new SparkConf()
        conf.setAppName("RTree_DBSCAN")
          .setMaster(master)
          .set("spark.executor.cores",args(6))
          .set("spark.cores.max",args(7))
          .set("spark.executor.memory",args(8))

//    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
//    val master="local[*]"
//    val eps="10".toDouble
//    val minpts="15".toInt
//    val inPath="D:/KDBSCAN/in/cluto-t7-10k.csv"
//    val outPath="D:/KDBSCAN/out/cluto-t7-10k_210327"
//    val sampleRate="1".toDouble
//    val conf=new SparkConf()
//    conf.setAppName("RTree_DBSCAN")
//      .setMaster(master)

    val sc=new SparkContext(conf)
    val lines=sc.textFile(inPath)
    val points=lines.map{line=>
      val parts=Vectors.dense(line.split(",").map(_.toDouble))
      val dbscanpoint=new DBSCANPoint(parts)
      dbscanpoint
    }
    val samplePoints=points.sample(false,sampleRate)
    //对原始数据进行采样
    val samplePointsToIterable=samplePoints.collect().toIterable
    val t1=System.currentTimeMillis()
    //使用R树索引的DBSCAN方法
    val samplePoints_RTree=new RDBSCAN(eps,minpts).fit(samplePointsToIterable)
    val samplePointsRDD_RTree=sc.parallelize(samplePoints_RTree.toList)
    val t2=System.currentTimeMillis()
    samplePointsRDD_RTree.repartition(1).saveAsTextFile(outPath)
    println("聚类时间："+(t2-t1)/1000+"秒")
    sc.stop()
  }

  /**
    * R树索引的DBSCAN
    * @param eps
    * @param minPoints
    */
  class RDBSCAN(eps: Double, minPoints: Int)  {
    protected final val logger_RTree=LoggerFactory.getLogger(this.getClass)
    val minDistanceSquared = eps * eps // 定义eps平方

    // 模拟DBSCAN过程，DBSCANPoint是点(x,y),
    // DBSCANLabeledPoint加了cluster、flag和visited三个参数，用来记录点的属性信息
    def fit(points: Iterable[DBSCANPoint]): Iterable[DBSCANLabeledPoint] = {
      // 点插入R树
      //RTree[DBSCANLabeledPoint]()是初值，tempTree表示返回结果对象（迭代值），p表示points中的每个值
      val tree = points.foldLeft(RTree[DBSCANLabeledPoint]())(
        (tempTree, p) =>
          tempTree.insert(
            Entry(Point(p.x.toFloat, p.y.toFloat), new DBSCANLabeledPoint(p))))
      // cluster 初值
      var cluster = DBSCANLabeledPoint.Unknown
      tree.entries.foreach(entry => {
        val point = entry.value
        if (!point.visited) {
          point.visited = true
          val neighbors = tree.search(toBoundingBox(point), inRange(point))
          if (neighbors.size < minPoints) {
            point.flag = Flag.Noise
          } else {
            cluster += 1
            expandCluster(point, neighbors, tree, cluster)
          }
        }
      })
      logger_RTree.info(s"total: $cluster")
      tree.entries.map(_.value).toIterable
    }
    private def expandCluster(
                               point: DBSCANLabeledPoint,
                               neighbors: Seq[Entry[DBSCANLabeledPoint]],
                               tree: RTree[DBSCANLabeledPoint],
                               cluster: Int): Unit = {
      point.flag = Flag.Core
      point.cluster = cluster
      val left = Queue(neighbors)
      while (left.nonEmpty) {
        left.dequeue().foreach(neighborEntry => {
          val neighbor = neighborEntry.value
          if (!neighbor.visited) {
            neighbor.visited = true
            neighbor.cluster = cluster
            val neighborNeighbors = tree.search(toBoundingBox(neighbor), inRange(neighbor))
            if (neighborNeighbors.size >= minPoints) {
              neighbor.flag = Flag.Core
              left.enqueue(neighborNeighbors)
            } else {
              neighbor.flag = Flag.Border
            }
          }
          if (neighbor.cluster == DBSCANLabeledPoint.Unknown) {
            neighbor.cluster = cluster
            neighbor.flag = Flag.Border
          }
        })
      }
    }
    private def inRange(point: DBSCANPoint)(entry: Entry[DBSCANLabeledPoint]): Boolean = {
      entry.value.distanceSquared(point) <= minDistanceSquared
    }
    private def toBoundingBox(point: DBSCANPoint): Box = {
      Box(
        (point.x - eps).toFloat,
        (point.y - eps).toFloat,
        (point.x + eps).toFloat,
        (point.y + eps).toFloat)
    }

  }
}
