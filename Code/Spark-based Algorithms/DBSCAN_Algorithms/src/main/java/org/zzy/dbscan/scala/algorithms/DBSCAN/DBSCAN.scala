package org.zzy.dbscan.scala.algorithms.DBSCAN

import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.zzy.dbscan.scala.utils.DBSCANLabeledPoint.Flag
import org.zzy.dbscan.scala.utils.{DBSCANLabeledPoint, DBSCANPoint}

import scala.collection.mutable.Queue
//经典DBSCAN算法
object DBSCAN {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")//本地缺少hadoop环境，所以要加上
    val conf=new SparkConf()
    conf.setAppName("dbscan_native")
    conf.setMaster("local[1]")
    //    conf.set("spark.executor.memory","12g")
    val sc=new SparkContext(conf)
    //使用测试数据
        val lines=sc.textFile("D:/KDBSCAN/in/cluto-t7-10k.csv")
    //使用湖北数据
//    val lines=sc.textFile("C:/Users/ASUS/Desktop/test.csv")

    val points=lines.map{line=>
      val parts=Vectors.dense(line.split(",").map(_.toDouble))
      val dbscanpoint=new DBSCANPoint(parts)
      dbscanpoint
    }

    val samplePoints=points.sample(false,1) //0.06 0.3 0.63 0.93

    println(samplePoints.collect().length)
    //对原始数据进行采样
    val samplePointsToIterable=samplePoints.collect().toIterable
    println("聚类开始："+System.currentTimeMillis())
    //使用原始DBSCAN方法生成结果
    val samplePoints_native=new DBSCANNaive(10,15).fit(samplePointsToIterable)

    val samplePointsRDD_native=sc.parallelize(samplePoints_native.toList)
    println("聚类结束："+System.currentTimeMillis())
    samplePointsRDD_native.saveAsTextFile("D:/KDBSCAN/out/cluto-t7-10k_原始DBSCAN")
  }

  /**
    * 经典DBSCAN算法
    * @param eps
    * @param minPoints
    */
  class DBSCANNaive(eps: Double, minPoints: Int){
    protected final val logger_native=LoggerFactory.getLogger(this.getClass)
    val minDistanceSquared = eps * eps //距离阈值的平方，为了简便运算

    //    def samplePoint = Array(new DBSCANLabeledPoint(Vectors.dense(Array(0D, 0D))))

    def fit(points: Iterable[DBSCANPoint]): Iterable[DBSCANLabeledPoint] = {
      logger_native.info(s"About to start fitting")
      val labeledPoints = points.map { new DBSCANLabeledPoint(_) }.toArray //这里是对点进行更新，所有用array
      //DBSCANLabeledPoint.Unknown为初始值，cluster表示返回结果对象（迭代值），point表示labeledPoints中的每个值
      val totalClusters =
      labeledPoints
        .foldLeft(DBSCANLabeledPoint.Unknown)(
          (cluster, point) => {
            if (!point.visited) { //点没有访问则继续往下
              point.visited = true
              val neighbors = findNeighbors(point, labeledPoints)
              if (neighbors.size < minPoints) {
                point.flag = Flag.Noise
                cluster //不产生新的类簇
              } else { // 新的类簇产生，cluster+1
                expandCluster(point, neighbors, labeledPoints, cluster + 1)
                cluster + 1
              }
            } else {// 所有点都访问过了则退出
              cluster
            }
          })

      logger_native.info(s"found: $totalClusters clusters")

      labeledPoints //返回最终的结果，类簇记号都改变了

    }

    //传入当前点和所有的点  //这里point的类型是DBSCANLabeledPoint或者是DBSCANPoint，DBSCANPoint是父类
    // view的作用是创造惰性视图，当数据量大并且操作多的时候，不使用视图会一步一步操作，使用视图会避免很多无效操作
    private def findNeighbors(point: DBSCANPoint,all: Array[DBSCANLabeledPoint]): Iterable[DBSCANLabeledPoint] =
      all.view.filter(other => {
        point.distanceSquared(other) <= minDistanceSquared
      })

    def expandCluster(
                       point: DBSCANLabeledPoint,
                       neighbors: Iterable[DBSCANLabeledPoint],
                       all: Array[DBSCANLabeledPoint],
                       cluster: Int): Unit = {

      point.flag = Flag.Core
      point.cluster = cluster
      // 将neighbors变成队列
      var allNeighbors = Queue(neighbors)

      while (allNeighbors.nonEmpty) {
        // 返回队列的第一个元素 并且删除
        allNeighbors.dequeue().foreach(neighbor => {
          if (!neighbor.visited) {//如果点没有访问过

            neighbor.visited = true
            neighbor.cluster = cluster

            val neighborNeighbors = findNeighbors(neighbor, all)

            if (neighborNeighbors.size >= minPoints) {
              neighbor.flag = Flag.Core
              // 将neighborNeighbors加入队列
              allNeighbors.enqueue(neighborNeighbors)
            } else {
              neighbor.flag = Flag.Border
            }
          }
          if (neighbor.visited){//如果点访问过,则表示不是核心点但是又在类簇内部，所以是边界点
            neighbor.cluster=cluster
            neighbor.flag=Flag.Border
          }
          //          或者使用如下方法，neighbor不存在cluster为0的情况，即之前访问过的点应重新标记为border并更改类簇编号
          //          if (neighbor.cluster == DBSCANLabeledPoint.Unknown) {
          //            neighbor.cluster = cluster
          //            neighbor.flag = Flag.Border
          //          }
        })

      }

    }

  }

}