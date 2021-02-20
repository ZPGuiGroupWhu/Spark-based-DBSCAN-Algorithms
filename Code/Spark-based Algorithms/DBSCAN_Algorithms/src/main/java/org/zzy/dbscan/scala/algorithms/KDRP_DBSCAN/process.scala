package org.zzy.dbscan.scala.algorithms.KDRP_DBSCAN

import org.apache.spark.rdd.RDD
import org.zzy.dbscan.java.KDRP_DBSCAN.processPoints
import org.zzy.dbscan.java.model.{KDTree, MC, Point}
import scala.collection.JavaConverters._

object process {
  class KDRP_DBSCAN(points: RDD[Point],eps: Double,minPoints:Int,numOfPartition:Int,kdTree: KDTree[Point])extends Serializable{
//    val MCList:List[MC]=List()
//    val unassignedList:List[Point]=List()
//    val noiseList:List[Point]=List()
    def fit():List[Point]={
      val process=new processPoints()
      val pointsProposed=process.kdrpDBSCAN(points.toLocalIterator.toList.asJava,eps,minPoints,numOfPartition,kdTree).asScala.toList
      pointsProposed
    }
}
}
