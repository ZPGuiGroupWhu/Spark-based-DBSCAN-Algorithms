package org.zzy.dbscan.scala.algorithms.KDRP_DBSCAN

import org.apache.spark.graphx.lib.ConnectedComponents
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.model.{KDTree, MC, Point}
import scala.collection.JavaConverters._


object KDRP_DBSCAN {
  def main(args:Array[String]):Unit={
    //本地模式
    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
    val master="local[*]"
//    val eps="3".toDouble
//    val minpts="2".toInt
//    val inPath="D:/KDBSCAN/in/test2.csv"
//    val outPath="D:/KDBSCAN/out/test"
    val eps="10".toDouble
    val minpts="15".toInt
    val inPath="D:/KDBSCAN/in/cluto-t7-10k.csv"
    val outPath="D:/KDBSCAN/out/cluto-t7-10k_22222"
    val numOfPartitions="1".toInt//节点数
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
      (point.getId.toLong,point)
    }
    //建立超球体、第一层KD树
    val MCList=new process.KDRP_DBSCAN().buildMCs(points.values,eps,minpts,numOfPartitions,broadcastPointKDTree.value)
    //数据分区
    val MCs=sc.parallelize(MCList)
    
    val partitionedMCs=MCs.map{mc=>
      val partitionID=scala.util.Random.nextInt(numOfPartitions)
      (partitionID,mc)
    }
    //构建第二层KD树
    val MCsProcessing=partitionedMCs
      .groupByKey(numOfPartitions)
      .flatMapValues(mcs=>{
        new process.KDRP_DBSCAN().buildAuxKDTree(mcs)
      })
    val MCListFinal=MCsProcessing.values.collect().toList
    //分区内识别核心点
    val clustered=MCsProcessing
      .groupByKey(numOfPartitions)
      .flatMapValues(mcs=>{
        val ccccc=broadcastPointKDTree.value
        new process.KDRP_DBSCAN().processMicroClusters(mcs,eps,minpts,broadcastPointKDTree.value,MCListFinal)
      })
    val ssssssss=clustered.values.map(mc=>{
      val listIn=mc.getInPoints.asScala.toList
      val listOut=mc.getOutPoints.asScala.toList
      val result=listIn ::: listOut
      result
    })
    val pointsFromMCs=new process.KDRP_DBSCAN().processPoint(ssssssss)
    val pointsFromMCsRDD=sc.parallelize(pointsFromMCs)

    println("------------------------------------")
    //图联通
    val edges=pointsFromMCsRDD.filter( _.getFlag.equals(Point.Flag.Core)).map(p=>{
      val list=new process.KDRP_DBSCAN().findNBPoints(p,eps,minpts,broadcastPointKDTree.value,MCListFinal)
      (p,list)
    }).flatMap(coreRange => {
      for (p <- coreRange._2) yield Edge(coreRange._1.getId, p.getId, coreRange._1)//Edge——单一有向边
    })

    val srcGraph = Graph.fromEdges(edges,10)
    val cc = ConnectedComponents.run(srcGraph)

    val result=points.leftOuterJoin(cc.vertices).map {s=>
      val resultPoint=new Point()
      resultPoint.setValue(s._2._1.getValue)
      if (s._2._2!=None){
        resultPoint.setCluster(s._2._2.get.toInt)
      }else{
        resultPoint.setCluster(0)
      }
      resultPoint
    }
    result.repartition(1).saveAsTextFile(outPath)
//    val MCList:List[MC]=List()
//    val unassignedList:List[Point]=List()
//    val noiseList:List[Point]=List()
//    val labledPoints=new process.KDRP_DBSCAN(points,eps,minpts,numOfPartition,broadcastPointKDTree.value).fit()
//    sc.parallelize(labledPoints).repartition(1).saveAsTextFile(outPath)
  }




}
