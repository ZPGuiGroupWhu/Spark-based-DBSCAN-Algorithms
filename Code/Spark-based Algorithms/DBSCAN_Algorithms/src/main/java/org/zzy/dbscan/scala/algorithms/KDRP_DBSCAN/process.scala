package org.zzy.dbscan.scala.algorithms.KDRP_DBSCAN

import org.apache.spark.rdd.RDD
import org.zzy.dbscan.java.KDRP_DBSCAN.{process, processPoints}
import org.zzy.dbscan.java.model.{KDTree, MC, Point}

import scala.collection.JavaConverters._

object process {
  class KDRP_DBSCAN()extends Serializable{
//    val MCList:List[MC]=List()
//    val unassignedList:List[Point]=List()
//    val noiseList:List[Point]=List()
    def fit(points: RDD[Point],eps: Double,minPoints:Int,numOfPartition:Int,kdTree: KDTree[Point],MCList:List[MC]):List[Point]={
      val process=new processPoints()
      val pointsProposed=process.kdrpDBSCAN(points.toLocalIterator.toList.asJava,eps,minPoints,numOfPartition,kdTree).asScala.toList
      pointsProposed
    }
    def buildMCs(points: RDD[Point],eps: Double,minPoints:Int,numOfPartition:Int,kdTree: KDTree[Point]):List[MC]={
      val process=new process()
      val MCs=process.buildMicroClusters(points.toLocalIterator.toList.asJava,eps,kdTree).asScala.toList
      MCs
    }
    def buildAuxKDTree(mcs:Iterable[MC]):Iterable[MC]={
      val process=new process()
      mcs.map(mc=>
        process.buildAuxKDTree(mc) )
     mcs
    }
    def processMicroClusters(mcs:Iterable[MC],Eps:Double,MinPts:Int,kdTree: KDTree[Point],MCList:List[MC]):Iterable[MC]={
      val process=new process()
      mcs.map { mc =>
        process.processMicroClusters(mc, Eps, MinPts)
        process.processRemPoints(mc, kdTree, Eps, MinPts,MCList.asJava)
      }
      mcs
    }
    def findNBPoints(point:Point,Eps:Double,MinPts:Int,kdTree:KDTree[Point],MCList:List[MC]):List[Point]={
      val process=new process()
      val list=process.findNBPoints(point,Eps,MinPts,kdTree,MCList.asJava).asScala.toList
      list
    }
    def processPoint(pointList:RDD[List[Point]]):List[Point]={
      val process=new process()
      val pp=pointList.map(ss=>ss.asJava)
      val list=process.processPoint(pp.toLocalIterator.toList.asJava).asScala.toList
      list
    }
}
}
