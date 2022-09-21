package regulator

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

/**
  * Created by Rajat on Sep 21, 2022.
  */

trait AttemptRegulatorConf {
  def maxAttempt: Int

  def waitDuration: FiniteDuration
}

object AttemptRegulatorConf {
  object Implicits {
    implicit lazy val general: Conf = new Conf {
      override lazy val maxAttempt  : Int            = 1
      override lazy val waitDuration: FiniteDuration = FiniteDuration(10, TimeUnit.SECONDS)
    }
  }
}