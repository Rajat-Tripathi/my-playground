package map

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 10, 2022.
  */

class MapExtractorTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "MapExtractorTest" must {

    "findProperty case 1" in {
      val address = Map("flat" -> "101 A", "floor" -> 1, "pin" -> 12345)
      val data    = Map("name" -> "Rajat", "address" -> address)
      val result  = MapExtractor.findProperty(data, Seq("name"))
      assert(result.contains("Rajat"))
    }

    "findProperty case 2" in {
      val address = Map("flat" -> "101 A", "floor" -> 1, "pin" -> 12345)
      val data    = Map("name" -> "Rajat", "address" -> address)
      val result  = MapExtractor.findProperty(data, Seq("address", "floor"))
      assert(result.contains("Rajat"))
    }

    "findProperty case 3" in {
      val india   = Map("name" -> "India", "currency" -> "INR", "timeZone" -> "UTC+05:30", "callingCode" -> "+91")
      val address = Map("flat" -> "101 A", "floor" -> 1, "country" -> india)
      val data    = Map("name" -> "Rajat", "address" -> address)
      val result  = MapExtractor.findProperty(data, Seq("address", "country", "name"))
      assert(result.contains("Rajat"))
    }

    "findProperty case 4" in {
      val india   = Map("name" -> "India", "currency" -> "INR", "timeZone" -> "UTC+05:30", "callingCode" -> "+91")
      val address = Map("flat" -> "101 A", "floor" -> 1, "country" -> india)
      val data    = Map("name" -> "Rajat", "address" -> address)
      val result  = MapExtractor.findProperty(data, Seq("address", "abc", "name"))
      assert(result.isEmpty)
    }

  }

}
