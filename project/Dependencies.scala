import sbt.librarymanagement.ModuleID

/**
  * Created by Rajat on Aug 05, 2021.
  */

object Dependencies {

  val myPlaygroundTestDependencies: Seq[ModuleID] = {
    import TestLibrary._
    Seq(scalaTest, mockitCore, scalaTestPlusMockito, jodaTime, akkaKit)
  }

}
