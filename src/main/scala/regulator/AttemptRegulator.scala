package regulator

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import org.joda.time.{DateTime, Seconds}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
  * Created by Rajat on Sep 20, 2022.
  */

/**
  * To control no. of attempt to wait for time period between subsequent max attempts.
  * @param maxAttempt : Max no. of attempt between subsequent duration.
  * @param waitDuration : Time period to wait between subsequent max attempts.
  */
sealed class AttemptRegulator(maxAttempt: Int, waitDuration: FiniteDuration) {

  def this(maxAttempt: Int, length: Long, unit: TimeUnit) = {
    this(maxAttempt, FiniteDuration(length, unit))
  }

  private val counterRef: AtomicReference[Attempt] = {
    new AtomicReference[Attempt](Attempt(0, DateTime.now()))
  }

  /** Checks for quota */
  private def checkForQuota: Either[String, Boolean] = this.synchronized{
    val counter = counterRef.get()
    if (counter.totalAttempt < maxAttempt) {
      counterRef.set(counter.increment())
      Right(true)
    } else {
      if (counter.getTimeElapsedInSeconds >= waitDuration.toSeconds) {
        counterRef.set(counter.reset())
        Right(true)
      } else {
        Left(s"Please wait for ${waitDuration.toString()} !!")
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

sealed case class Attempt(totalAttempt: Int, timeStamp: DateTime) {

  /** Increments attempt count */
  def increment(): Attempt = Attempt(this.totalAttempt + 1, DateTime.now())

  /** Resets total attempts */
  def reset(): Attempt = Attempt(1, DateTime.now())

  /** Gets seconds between last attempt timeStamp and current timestamp */
  def getTimeElapsedInSeconds: Int = {
    Seconds.secondsBetween(this.timeStamp, DateTime.now()).getSeconds
  }

}

object AttemptRegulator {

  private val registry: mutable.Map[String, AttemptRegulator] = mutable.Map.empty

  private def getCompileTimeKey(stackTraceElements: Array[StackTraceElement]): String = {
    if (stackTraceElements.length >= 3) {
      val ste = stackTraceElements(2)
      ste.getClassName + "_" + ste.getMethodName + "_" + ste.getLineNumber
    } else {
      throw new Exception("Could not get stack trace elements !!")
    }
  }

  private def getAttemptRegulator[O](key: String)(implicit conf: Conf): AttemptRegulator = this.synchronized{
    registry.getOrElse(key, {
      val regulator = new AttemptRegulator(conf.maxAttempt, conf.waitDuration)
      println(s"Created new AttemptRegulator with key = $key")
      registry.put(key, regulator)
      regulator
    })
  }

  /** To regulate per org level */
  def regulate[O](orgCode: String)(fn: => O)(implicit conf: Conf): O = {
    val stackTraceElements     = Thread.currentThread.getStackTrace
    val compileTimeKey: String = getCompileTimeKey(stackTraceElements)
    val key           : String = orgCode + "_" + compileTimeKey
    getAttemptRegulator(key).regulate(fn)
  }

  /** To regulate globally */
  def regulate[O](fn: => O)(implicit conf: Conf): O = {
    val stackTraceElements     = Thread.currentThread.getStackTrace
    val compileTimeKey: String = getCompileTimeKey(stackTraceElements)
    getAttemptRegulator(compileTimeKey).regulate(fn)
  }

}