package org.zzy.dbscan.scala.utils

import org.apache.spark.ml.linalg._
class DBSCANPoint(val vector: Vector) extends Serializable {
  def x=vector(1)
  def y=vector(2)
  // 距离的平方
  def distanceSquared(other: DBSCANPoint): Double = {
    val dx = other.x - x
    val dy = other.y - y
    (dx * dx) + (dy * dy)
  }
  override def toString = s"DBSCANPoint($x, $y)"
}
