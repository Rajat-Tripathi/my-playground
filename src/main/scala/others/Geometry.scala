package others

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 15, 2022.
  */

object Geometry {

  /**
    *  Center of  spiral always lies on origin.
    *
    * @param radius : Radius of spiral.
    * @param rotationAngle : Total Rotation angle spiral.
    * @param pitch : Z-axis movement per degree of spiral rotation.
    * @param gpsIntervalAngle : Angle difference between successive GPS points.
    * @return
    */
  def spiralCoordinates(radius: Double, rotationAngle: Double, pitch: Double, gpsIntervalAngle: Double): List[GPS] = {

    @tailrec
    def spiralTailRec(angle: Double, acc: List[GPS]): List[GPS] = {
      if (angle >= rotationAngle) {
        acc
      } else {
        val newAngle = angle + gpsIntervalAngle
        val x        = radius * Math.cos(newAngle)
        val y        = radius * Math.sin(newAngle)
        val z        = angle * pitch
        spiralTailRec(newAngle, acc :+ GPS(x, y, z))
      }
    }

    spiralTailRec(0D, Nil)
  }

}

sealed case class GPS(x: Double, y: Double, z: Double)