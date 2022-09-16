package clock

import org.joda.time.LocalTime
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 08, 2022.
  */

class ClockTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "ClockTest" must {

    "calculateDegree" in {
      val time   = LocalTime.now()
      val result = Clock.calculateDegree(time)
      println(time)
      println(result.output())
      println("angle Btw Minute & Seconds Hand = " + result.angleBtwMinuteAndSecondsHand())
      println("angle Btw Hours & Seconds Hand = " + result.angleBtwHourAndSecondHand())
      println("angle Btw Hours & Minutes Hand = " + result.angleBtwHourAndMinuteHand())
    }

    "calculateDegree : case 2" in {
      val time   = LocalTime.parse("12:15:30")
      val result = Clock.calculateDegree(time)
      println(time)
      println(result.output())
      println("angle Btw Minute & Seconds Hand = " + result.angleBtwMinuteAndSecondsHand())
      println("angle Btw Hours & Seconds Hand = " + result.angleBtwHourAndSecondHand())
      println("angle Btw Hours & Minutes Hand = " + result.angleBtwHourAndMinuteHand())
    }

  }
}
