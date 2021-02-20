package org.zzy.dbscan.scala.algorithms.KDRP_DBSCAN

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.model.{KDTree, MC, Point}

object KDRP_DBSCAN {
  def main(args:Array[String]):Unit={
    //本地模式
    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
    val master="local[*]"
//    val eps="3".toDouble
//    val minpts="2".toInt
//    val inPath="D:/KDBSCAN/in/test.csv"
//    val outPath="D:/KDBSCAN/out/test"
    val eps="10".toDouble
    val minpts="15".toInt
    val inPath="D:/KDBSCAN/in/cluto-t7-10k.csv"
    val outPath="D:/KDBSCAN/out/cluto-t7-10k_1"
    val numOfPartition="1".toInt
    val conf=new SparkConf()
    conf.setAppName("KDRP-DBSCAN")
      .setMaster(master)
    //集群模式
//    val master=args(0)
//    val eps=args(1).toDouble
//    val minPts=args(2).toInt
//    val inPath=args(3)
//    val outPath=args(4)
//    val conf=new SparkConf()
//    conf.setAppName("KDRP-DBSCAN")
//      .setMaster(master)
//      .set("spark.executor.memory",args(5))
//      .set("spark.driver.memory",args(6))

    val sc=new SparkContext(conf)
    val lines=sc.textFile(inPath)
    val pointKDTree=new KDTree[Point](2)//新建二维KD树，高维数据还需要修改，也可以用户自己输入
    val broadcastPointKDTree=sc.broadcast(pointKDTree);
    //读取数据并转化为向量形式
    val points=lines.map{line=>
      val parts=Vectors.dense(line.split(",").map(_.toDouble))
      var point=new Point()
      point.setId(parts(0).toInt)
      point.setValue(Array(parts(1),parts(2)))//这里目前只适用于二维的情况，高维数据还需要修改
      point
    }
//    val MCList:List[MC]=List()
//    val unassignedList:List[Point]=List()
//    val noiseList:List[Point]=List()
    val labledPoints=new process.KDRP_DBSCAN(points,eps,minpts,numOfPartition,broadcastPointKDTree.value).fit()
    sc.parallelize(labledPoints).repartition(1).saveAsTextFile(outPath)
  }




}
