package design

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import org.joda.time.{DateTime, Seconds}

import scala.util.{Failure, Success, Try}

/**
  * Created by Rajat on Sep 05, 2022.
  */

object CircuitBreaker {

  import SwitchStatus._

  private def externalCall: Try[Unit] = {
    ???
  }

  private val switch = new AtomicInteger(CLOSED)

  private val failedAttempts = new AtomicReference[Option[FailedAttempt]](None)

  private val circuitOpenTimeStamp = new AtomicReference[Option[DateTime]](None)

  private val circuitOpenCoolingTimeInSec = 5

  private val MaxFailedAttempts: Int = 3

  private val MaxSecondsToWait: Int = 3

  def invoke(): Unit = {
    switch.get() match {
      case CLOSED    => closeState()
      case OPEN      => openState()
      case HALF_OPEN => closeState()

    }
  }

  private def closeState(): Unit = {
    externalCall match {
      case Failure(exception) => {
        val maybeAttempt = failedAttempts.get()
        if (maybeAttempt.isEmpty) {
          failedAttempts.set(Some(FailedAttempt(1)))
        } else if (maybeAttempt.get.canTryMoreAttempt(MaxFailedAttempts, MaxSecondsToWait)) {
          val nextAttempt = maybeAttempt.get.attempt + 1
          failedAttempts.set(Some(FailedAttempt(nextAttempt)))
        } else {
          switch.set(OPEN)
          failedAttempts.set(None)
          circuitOpenTimeStamp.set(Some(DateTime.now))
        }
      }
      case Success(value)     => {
        ???
      }
    }
  }

  private def halfOpenState(): Unit = {
    externalCall match {
      case Failure(exception) => {
        val maybeAttempt = failedAttempts.get()
        if (maybeAttempt.isEmpty) {
          failedAttempts.set(Some(FailedAttempt(1)))
        } else if (maybeAttempt.get.canTryMoreAttempt(MaxFailedAttempts, MaxSecondsToWait)) {
          val nextAttempt = maybeAttempt.get.attempt + 1
          failedAttempts.set(Some(FailedAttempt(nextAttempt)))
        } else {
          switch.set(OPEN)
          failedAttempts.set(None)
        }
      }
      case Success(value)     => {
        switch.set(CLOSED)
        ???
      }
    }
  }

  private def openState(): Unit = {
    if (circuitOpenTimeStamp.get().isEmpty) {
      throw new Exception("Resource not available !!")
    } else {
      val sec = Seconds.secondsBetween(circuitOpenTimeStamp.get().get, DateTime.now()).getSeconds
      if (sec > circuitOpenCoolingTimeInSec) {
        switch.set(HALF_OPEN)
      } else {
        throw new Exception("Resource not available !!")
      }
    }
  }


}

sealed case class FailedAttempt(attempt: Int, timestamp: DateTime = DateTime.now()) {
  def getSecondsElapsed: Int = {
    Seconds.secondsBetween(timestamp, DateTime.now()).getSeconds
  }

  def canTryMoreAttempt(maxAttempts: Int, maxSeconds: Int): Boolean = {
    attempt < maxAttempts && getSecondsElapsed <= maxSeconds
  }
}

private object SwitchStatus {
  final val OPEN     : Int = -1
  final val HALF_OPEN: Int = 0
  final val CLOSED   : Int = 1
}
