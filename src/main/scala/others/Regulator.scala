package others

import org.joda.time.{DateTime, Seconds}

/**
  * Created by Rajat on Sep 20, 2022.
  */

class Regulator(maxPerSecond: Int, maxPerDay: Int) {

  private var invoke: Invoke = Invoke(0, 0, DateTime.now())

  private def checkLimits: Either[String, Boolean] = {
    if (invoke.totalAttempt < maxPerDay) {
      val sec = invoke.getSeconds
      if (sec <= 1 && invoke.perSecAttempt < maxPerSecond) {
        println("here")
        invoke = invoke.incrementNow()
        Right(true)
      } else if (sec <= 1) {
        Left("Max per second quota crossed !!")
      } else {
        invoke = invoke.resetPerSec()
        Right(true)
      }
    } else {
      if (invoke.hasDayPassed) {
        invoke = invoke.resetPerDay()
        Right(true)
      } else {
        Left("Max per day quota crossed !!")
      }
    }
  }

  def regulate[O](fn: => O): O = {
    if (checkLimits.isRight) {
      fn
    } else {
      throw new Exception(checkLimits.left.get)
    }
  }

}

sealed case class Invoke(perSecAttempt: Int, totalAttempt: Int, timeStamp: DateTime) {

  def incrementNow(): Invoke = Invoke(this.perSecAttempt + 1, this.totalAttempt + 1, DateTime.now())

  def resetPerSec(): Invoke = Invoke(1, this.totalAttempt + 1, DateTime.now())

  def resetPerDay(): Invoke = Invoke(this.perSecAttempt + 1, 1, DateTime.now())

  def getSeconds: Int = {
    Seconds.secondsBetween(this.timeStamp, DateTime.now()).getSeconds
  }

  def hasDayPassed: Boolean = {
    this.timeStamp.plusDays(1).withTimeAtStartOfDay().getMillis <= DateTime.now().getMillis
  }
}

object CheckRegulator extends App {

  private def sendEmail(): Unit = {
    println("sending Email ..")
  }

  private val regulator = new Regulator(maxPerSecond = 1, maxPerDay = 5)

  def callEmailService(): Unit = {
    regulator.regulate{
      sendEmail()
    }
  }

  (1 to 10).foreach{ x =>
    Thread.sleep(2000)
    callEmailService()
  }

  Thread.sleep(5000)

}