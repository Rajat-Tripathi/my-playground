package string

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 05, 2022.
  */

class FindWordCountTest extends AnyWordSpec with Matchers with MockitoSugar {

  "FindWordCount" must {
    "case: 1" in {
      val result = FindWordCount.findWordCount("123456", "234")
      assert(result == 1)
    }

    "case: 2" in {
      val result = FindWordCount.findWordCount("1231241", "12")
      assert(result == 2)
    }

    "case: 3" in {
      val result = FindWordCount.findWordCount("abcabcabca", "abc")
      assert(result == 3)
    }
  }

  "ValidateParenthesis" must {
    "case: 1" in {
      val result = FindWordCount.validateParenthesis("(1+1)")
      assert(result)
    }

    "case: 2" in {
      val result = FindWordCount.validateParenthesis("(1+1))")
      assert(!result)
    }

    "case: 3" in {
      val result = FindWordCount.validateParenthesis("((())())")
      assert(result)
    }
  }

  "Split" must {
    "case: 1" in {
      val result = FindWordCount.split("abcdef", "bc")
      assert(result sameElements Array("a", "def"))
    }

    "case: 2" in {
      val result = FindWordCount.split("abcabc", "b")
      assert(result sameElements Array("a", "ca", "c"))
    }

    "case: 3" in {
      val result = FindWordCount.intersect("a1b1", "ab22")
//      val result = FindWordCount.intersect("ab22", "a1")
      println(result)
    }
  }


}
