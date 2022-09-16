package string

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 05, 2022.
  */

object FindWordCount {

  /**
    * Find the no. of repetition of word in a string
    */
  def findWordCount(string: String, word: String): Int = {

    @tailrec
    def findWordTailRec(str: String, count: Int): Int = {
      if (str.isEmpty) count
      else if (str.startsWith(word)) {
        findWordTailRec(str.drop(word.length), count + 1)
      } else findWordTailRec(str.tail, count)
    }

    if (string == null) {
      0
    } else {
      findWordTailRec(string, 0)
    }
  }


  /**
    * Validate of all Parenthesis are closed or not
    */
  def validateParenthesis(expression: String): Boolean = {

    @tailrec
    def validateParenthesisTailRec(exp: String, open: Int, close: Int): Boolean = {
      if (exp.isEmpty) {
        open == close
      } else if (exp.head == '(') {
        validateParenthesisTailRec(exp.tail, open + 1, close)
      } else if (exp.head == ')') {
        validateParenthesisTailRec(exp.tail, open, close + 1)
      } else {
        validateParenthesisTailRec(exp.tail, open, close)
      }
    }

    if (expression == null) {
      true
    } else {
      validateParenthesisTailRec(expression, 0, 0)
    }
  }

  /**
    * Split a string by a word
    */
  def split(string: String, splitBy: String): Array[String] = {

    @tailrec
    def splitTailRec(str: String, word: String, acc: Array[String]): Array[String] = {
      // println(s"str = $str , word = $word , acc =  ${acc.mkString("(", ", ", ")")}")
      if (str.isEmpty && word.isEmpty) acc
      else if (str.isEmpty) acc :+ word
           else if (str.startsWith(splitBy)) {
             splitTailRec(str.drop(splitBy.length), "", acc :+ word)
           } else {
             splitTailRec(str.tail, word.concat(str.head.toString), acc)
           }
    }

    if (string == null) {
      Array.empty[String]
    } else {
      splitTailRec(string, "", Array.empty[String])
    }
  }

  /**
    * intersect a string by a word
    */
  def intersect(string1: String, string2: String): String = {

    @tailrec
    def intersectTailRec(str: String, acc: String): String = {
      if (str.isEmpty) acc
      else if (string2.contains(str.head.toString)) {
        intersectTailRec(str.tail, acc.concat(str.head.toString))
      } else intersectTailRec(str.tail, acc)
    }

    if (string1 == null || string2 == null) {
      ""
    } else {
      intersectTailRec(string1, "")
    }
  }

}
