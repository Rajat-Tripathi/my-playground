package string

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 04, 2022.
  */

object RansomNote {

  def ransomNote(newspaper: String, msg: String): Boolean = {
    @tailrec
    def ransomNoteTailRec(leftMsg: String, status: Map[Char, Int]): Boolean = {
      if (leftMsg == null || leftMsg.isEmpty) true
      else {
        val letter   = leftMsg.head
        val maybeInt = status.get(letter)
        if (maybeInt.isDefined) {
          val currentCount  = maybeInt.get
          val updatedStatus = {
            if (currentCount == 1) status.-(letter)
            else
              status.updated(letter, currentCount - 1)
          }
          ransomNoteTailRec(leftMsg.tail, updatedStatus)
        } else {
          false
        }
      }
    }

    val newspaperStatus = if (newspaper == null) {
      Map.empty[Char, Int]
    } else {
      newspaper.groupBy(identity).map{ case (char, string) => (char, string.length) }
    }
    ransomNoteTailRec(msg, newspaperStatus)
  }

}