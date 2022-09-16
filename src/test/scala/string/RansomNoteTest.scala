package string

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 04, 2022.
  */

class RansomNoteTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "RansomNote" must {

    "RansomNote should return true if letters present" in {
      val result = RansomNote.ransomNote("12345", "msg")
      assert(result)
    }

    "RansomNote should return false if letters present" in {
      val result = RansomNote.ransomNote("12345", "msg")
      assert(result)
    }

    "RansomNote should return true if newspaper is empty" in {
      val result = RansomNote.ransomNote("12345", "msg")
      assert(result)
    }


  }
}
