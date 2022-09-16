package string

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 04, 2022.
  */

class CountCharTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "CountChar" must {

    "case-1" in {
      val result = CountChar.countChar("aabbcccaaa")
      assert(result == Seq(('a', 2), ('b', 2), ('c', 3), ('a', 3)))
    }

    "case-2" in {
      val result = CountChar.countChar("abc")
      assert(result == Seq(('a', 1), ('b', 1), ('c', 1)))
    }

    "case-3" in {
      val result = CountChar.countChar("a")
      assert(result == Seq(('a', 1)))
    }

    "case-4" in {
      val result = CountChar.countChar("aa")
      assert(result == Seq(('a', 2)))
    }

  }

  "longestCommonPrefix" must {

    "case-1" in {
      val se     = Seq("abc", "ab")
      val result = CountChar.longestCommonPrefix(se)
      assert(result.contains("ab"))
    }

    "case-2" in {
      val se     = Seq("a", "b", "ab")
      val result = CountChar.longestCommonPrefix(se)
      assert(result.isEmpty)
    }

    "case-3" in {
      val se     = Seq("a", "ab", "abc")
      val result = CountChar.longestCommonPrefix(se)
      assert(result.contains("a"))
    }

    "case-4" in {
      val se     = Seq("abc", "bcd", "efg")
      val result = CountChar.longestCommonPrefix(se)
      assert(result.isEmpty)
    }


  }
}