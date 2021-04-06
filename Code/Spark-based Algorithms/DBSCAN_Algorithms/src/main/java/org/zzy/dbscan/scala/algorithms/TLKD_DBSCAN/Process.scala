package org.zzy.dbscan.scala.algorithms.TLKD_DBSCAN

import java.util

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.Edge
import org.apache.spark.rdd.RDD
import org.zzy.dbscan.java.TLKD.process
import org.zzy.dbscan.java.index.balanced_KDTree.KDBSCANPoint
import org.zzy.dbscan.java.TLKDModel.{KDTree, MC}

import scala.collection.JavaConverters._

object Process {
  class KDRP_DBSCAN()extends Serializable{
    def buildMCs(points: Iterable[KDBSCANPoint], eps: Double, minPoints:Int, kdTree: KDTree[KDBSCANPoint])={
      val process=new process()
      process.buildMicroClusters(points.toList.asJava,eps,kdTree)
//      process.buildAuxKDTreeFromMC(MCs)
    }
//    def buildAuxKDTree(mcs:Iterable[MC]):Iterable[MC]={
//      val process=new process()
//      mcs.map(mc=>
//        process.buildAuxKDTree(mc) )
//     mcs
//    }
    def findNBPoints(point:KDBSCANPoint,Eps:Double,MinPts:Int,kdTree:KDTree[KDBSCANPoint]):List[KDBSCANPoint]={
      val process=new process()
      val list=process.findNBPoints(point,Eps,MinPts,kdTree).asScala.toList
      list
    }
    def processPoint(pointList:RDD[List[KDBSCANPoint]]):List[KDBSCANPoint]={
      val process=new process()
      val pp=pointList.map(ss=>ss.asJava)
      val list=process.processPoint(pp.toLocalIterator.toList.asJava).asScala.toList
      list
    }
    def processPointsFromMC(pointList:Iterable[List[KDBSCANPoint]]):List[KDBSCANPoint]={
      val process=new process()
      val pp=pointList.map(ss=>ss.asJava)
      val list=process.processPoint(pp.toList.asJava).asScala.toList
      list
    }
    def getPointAndNBs(points:Iterable[KDBSCANPoint],eps:Double,minPts:Int,broadcastPointKDTree:Broadcast[KDTree[KDBSCANPoint]])={
      points.map(p=>{
        val point=p
        val list=broadcastPointKDTree.value.reachableMCSearchID(point.getValue, 2*eps).asScala
        (point,list)
      })
    }
//    def processMCs(mcs:Iterable[MC],Eps:Double,MinPts:Int,kdTree:KDTree[KDBSCANPoint],MCList:List[MC])={
//      val mcPoints=mcs.map(mc=>{
//        val result=mc.getPoints.asScala.toList
//        result
//      })
//      val points=processPointsFromMC(mcPoints)
//      val pointAndNB=points.map(p=>{
//        val list=findNBPoints(p,Eps,MinPts,kdTree)
//        (p,list)
//      }).filter(_._2.size>=MinPts)
//      pointAndNB
//    }
//
//    def processMCsssss(mcs:Iterable[MC],Eps:Double,MinPts:Int,kdTree:KDTree[KDBSCANPoint])={
//      val mcPoints=mcs.map(mc=>{
//        val result=mc.getPoints.asScala.toList
//        result
//      })
//      val points=processPointsFromMC(mcPoints)
//      val pointAndNB=points.map(p=>{
//        val list=findNBPoints(p,Eps,MinPts,kdTree)
//        (p,list)
//      }).filter(_._2.size>=MinPts).flatMap(coreRange => {
//        for (p <- coreRange._2) yield Edge(coreRange._1.getId, p.getId, coreRange._1)//Edge——单一有向边
//      })
//      pointAndNB
//    }
}
}
