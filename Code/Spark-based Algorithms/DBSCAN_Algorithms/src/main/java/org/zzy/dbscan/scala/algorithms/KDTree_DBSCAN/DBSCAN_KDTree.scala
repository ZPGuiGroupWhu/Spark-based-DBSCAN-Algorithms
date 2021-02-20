package org.zzy.dbscan.scala.algorithms.KDTree_DBSCAN

import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.zzy.dbscan.java.index.balanced_KDTree.{KDBSCANPoint, KDTree}
import scala.collection.JavaConverters._

import scala.collection.mutable

object DBSCAN_KDTree {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","D:/KDBSCAN/Spark-based-DBSCAN-Algorithms/Code/Spark-based Algorithms/")
    val conf=new SparkConf()
    conf.setAppName("dbscan_kdtree")
    conf.setMaster("local[*]")
    val sc= new SparkContext(conf)
    val data_hubei=sc.textFile("D:/KDBSCAN/in/cluto-t7-10k.csv")
    val points_hubei=data_hubei.map(line=>{
      val parts=line.split(",")
      val pid=parts(0).toLong
      val coord=Array(parts(1).toDouble,parts(2).toDouble)
      val geoPoint=new KDBSCANPoint()
      geoPoint.setValue(coord)
      (pid,geoPoint)  //传入键值对的形式，系统会进行默认的hash分区
    })
    val iterablePoints_hubei=points_hubei.sample(false,1)
    val labeledPoints_hubei_kdtree=new KDBSCAN(iterablePoints_hubei.values.collect().toIterable,10,15).fit()
    val labeledPointsRDD_hubei_kdtree=sc.parallelize(labeledPoints_hubei_kdtree.toList)
    labeledPointsRDD_hubei_kdtree.foreach(println)
    println(labeledPointsRDD_hubei_kdtree.collect().length)
    labeledPointsRDD_hubei_kdtree.repartition(1).saveAsTextFile("D:/KDBSCAN/out/kdbscan_cluto_10_15")
  }
  class KDBSCAN(points:Iterable[KDBSCANPoint],eps: Double,minPoints:Int)extends Serializable {
    protected final val logger_kdbscan=LoggerFactory.getLogger(this.getClass)
    logger_kdbscan.info(s"About to start fitting")

//    println("建树前："+System.currentTimeMillis())
    val kdtreePoints=KDTree.build(points.toList.asJava)
//    println("建树后："+System.currentTimeMillis())

    def fit():Iterable[KDBSCANPoint] ={
      val labeledPoints=kdtreePoints.getNodes.asScala.toArray
      val rectangleKD=kdtreePoints.getRectangle(4).asScala.toArray

//      println("本地聚类前："+System.currentTimeMillis())

      val totalClusters=labeledPoints.foldLeft(0)((cluster,point)=>{
        if (!point.isVisited) { //点没有访问则继续往下
          point.setVisited(true)
          val neighbors = kdtreePoints.rangeSearch(point.getValue,eps).asScala.toIterable
          if (neighbors.size < minPoints) {
            point.setFlag(KDBSCANPoint.Flag.Noise)
            cluster //不产生新的类簇
          } else { // 新的类簇产生，cluster+1
            expandCluster(point, neighbors, labeledPoints, cluster + 1)
            cluster + 1
          }
        } else {// 所有点都访问过了则退出
          cluster
        }
      })
//      println("本地聚类结束："+System.currentTimeMillis())

      logger_kdbscan.info(s"found: $totalClusters clusters")
      labeledPoints //返回最终的结果，类簇记号都改变了
    }
    def expandCluster(
                       point: KDBSCANPoint,
                       neighbors: Iterable[KDBSCANPoint],
                       all: Array[KDBSCANPoint],
                       cluster: Int): Unit = {
      point.setFlag(KDBSCANPoint.Flag.Core)
      point.setCluster(cluster)
      // 将neighbors变成队列
      var allNeighbors = mutable.Queue(neighbors)

      while (allNeighbors.nonEmpty) {
        // 返回队列的第一个元素 并且删除
        allNeighbors.dequeue().foreach(neighbor => {
          if (!neighbor.isVisited) {//如果点没有访问过
            neighbor.setVisited(true)
            neighbor.setCluster(cluster)
            val neighborNeighbors = kdtreePoints.rangeSearch(neighbor.getValue,eps).asScala.toIterable
            if (neighborNeighbors.size >= minPoints) {
              neighbor.setFlag(KDBSCANPoint.Flag.Core)
              // 将neighborNeighbors加入队列
              allNeighbors.enqueue(neighborNeighbors)
            } else {
              neighbor.setFlag(KDBSCANPoint.Flag.Border)
            }
          }
          if (neighbor.isVisited){//如果点访问过,则表示不是核心点但是又在类簇内部，所以是边界点
            neighbor.setCluster(cluster)
            neighbor.setFlag(KDBSCANPoint.Flag.Border)
          }
        })
      }
    }
  }
}
