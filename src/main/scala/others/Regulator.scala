package others

import java.util.concurrent.atomic.AtomicReference

import org.joda.time.{DateTime, Seconds}

/**
  * Created by Rajat on Sep 20, 2022.
  */

class Regulator(maxPerSecond: Int, maxPerDay: Int) {

  private val counterRef: AtomicReference[Counter] = {
    new AtomicReference[Counter](Counter(0, 0, DateTime.now()))
  }

  /** Checks for quota */
  private def checkForQuota: Either[String, Boolean] = {
    val counter = counterRef.get()
    if (counter.totalAttempt < maxPerDay) {
      val sec = counter.getSeconds
      if (sec <= 1 && counter.perSecAttempt < maxPerSecond) {
        counterRef.set(counter.incrementNow())
        Right(true)
      } else if (sec <= 1) {
        Left("Max attempts per second quota exceeded !!")
      } else {
        counterRef.set(counter.resetPerSecAttempt())
        Right(true)
      }
    } else {
      if (counter.hasDayPassed) {
        counterRef.set(counter.resetPerDayAttempt())
        Right(true)
      } else {
        Left("Max attempts per day quota exceeded !!")
      }
    }
  }

  /** Regulates the invocation based on quota */
  def regulate[O](fn: => O): O = {
    if (checkForQuota.isRight) {
      fn
    } else {
      throw new Exception(checkForQuota.left.get)
    }
  }

}

sealed case class Counter(perSecAttempt: Int, totalAttempt: Int, timeStamp: DateTime) {

  /** Increment attempts count */
  def incrementNow(): Counter = Counter(this.perSecAttempt + 1, this.totalAttempt + 1, DateTime.now())

  /** Resets total attempts count for the second for the next second */
  def resetPerSecAttempt(): Counter = Counter(1, this.totalAttempt + 1, DateTime.now())

  /** Resets total attempts count for the day for the next day */
  def resetPerDayAttempt(): Counter = Counter(this.perSecAttempt + 1, 1, DateTime.now())

  /** Gets seconds between last attempt timeStamp and current timestamp */
  def getSeconds: Int = {
    Seconds.secondsBetween(this.timeStamp, DateTime.now()).getSeconds
  }

  /** Checks if midnight passed */
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
    //    Thread.sleep(2000)
    callEmailService()
  }

  Thread.sleep(5000)

}