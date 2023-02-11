package string

import org.joda.time.{DateTime, Seconds}

import scala.collection.mutable

/**
  * Created by Rajat on Dec 03, 2022.
  */
sealed case class AttemptStats(totalAttempt: Int, timeStamp: DateTime) {

  /** Increments attempt count */
  def increment(): AttemptStats = AttemptStats(this.totalAttempt + 1, timeStamp)

  /** Resets total attempts */
  def reset(): AttemptStats = AttemptStats(1, DateTime.now())

  /** Gets seconds between last attempt timeStamp and current timestamp */
  def getTimeElapsedInSeconds: Int = {
    Seconds.secondsBetween(this.timeStamp, DateTime.now()).getSeconds
  }

}

trait FrequencyMonitor {

  //  private lazy val counterRef: AtomicReference[Option[AttemptStats]] = {
  //    new AtomicReference[Option[AttemptStats]](None)
  //  }

  private lazy val counterRef: mutable.Map[String, AttemptStats] = mutable.Map()

  private lazy val alarmSent: mutable.Map[String, Boolean] = mutable.Map()

  def upperLimit: Long

  def raiseAlarm(): Unit

  private def raiseAlarmInternal(key: String): Unit = {
    val maybeBoolean = alarmSent.get(key)
    if (maybeBoolean.isEmpty) {
      alarmSent.update(key, true)
      raiseAlarm()
    } else {
      if (maybeBoolean.get) {
        ()
      } else {
        alarmSent.update(key, true)
        raiseAlarm()
      }
    }
  }

  def monitor[O](key: String)(fn: => O): O = this.synchronized{
    val maybeStats = counterRef.get(key)
    if (maybeStats.isEmpty) {
      counterRef.update(key, AttemptStats(1, DateTime.now()))
    } else {
      val value = maybeStats.get
      val sec   = value.getTimeElapsedInSeconds
      if (sec < 1 && value.totalAttempt >= upperLimit) {
        raiseAlarmInternal(key)
      } else if (sec < 1) {
        counterRef.update(key, value.increment())
      } else {
        counterRef.update(key, AttemptStats(1, DateTime.now()))
        alarmSent.remove(key)
      }
    }
    fn
  }

}