package string

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 04, 2022.
  */

object VersionComparator {

  def compare(version1: String, version2: String): Int = {

    @tailrec
    def compareTailrec(v1: List[Int], v2: List[Int]): Int = {
      if (v1.isEmpty && v2.isEmpty) 0
      else if (v1.isEmpty) -1
           else if (v2.isEmpty) 1
                else {
                  if (v1.head < v2.head) -1
                  else if (v1.head > v2.head) 1
                       else compareTailrec(v1.tail, v2.tail)
                }
    }
    //1.2.4 < 1.3.2

    val v1 = version1.split("\\.").toList.map(_.toInt)
    val v2 = version2.split("\\.").toList.map(_.toInt)
    compareTailrec(v1, v2)
  }

}
