package org.zzy.dbscan.scala.algorithms.KDSG

import org.apache.spark.graphx.lib.ConnectedComponents
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.index.classical_KDTree.{KDTree, Point}

import scala.collection.JavaConverters._

object KDSG{
  def main(args: Array[String]): Unit = {
    val master = args(0)
    val fname = args(1)
    val outpath = args(2)
    val epsilon = args(3)
    val minPts = args(4).toInt
    val conf = new SparkConf()
    val sc = new SparkContext(
      conf.setAppName("KDSG-DBSCAN")
        .setMaster(master)
        .set("spark.executor.cores",args(5))
        .set("spark.cores.max",args(6))
        .set("spark.executor.memory",args(7)))

//    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")
//    val master="local[1]"
//    val fname="D:/KDBSCAN/in/cluto-t7-10k.csv"
//    val outpath="D:/KDBSCAN/out/cluto-t7-10k_210331"
//    val epsilon="10"
//    val minPts="15".toInt
//    val conf = new SparkConf()
//    val sc = new SparkContext(
//      conf.setAppName("KDSG-DBSCAN")
//        .setMaster(master))

//    println("开始时间："+System.currentTimeMillis())
    val lines = sc.textFile(fname)
    val pointKDTree = new KDTree[Point](2);
    val broadcastPointKDTree = sc.broadcast(pointKDTree);

    val points = lines.map { s =>
      val parts = s.split(",")
    val geoPoint = new Point()
      val pid = parts(0).toLong
      geoPoint.setId(pid)
      geoPoint.setCoord(parts(1), parts(2))
      broadcastPointKDTree.value.insertTree(geoPoint.coord, geoPoint);
      (pid, geoPoint)
    }
    val edges = points.map(tuple => {
      val point = tuple._2
      var list = broadcastPointKDTree.value.rangeSearch(point.coord, epsilon.toDouble).asScala
      (point, list)
    }).filter(_._2.size > minPts).flatMap(coreRange => {
      for (p <- coreRange._2) yield Edge(coreRange._1.getId, p.getId, coreRange._1)
    })
//    println("原始边数："+edges.collect().length)
    val srcGraph = Graph.fromEdges(edges,10)
    val cc = ConnectedComponents.run(srcGraph)

    val result=points.leftOuterJoin(cc.vertices).map {s=>
      val resultPoint=new Point()
      resultPoint.setCoord(s._2._1.coord)
      if (s._2._2!=None){
        resultPoint.setClusterId(s._2._2.get.toInt)
      }else{
        resultPoint.setClusterId(0)
      }
      resultPoint
    }
//    println("结束时间："+System.currentTimeMillis())
    result.repartition(1).saveAsTextFile(outpath)
    sc.stop()
  }
}
