package clock

import org.joda.time.LocalTime
import utility.Utility._

/**
  * Created by Rajat on Sep 08, 2022.
  */

object Clock {

  private final val DEGREE_PER_ROTATION : Double = 360
  private final val HOURS_PER_ROTATION  : Double = 12
  private final val MINUTES_PER_ROTATION: Double = 60
  private final val SECONDS_PER_ROTATION: Double = 60

  private val DEGREE_PER_HOUR   : Double = DEGREE_PER_ROTATION / HOURS_PER_ROTATION //30
  private val DEGREE_PER_MINUTE : Double = DEGREE_PER_ROTATION / MINUTES_PER_ROTATION //6
  private val DEGREE_PER_SECONDS: Double = DEGREE_PER_ROTATION / SECONDS_PER_ROTATION //6

  /*
    360 degree of hours hand == 12 hours => 30 degree per hours
    360 degree of minutes hand == 1 hour => 30 degree of hours hand
    1 degree of minutes hand == 30 / 360 degree of hours hand

    360 degree of minutes hand == 60 minutes => 6 degree per minute
    360 degree of seconds hand == 1 minute => 6 degree of minutes hand
    1 degree of seconds hand == 6 / 360 degree of minutes hand
   */

  private val HOUR_TO_MINUTE_MOVEMENT_DEGREE_RATIO   : Double = DEGREE_PER_HOUR / DEGREE_PER_ROTATION //0.083333
  private val MINUTE_TO_SECONDS_MOVEMENT_DEGREE_RATIO: Double = DEGREE_PER_MINUTE / DEGREE_PER_ROTATION //0.016666

  def calculateDegree(time: LocalTime): DegreeWith12 = {
    val hourDegree   = getHourDegree(time)
    val minuteDegree = getMinuteDegree(time)
    val secondDegree = getSecondDegree(time)
    DegreeWith12(
      hourDegree.roundOffDecimal(),
      minuteDegree.roundOffDecimal(),
      secondDegree.roundOffDecimal()
    )
  }

  private def getHourDegree(time: LocalTime): Double = {
    val hours          = time.getHourOfDay
    val hourIn12Format = hours % HOURS_PER_ROTATION //to handle 24 hours format
    val baseDegree     = hourIn12Format * DEGREE_PER_HOUR
    val minutes        = time.getMinuteOfHour
    val minutesDegree  = minutes * DEGREE_PER_MINUTE
    val extraDegree    = minutesDegree * HOUR_TO_MINUTE_MOVEMENT_DEGREE_RATIO
    baseDegree + extraDegree
  }

  private def getMinuteDegree(time: LocalTime): Double = {
    val minutes       = time.getMinuteOfHour
    val baseDegree    = minutes * DEGREE_PER_MINUTE
    val seconds       = time.getSecondOfMinute
    val secondsDegree = seconds * DEGREE_PER_SECONDS
    val extraDegree   = secondsDegree * MINUTE_TO_SECONDS_MOVEMENT_DEGREE_RATIO
    baseDegree + extraDegree
  }

  private def getSecondDegree(time: LocalTime): Double = {
    val seconds    = time.getSecondOfMinute
    val baseDegree = seconds * DEGREE_PER_SECONDS
    baseDegree
  }

}

sealed case class DegreeWith12(hourDegree: Double,
                               minuteDegree: Double,
                               secondDegree: Double) {
  def output(): String = {
    s"""
       | hour degree = $hourDegree
       | minute degree = $minuteDegree
       | second degree = $secondDegree
       |""".stripMargin
  }

  def angleBtwHourAndMinuteHand(): Double = {
    Math.abs(hourDegree - minuteDegree).roundOffDecimal().asAcuteAngle
  }

  def angleBtwHourAndSecondHand(): Double = {
    Math.abs(hourDegree - secondDegree).roundOffDecimal().asAcuteAngle
  }

  def angleBtwMinuteAndSecondsHand(): Double = {
    Math.abs(minuteDegree - secondDegree).roundOffDecimal().asAcuteAngle
  }
}
