package org.zzy.dbscan.scala.algorithms.KDSG

import org.apache.spark.graphx.lib.ConnectedComponents
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.{SparkConf, SparkContext}
import org.zzy.dbscan.java.index.classical_KDTree.{KDTree, Point}

import scala.collection.JavaConverters._

/**
  * Driver program for running graph algorithms.
  * 地理数据集用arcgis或者kepler.gl
  * 测试数据集使用python或者matlab
  */
object KDSG{
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","D:/KDBSCAN/")
    /**
      * 完整定义一个函数为：
      * def 函数名（参数：参数类型，参数：参数类型）: 返回值类型={
      * }
      * main函数是scala的入口函数
      * scala中的分号是可选的，但是两条语句在同一行的时候就必须使用分号分割
      * Unit相当于java中的void
      */
    /**
      * val master = "spark://master:7077"
      *     本地应该是 val master = "spark://spark1:7077"
      * val fname = "hdfs://master:9000/spark/in/20.csv"
      *     本地 val fname = "hdfs://spark1:8020/spark/in/20.csv"
      *     本地 val fname = "hdfs://spark1:9000/spark/in/hubei.csv"
      * val outpath = "hdfs://master:9000/spark/out/02"
      *     本地 outpath = "hdfs://spark1:8020/spark/out/02"
      *     本地 outpath = "hdfs://spark1:9000/spark/out/hubei"
      * val epsilon = 1
      *     指定eps（半径）
      * val minPts = 2
      *     指定密度mip （最小点）
      */

//    val master = args(0)
    val master="local[*]"
//    val fname = args(1)
    val fname="D:/KDBSCAN/in/cluto-t7-10k.csv"
//    val outpath = args(2)
    val outpath="D:/KDBSCAN/out/cluto-t7-10k_2"
//    val epsilon = args(3)
    val epsilon="10"
//    val minPts = args(4).toInt
    val minPts="15".toInt
    val conf = new SparkConf()//SparkConf包含了Spark集群配置的各种参数
    //    GraphXUtils.registerKryoClasses(conf)
   // val jars: Array[String] = Array("hdfs://master:9000/spark/jar/1.jar");//文件的jar包位置，需要将程序打成jar包
    //hdfs://spark1:8020/spark/jar/1.jar
    val sc = new SparkContext(
      conf.setAppName("KDSG-DBSCAN")
        .setMaster(master))//设置运行模式，local[4]表示本地4核，spark://master:7077表示standalone模式
     //   .set("spark.executor.memory", args(5))//给每个节点分配的内存
   //     .set("spark.driver.memory", args(6))//给当前应用分配的总内存
       // .setJars(jars))    //设置jar包位置
    println("开始时间："+System.currentTimeMillis())
    val lines = sc.textFile(fname) //读取文件
    val pointKDTree = new KDTree[Point](2);//新建一个kd树索引结构，2代表的是二维数据
    val broadcastPointKDTree = sc.broadcast(pointKDTree);//索引结构广播到spark集群

    /*
    首先将读取的每行点按照逗号分隔
    然后新建地理坐标点，并将地理坐标的id,
     */
    val points = lines.map { s =>
      val parts = s.split(",")//
    val geoPoint = new Point()
      val pid = parts(0).toLong   //读取数据的第一个点并转换数据格式
      geoPoint.setId(pid)         //对geoPoint赋值
      geoPoint.setCoord(parts(1), parts(2))//这里只需要直接传进去字符串，转换操作在Point类内部，数据的第二个值为经度，第三个值为维度
      broadcastPointKDTree.value.insertTree(geoPoint.coord, geoPoint);//将点插入k-d树中
      (pid, geoPoint)//返回键值对 geopoint的编号以及点
    }
    val edges = points.map(tuple => {
      val point = tuple._2
//      var result: ListBuffer[Edge[Point]] = ListBuffer(new Edge[Point]());
      var list = broadcastPointKDTree.value.rangeSearch(point.coord, epsilon.toDouble).asScala
      (point, list) // list里面存储了所有的邻域点
    }).filter(_._2.size > minPts).flatMap(coreRange => {//核心点441
      for (p <- coreRange._2) yield Edge(coreRange._1.getId, p.getId, coreRange._1)//Edge——单一有向边
    })//.distinct()  //得到所有核心点与边界点组成的边，形式为(core.id,border.id,core)
    println("原始边数："+edges.collect().length)//7837 则点的个数为7837+441=8278
//    val aaaaaaaa=edges.collect().length
//    val ccccccccccc=points.map(tuple => {
//      val point = tuple._2
//      //      var result: ListBuffer[Edge[Point]] = ListBuffer(new Edge[Point]());
//      var list = broadcastPointKDTree.value.rangeSearch(point.coord, epsilon.toDouble).asScala
//      (point, list) // list里面存储了所有的邻域点
//    }).filter(_._2.size > minPts).flatMap(scscsc=>scscsc._2).collect().length
//
//    println(ccccccccccc)


    val srcGraph = Graph.fromEdges(edges,10)
    println("num edges = " + srcGraph.numEdges)//查看边的个数 157347
    println("num vertices = " + srcGraph.numVertices)//查看点的个数 9174
    val cc = ConnectedComponents.run(srcGraph)
    val sssssssssssssssss=cc.vertices.map { case (pid, point) => point }.distinct().collect().length

    println("图连通顶点数"+cc.vertices.map { case (pid, point) => point }.collect().length) //9174或者4248
  //  println("图连通不重复顶点数"+cc.vertices.map { case (pid, point) => point }.distinct().collect().length) //9或者66

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

    println("结束时间："+System.currentTimeMillis())
result.repartition(1).saveAsTextFile(outpath)
//     println("结果点数"+result.collect().length)
//
//    val b=points.join(a).map {
//      case (id, (point, a)) => (point.getId,point.getCoord.toList(0), a)
//    }
//    println(a.collect().length)
//    println(b.collect().length)
//    b.collect().foreach(println)
//     val ccTriplets=cc.triplets.map (ct=>{
//       val clusterId=ct.srcAttr
//       (clusterId,ct)
//     })
//    ccTriplets.flatMap

//    cc.vertices.collect().foreach(println)
//    println(cc.vertices.collect().length)
//      cc.edges.foreach(println)
//    println(cc.vertices.map { case (pid, point) => point }.distinct().count())
//      println(cc.vertices.collect().toList.length)
//      println(cc.vertices.collect().toList)
    //    val sss=srcGraph.connectedComponents()
//    sss.vertices.saveAsTextFile(outpath)
//        cc.vertices.saveAsTextFile(outpath)
//    cc.vertices.distinct().repartition(1).saveAsTextFile(outpath)
//    cc.vertices.map { case (pid, point) => point }.distinct().repartition(1).saveAsTextFile(outpath)
    //    cc.vertices.map { case (pid, point) => point }.distinct().saveAsTextFile(outpath)
    //    cc.vertices.foreach(println)
    //    println("Components: " + cc.vertices.map { case (vid, data) => data }.distinct())
    sc.stop()
  }
}
