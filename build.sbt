import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.14"

lazy val root = (project in file("."))
  .settings(
    ThisBuild / organization := "Rajat",
    ThisBuild / scalaVersion := "2.12.14",
    name := "my-playground",
    Test / parallelExecution := false,
    Test / testOptions ++= Seq(Tests.Argument("-oT"), Tests.Argument(TestFrameworks.ScalaTest, "-oNCELOPQRM")),
    libraryDependencies ++= Dependencies.myPlaygroundTestDependencies
  )
