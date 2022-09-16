package string

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 04, 2022.
  */

class RotateTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "Rotate" must {

    "Rotate should rote right 2 steps" in {
      val result = Rotate.rotate("12345", 2, moveRight = true)
      assert(result == "34512")
    }

    "Rotate should rote right 3 steps" in {
      val result = Rotate.rotate("12345", 2, moveRight = true)
      assert(result == "34512")
    }

    "Rotate should rote right 5 steps" in {
      val result = Rotate.rotate("12345", 2, moveRight = true)
      assert(result == "34512")
    }

  }

  "FindPairs" must {

    "case- 1" in {
      val result = Rotate.findPairs(Set("A", "B"))
      println("result = " + result)
      assert(result == Set("A-B"))
    }

    "case- 2" in {
      val result = Rotate.findPairs(Set("A", "B", "C"))
      println("result = " + result)
      assert(result == Set("A-B", "A-C", "B-C"))
    }

    "case- 3" in {
      val result = Rotate.findPairs(Set("A", "B", "C", "D"))
      println("result = " + result)
      assert(result == Set("A-B", "A-C", "A-D"))
    }

  }
}
