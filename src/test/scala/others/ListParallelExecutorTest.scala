package others

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by Rajat on Feb 11, 2023.
  */

class ListParallelExecutorTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  private val to: Timeout = Timeout(1.minutes)

  private val list: List[Int] = (1 to 10).toList

  import ListParallelExecutor._

  import scala.concurrent.ExecutionContext.Implicits.global

  "ListParallelExecutorTest" must {

    "parExecute must execute atMost 2 futures at once in parallel" in {
      list.parExecute(2){ x =>
        Future{
          println("Executing for x = " + x)
          Thread.sleep(3000)
          println("Executed for x = " + x)
          //      println("..........")
          x
        }
      }.futureValue(to)
    }


  }
}