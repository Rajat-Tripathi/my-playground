package others

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
class AttemptRegulator(maxAttempt: Int, waitDuration: FiniteDuration) {

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

  private val regulatorMap: mutable.Map[String, AttemptRegulator] = mutable.Map.empty

  def regulate[O](orgCode: String)(fn: => O)(implicit maxAttempt: Int, waitDuration: FiniteDuration): O = {
    val stackTraceElement  = Thread.currentThread.getStackTrace()(2)
    val methodName: String = stackTraceElement.getMethodName
    val className : String = stackTraceElement.getClassName
    val lineNumber: Int    = stackTraceElement.getLineNumber
    val key       : String = orgCode + "_" + className + "_" + methodName + "_" + lineNumber
    regulatorMap.getOrElse(key, {
      val regulator = new AttemptRegulator(maxAttempt, waitDuration)
      println(s"Created new AttemptRegulator with key = $key")
      regulatorMap.put(key, regulator)
      regulator
    }).regulate{
      fn
    }
  }

}

object CheckAttemptRegulator extends App {

  private def sendEmail(): Unit = {
    println("sending Email ..")
  }

  //  private val regulator = new AttemptRegulator(1, 10, TimeUnit.SECONDS)


  import AttemptRegulator._

  implicit val maxAttempt  : Int            = 1
  implicit val waitDuration: FiniteDuration = FiniteDuration(10, TimeUnit.SECONDS)

  def callEmailService(orgCode: String): Unit = regulate(orgCode){
    sendEmail()
  }

  def callEmailService2(orgCode: String): Unit = regulate(orgCode){
    sendEmail()
  }

  val orgCode = "devum"
  (1 to 50).foreach{ x =>
    Thread.sleep(1500)
    callEmailService(orgCode)
    callEmailService2(orgCode)
  }

  Thread.sleep(5000)

}