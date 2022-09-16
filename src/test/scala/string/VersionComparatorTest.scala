package string

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 04, 2022.
  */

class VersionComparatorTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "VersionComparator" must {

    "VersionComparator should return -1 if v1 is less than v2" in {
      val result = VersionComparator.compare("1.2.3", "1.2")
      assert(result == -1)
    }

    "VersionComparator should return 1 if v1 is greater than v2" in {
      val result = VersionComparator.compare("1.2.3", "1.2")
      assert(result == -1)
    }

  }
}
