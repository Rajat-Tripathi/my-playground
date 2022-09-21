package regulator

import regulator.AttemptRegulator.regulate
import regulator.AttemptRegulatorConf.Implicits.general

/**
  * Created by Rajat on Sep 21, 2022.
  */

object AttemptRegulatorTest extends App {

  private def sendEmail(): Unit = {
    println("sending Email ..")
  }

  def callEmailService(orgCode: String): Unit = regulate(orgCode){
    sendEmail()
  }

  def callEmailService(orgCode: String, others: Boolean): Unit = regulate(orgCode){
    println(others)
    sendEmail()
  }

  val orgCode = "devum"
  (1 to 50).foreach{ x =>
    Thread.sleep(1500)
    callEmailService(orgCode)
    callEmailService(orgCode, true)
  }

  Thread.sleep(5000)

}