package org.zzy.dbscan.scala.algorithms.TLKD_DBSCAN

import java.util

import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.zzy.dbscan.java.TLKD.process
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint
import org.zzy.dbscan.java.TLKDModel.{KDTree, MC}

import scala.collection.JavaConverters._
import scala.collection.mutable

object TLKD_DBSCAN {
  def main(args: Array[String]): Unit = {
        val master=args(0)
        val eps=args(1).toDouble
        val minpts=args(2).toInt
        val inPath=args(3)
        val outPath=args(4)
        val sampleRate=args(5).toDouble
        val conf=new SparkConf()
        conf.setAppName("TLKD_DBSCAN")
          .setMaster(master)
          .set("spark.executor.cores",args(6))
          .set("spark.cores.max",args(7))
          .set("spark.executor.memory",args(8))

//    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
//    val master="local[*]"
//    val eps="10".toDouble
//    val minpts="15".toInt
//    val inPath="D:/KDBSCAN/in/cluto-t7-10k.csv"
//    val outPath="D:/KDBSCAN/out/cluto-t7-10k_210329"
//    val sampleRate="1".toDouble
//    val conf=new SparkConf()
//    conf.setAppName("TLKD_DBSCAN")
//      .setMaster(master)

    val sc= new SparkContext(conf)
    val data=sc.textFile(inPath)
    val points=data.map(line=>{
      val parts=line.split(",")
      val pid=parts(0).toLong
      val coord=Array(parts(1).toDouble,parts(2).toDouble)
      val geoPoint=new KDBSCANPoint()
      geoPoint.setValue(coord)
      (pid,geoPoint)  //传入键值对的形式，系统会进行默认的hash分区
    })
    val iterablePoints=points.sample(false,sampleRate)
    val t1=System.currentTimeMillis()
    val labeledPoints_kdtree=new Clustering(iterablePoints.values.collect().toIterable,eps,minpts).fit()
    val labeledPointsRDD_kdtree=sc.parallelize(labeledPoints_kdtree.toList)
    val t2=System.currentTimeMillis()
    labeledPointsRDD_kdtree.repartition(1).saveAsTextFile(outPath)
    println("聚类时间："+(t2-t1)/1000+"秒")
    sc.stop()
  }
  class Clustering(points:Iterable[KDBSCANPoint],eps: Double,minPoints:Int)extends Serializable {
    protected final val logger_kdbscan=LoggerFactory.getLogger(this.getClass)
//    val mClusterList = new util.ArrayList[MC]//之前是想基于MC进行分区，因此在建树的时候加上了，现在不需要了
    val kdTree=new KDTree[KDBSCANPoint](2)
    new Process.KDRP_DBSCAN().buildMCs(points,eps,minPoints,kdTree)
    def fit():Iterable[KDBSCANPoint] ={
      val labeledPoints=new process().nonRecOrder(kdTree.root).asScala.toArray
      val totalClusters=labeledPoints.foldLeft(0)((cluster,point)=>{
        if (!point.isVisited) { //点没有访问则继续往下
          point.setVisited(true)
          val neighbors = kdTree.reachableMCSearch(point.getValue,2*eps).asScala
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
            val neighborNeighbors = kdTree.reachableMCSearch(neighbor.getValue,2*eps).asScala
            if (neighborNeighbors.size >= minPoints) {
              neighbor.setFlag(KDBSCANPoint.Flag.Core)
              // 将neighborNeighbors加入队列
              allNeighbors.enqueue(neighborNeighbors)
            } else {
              neighbor.setFlag(KDBSCANPoint.Flag.Border)
            }
          }
          if (neighbor.getCluster==0){//如果点访问过,则表示不是核心点但是又在类簇内部，所以是边界点
            neighbor.setCluster(cluster)
            neighbor.setFlag(KDBSCANPoint.Flag.Border)
          }
        })
      }
    }
  }
}
