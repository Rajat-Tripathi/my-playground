package map

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 10, 2022.
  */

object MapExtractor {

  def findProperty(completeData: Map[String, Any], keySeq: Seq[String]): Option[Any] = {

    @tailrec
    def findTailRec(data: Map[String, Any], keys: Seq[String]): Option[Any] = {
      val key        = keys.head
      val mayBeValue = data.get(key)
      keys.length match {
        case 1 => mayBeValue
        case _ => mayBeValue match {
          case Some(value) => value match {
            case map: Map[String, Any] => findTailRec(map, keys.tail)
            case _                     =>
              println(s"data for key = '$key' not in nested format !!"); None
          }
          case None        => println(s"key = '$key' not found !!"); None
        }
      }
    }

    if (keySeq.isEmpty) {
      println("keys are empty !!")
      None
    } else {
      findTailRec(completeData, keySeq)
    }
  }


}