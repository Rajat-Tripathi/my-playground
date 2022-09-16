package others

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 15, 2022.
  */

class GeometryTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "Geometry" must {
    "spiralCoordinates" in {
      val radius          : Double = 2
      val rotationAngle   : Double = 270
      val pitch           : Double = 0.1
      val gpsIntervalAngle: Double = 5
      val result                   = Geometry.spiralCoordinates(radius, rotationAngle, pitch, gpsIntervalAngle)
      println(result)
    }


  }
}