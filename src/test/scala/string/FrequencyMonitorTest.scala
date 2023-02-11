package string

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Dec 10, 2022.
  */

class FrequencyMonitorTest extends AnyWordSpec with Matchers with MockitoSugar {

  object MongoCallMonitor extends FrequencyMonitor {
    override def upperLimit: Long = 3

    override def raiseAlarm(): Unit = {
      println("Sent Email")
    }
  }

  def callMongo(dbName: String) = {
    println(s"Calling $dbName Mongo DB ...")
  }

  "MongoCallMonitor" must {

    "monitor" in {
      val dbName = "db-1"
      (1 to 10).toList.foreach{ x =>
        //        Thread.sleep(250)
        //        val dbName = "db-" + x
        MongoCallMonitor.monitor(dbName){
          callMongo(dbName)
        }
      }
    }


  }
}