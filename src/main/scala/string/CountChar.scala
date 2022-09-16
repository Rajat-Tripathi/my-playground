package string

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 04, 2022.
  */

object CountChar {

  def countChar(str: String): Seq[(Char, Int)] = {

    @tailrec
    def countCharTailRec(string: String, current: Char, count: Int, acc: Seq[(Char, Int)]): Seq[(Char, Int)] = {
      if (string.isEmpty) {
        acc :+ ((current, count))
      } else if (string.head == current) {
        countCharTailRec(string.tail, current, count + 1, acc)
      } else {
        countCharTailRec(string.tail, string.head, 1, acc :+ ((current, count)))
      }
    }

    if (str == null || str.isEmpty) {
      Nil
    } else {
      countCharTailRec(str.tail, str.head, 1, Nil)
    }
  }

  def longestCommonPrefix(strings: Seq[String]): Option[String] = {

    @tailrec
    def commonPrefixTailRec(str: String, acc: Option[String]): Option[String] = {
      //      println(s" str = $str , acc = $acc ")
      if (acc.isEmpty && str.isEmpty) {
        None
      } else if (acc.isEmpty) {
        val isCommon = strings.forall(_.startsWith(str.head.toString))
        if (isCommon) {
          commonPrefixTailRec(str.tail, Option(str.head.toString))
        } else {
          acc
        }
      } else if (str.isEmpty) {
        acc
      } else {
        val isCommon = strings.forall(_.startsWith(acc.get))
        if (isCommon) {
          commonPrefixTailRec(str.tail, Option(acc.get.concat(str.head.toString)))
        } else {
          acc
        }
      }
    }


    if (strings.isEmpty) {
      None
    } else {
      val smallest = strings.minBy(_.length)
      commonPrefixTailRec(smallest, None)
    }
  }

}
