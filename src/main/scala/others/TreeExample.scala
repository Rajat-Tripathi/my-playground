package others

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by Rajat on Apr 06, 2023.
  */


class Node[T](data: T, var next: Node[T]) {

  def appendToTail(d: T): Unit = {
    var n = this
    while (n.next != null) {
      n = n.next
    }
    n.next = Node(d, null) //last node
  }

  override def toString: String = {
    val buffer = new ListBuffer[T]
    buffer.append(this.data)
    var n = this
    while (n.next != null) {
      n = n.next
      // println("adding " + n.data + " next = " + n.next)
      buffer.append(n.data)
    }
    buffer.mkString("[", " -> ", "]")
  }

  def toString2: String = {

    @tailrec
    def sds(n: Node[T], dataSet: List[T]): String = {
      if (n.next != null) {
        sds(n.next, dataSet :+ n.data)
      } else {
        (dataSet :+ n.data).mkString("[", " -> ", "]")
      }
    }

    sds(this, Nil)
  }


}

object Node {
  def head[T](d: T): Node[T] = new Node(d, null)
}

object Test extends App {

  val head = Node.head(1)
  head.appendToTail(2)
  head.appendToTail(3)
  head.appendToTail(4)
  head.appendToTail(5)
  println(head)

  //  def printKthToLast(n : Node, k : Int)={
  //
  //  }
}