import sbt._

/**
  * Created by Rajat on Aug 05, 2021.
  */

object TestLibrary {
  val scalaTestVer          = "3.2.9"
  val scalaTestPlusMockitoVersion = "3.1.1.0"
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVer % "test"
  val mockitCore = "org.mockito" % "mockito-core" % "3.12.1" % Test
  val jodaTime = "joda-time" % "joda-time" % "2.10.10"
  val scalaTestPlusMockito        = "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test
}
