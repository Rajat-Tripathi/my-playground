package string

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 04, 2022.
  */

object Rotate {

  def rotate(str: String, step: Int, moveRight: Boolean): String = {
    if (str == null || str.isEmpty) {
      str
    } else {
      val actualStep = step % str.length
      if (moveRight) {
        str.takeRight(actualStep).concat(str.take(str.length - actualStep))
      } else {
        str.takeRight(str.length - actualStep).concat(str.take(actualStep))
      }
    }
  }

  def rotate2(str: String, step: Int, moveRight: Boolean = true): String = {
    val actualStep = if (str == null || str.isEmpty) {
      0
    } else {
      step % str.length
    }

    @tailrec
    def rotateTailRec(currentStep: Int, accumultor: String): String = {
      if (currentStep == actualStep) {
        accumultor
      } else {
        if (moveRight) {
          rotateTailRec(currentStep + 1, accumultor.last.toString.concat(accumultor.init.mkString))
        } else {
          rotateTailRec(currentStep + 1, accumultor.tail.mkString.concat(accumultor.head.toString))
        }
      }
    }

    rotateTailRec(0, str)
  }

  def findPairs(elements: Set[String]): Set[String] = {

    @tailrec
    def findPairsTailRec(head: String, tail: Set[String], acc: Set[String]): Set[String] = {
      if (tail.isEmpty) {
        acc
      } else {
        val combo = tail.map(x => s"$head-$x")
        findPairsTailRec(tail.head, tail.tail, acc.++(combo))
      }
    }

    if (elements.isEmpty) {
      Set.empty
    } else {
      findPairsTailRec(elements.head, elements.tail, Set.empty)
    }
  }

}
