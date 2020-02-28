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
    System.setProperty("hadoop.home.dir","D:/kdsg/")//本地缺少hadoop环境，所以要加上
    val conf=new SparkConf()
    conf.setAppName("dbscan_native")
    conf.setMaster("local[1]")
//    conf.set("spark.executor.memory","12g")
    val sc=new SparkContext(conf)
    //使用测试数据
//    val lines=sc.textFile("D:/kdsg/in/origin.csv")
    //使用湖北数据
    val lines=sc.textFile("D:/kdsg/in/hubei.csv")

    val points=lines.map{line=>
      val parts=Vectors.dense(line.split(",").map(_.toDouble))
      val dbscanpoint=new DBSCANPoint(parts)
      dbscanpoint
    }

    val samplePoints=points.sample(false,0.93) //0.06 0.3 0.63 0.93

    println(samplePoints.collect().length)
    //对原始数据进行采样
    val samplePointsToIterable=samplePoints.collect().toIterable
    println("聚类开始："+System.currentTimeMillis())
    //使用R树索引的DBSCAN方法
    val samplePoints_RTree=new RDBSCAN(0.1,4000).fit(samplePointsToIterable)
    val samplePointsRDD_RTree=sc.parallelize(samplePoints_RTree.toList)
    samplePointsRDD_RTree.saveAsTextFile("D:/kdsg/out/20200228/rtree")
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
