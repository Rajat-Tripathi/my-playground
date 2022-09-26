package utility

/**
  * Created By Rajat on 25-Nov-2019.
  */

object Utility {

  def handleDivideByZero(numerator: Double, denominator: Double, isRoundOff: Boolean = true): Double = {
    val result = if (denominator == 0D && !(numerator == 0D)) {
      0D
    } else {
      if ((numerator / denominator).isNaN || (numerator / denominator).isInfinity) {
        0D
      } else {
        numerator / denominator
      }
    }
    if (isRoundOff) {
      BigDecimal(result).setScale(3, BigDecimal.RoundingMode.DOWN).toDouble
    } else {
      result
    }
  }

  implicit class DoubleExtension(value: Double) {
    def roundOffDecimal(precision: Int = 2): Double = {
      BigDecimal(value).setScale(precision, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

    def asAcuteAngle: Double = {
      val within360 = Math.abs(value) % 360
      within360 match {
        case angle if angle >= 0D && angle <= 180D  => angle
        case angle if angle > 180D && angle <= 360D => 360 - angle
        case _                                      => within360
      }
    }
  }

  def getCompileTimeKey(stackTraceElements: Array[StackTraceElement]): String = {
    if (stackTraceElements.length >= 3) {
      val ste = stackTraceElements(2)
      ste.getClassName + "_" + ste.getMethodName + "_" + ste.getLineNumber
    } else {
      throw new Exception("Could not get stack trace elements !!")
    }
  }

}