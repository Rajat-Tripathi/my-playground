package others

import scala.annotation.tailrec

/**
  * Created by Rajat on Sep 16, 2022.
  */

object TicTacToe {

  private val FIRST_CELL_ADDRESS: Int = 1
  private val GRID_SIZE         : Int = 3
  private val LAST_CELL_ADDRESS : Int = GRID_SIZE + FIRST_CELL_ADDRESS - 1
  private val TOTAL_CELLS       : Int = GRID_SIZE * GRID_SIZE

  private def validateCells(cells: List[Cell]): Unit = {
    require(cells.length == TOTAL_CELLS, s"All and only $TOTAL_CELLS cells input required for $GRID_SIZE X $GRID_SIZE grid !!")
    cells.foreach{ cell =>
      if (cell.x < FIRST_CELL_ADDRESS
          || cell.x > LAST_CELL_ADDRESS
          || cell.y < FIRST_CELL_ADDRESS
          || cell.y > LAST_CELL_ADDRESS) {
        throw new Exception(
          s"Cell address has to be between $FIRST_CELL_ADDRESS and $LAST_CELL_ADDRESS !!"
        )
      }
    }
  }

  def findWinner(cells: List[Cell]): Option[Winner] = {
    validateCells(cells)
    val verticalCells  = cells.groupBy(_.x).values.toSeq
    val verticalWinner = scan(verticalCells)
    if (verticalWinner.isDefined) {
      println("Success in vertical")
      verticalWinner
    } else {
      val horizontalCells  = cells.groupBy(_.y).values.toSeq
      val horizontalWinner = scan(horizontalCells)
      if (horizontalWinner.isDefined) {
        println("Success in horizontal")
        horizontalWinner
      } else {
        val upHillDiagonalCells   = cells.filter(_.isUpHillDiagonalCell)
        val downHillDiagonalCells = cells.filter(_.isDownHillDiagonalCell(FIRST_CELL_ADDRESS, LAST_CELL_ADDRESS))
        val diagonalCells         = Seq(upHillDiagonalCells, downHillDiagonalCells)
        val diagonalWinner        = scan(diagonalCells)
        if (diagonalWinner.isDefined) {
          println("Success in diagonal")
        }
        diagonalWinner
      }
    }
  }

  private def scan(groupings: Seq[List[Cell]]): Option[Winner] = {
    @tailrec
    def scanTailRec(toScan: Seq[List[Cell]], winner: Option[Winner]): Option[Winner] = {
      if (toScan.isEmpty) winner
      else {
        val list = toScan.head
        if (list.forall(_.value.contains(Player.X))) {
          Some(Winner(Player.X, list))
        } else if (list.forall(_.value.contains(Player.O))) {
          Some(Winner(Player.O, list))
        } else {
          scanTailRec(toScan.tail, winner)
        }
      }
    }

    scanTailRec(groupings, None)
  }

}

sealed abstract class Player

object Player {
  case object X extends Player

  case object O extends Player
}

final case class Cell(x: Int, y: Int, value: Option[Player] = None) {

  def isUpHillDiagonalCell: Boolean = x == y

  def isDownHillDiagonalCell(firstCellAddress: Int, lastCellAddress: Int): Boolean = {
    x + y == (firstCellAddress + lastCellAddress)
  }

  def location: String = s"""(x : $x, y : $y)"""

}

final case class Winner(player: Player, cells: List[Cell]) {
  def announceWinner(): Unit = {
    val sorted = cells.sortBy(x => (x.x, x.y))
    println{
      s"""
         |${"*" * 30}
         |$player is the winner.
         |Cells from ${sorted.head.location} to ${sorted.last.location}
         |${"*" * 30}
         |""".stripMargin
    }
  }
}