package org.zzy.dbscan.scala.algorithms.TLKD_DBSCAN

import org.apache.spark.graphx.lib.ConnectedComponents
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint
import org.zzy.dbscan.java.TLKDModel.KDTree
import org.zzy.dbscan.scala.algorithms.TLKD_DBSCAN

import scala.collection.JavaConverters._
//使用two-level KD树构造全局索引，然后查出每个点的邻域，最后进行图联通
object TLKDRP_DBSCAN {
  def  main(args:Array[String]):Unit={
    //单机
//        System.setProperty("hadoop.home.dir","D:/KDBSCAN/")
//        val master="local[1]"
//        val fname="D:/KDBSCAN/in/cluto-t7-10k.csv"
//        val outpath="D:/KDBSCAN/out/cluto-t7-10k_210331"
//        val epsilon="10".toDouble
//        val minPts="15".toInt
//        val numOfPartitions="1".toInt
//        val conf = new SparkConf()
//        val sc = new SparkContext(
//          conf.setAppName("TLKDRP_DBSCAN")
//            .setMaster(master))
    //集群
    val master = args(0)
    val fname = args(1)
    val outpath = args(2)
    val epsilon = args(3).toDouble
    val minPts = args(4).toInt
    val numOfPartitions=args(5).toInt
    val conf = new SparkConf()
    val sc = new SparkContext(
      conf.setAppName("TLKDRP_DBSCAN")
        .setMaster(master)
        .set("spark.executor.cores",args(6))
        .set("spark.cores.max",args(7))
        .set("spark.executor.memory",args(8)))


    val lines = sc.textFile(fname) //读取文件
    val pointKDTree = new KDTree[KDBSCANPoint](2);//新建一个two-level kd树索引结构，2代表的是二维数据

    val t1=System.currentTimeMillis()
    val points = lines.map { s =>
      val parts = s.split(",")//
    val geoPoint = new KDBSCANPoint()
      val pid = parts(0).toLong
      geoPoint.setId(parts(0).toInt)
      geoPoint.setValue(Array(parts(1).toDouble, parts(2).toDouble))
      (pid, geoPoint)//返回键值对 geopoint的编号以及点
    }
    new Process.KDRP_DBSCAN().buildMCs(points.values.collect().toIterable,epsilon,minPts,pointKDTree)
    val broadcastPointKDTree = sc.broadcast(pointKDTree);//索引结构广播到spark集群
    val t2=System.currentTimeMillis()
    print("树结构构建时间为："+(t2-t1)/1000+"秒")

    val partitionedPoints=points.values.map(p=>
    {
      val id=scala.util.Random.nextInt(numOfPartitions)
      (id,p)
    }
    )

    val edges=partitionedPoints.groupByKey(numOfPartitions).flatMapValues(cc=>{
    val pAndNBs=new Process.KDRP_DBSCAN().getPointAndNBs(cc,epsilon,minPts,broadcastPointKDTree)
    val edge = pAndNBs.filter(_._2.size>=minPts).flatMap(coreRange => {
    for (p <- coreRange._2) yield Edge(coreRange._1.getId, p.longValue(), coreRange._1)
  })
   edge
  })

    val srcGraph = Graph.fromEdges(edges.values,10)
    val cc = ConnectedComponents.run(srcGraph)
    val result=points.leftOuterJoin(cc.vertices).map {s=>
      val resultPoint=new KDBSCANPoint()
      resultPoint.setValue(s._2._1.getValue)
      if (s._2._2!=None){
        resultPoint.setCluster(s._2._2.get.toInt)
      }else{
        resultPoint.setCluster(0)
      }
      resultPoint
    }
    result.repartition(1).saveAsTextFile(outpath)
    sc.stop()
  }
}
