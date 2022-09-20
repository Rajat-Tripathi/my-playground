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
    require(cells.length == TOTAL_CELLS,
      s"All and only $TOTAL_CELLS cells input required for $GRID_SIZE X $GRID_SIZE grid !!")
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

  private def showImage(horizontalGroupings: Seq[CellGrouping]): Unit = {
    require(horizontalGroupings.nonEmpty, "No horizontal groupings found !!")
    require(horizontalGroupings.forall(_.cells.nonEmpty), "Empty cell found !!")
    val sortedFromBottomToTop = horizontalGroupings.sortBy(_.cells.head.y)
    println(sortedFromBottomToTop.reverse.map(_.rowFromLeft).mkString)
  }

  def findWinner(cells: List[Cell]): Option[Winner] = {
    validateCells(cells)
    val verticalGroupings        = cells.groupBy(_.x).values.toSeq.map(cells => CellGrouping(cells, "vertical"))
    val horizontalGroupings      = cells.groupBy(_.y).values.toSeq.map(cells => CellGrouping(cells, "horizontal"))
    val upHillDiagonalCells      = cells.filter(_.isUpHillDiagonalCell)
    val upHillDiagonalGrouping   = CellGrouping(upHillDiagonalCells, "uphill diagonal from left")
    val downHillDiagonalCells    = cells.filter(_.isDownHillDiagonalCell(FIRST_CELL_ADDRESS, LAST_CELL_ADDRESS))
    val downHillDiagonalGrouping = CellGrouping(downHillDiagonalCells, "downhill diagonal from left")
    val totalGrouping            = verticalGroupings ++ horizontalGroupings :+ upHillDiagonalGrouping :+ downHillDiagonalGrouping
    showImage(horizontalGroupings)
    scanCellGroupings(totalGrouping)
  }

  private def scanCellGroupings(groupings: Seq[CellGrouping]): Option[Winner] = {
    @tailrec
    def scanTailRec(toScan: Seq[CellGrouping], winner: Option[Winner]): Option[Winner] = {
      if (toScan.isEmpty) {
        winner
      } else {
        val grouping = toScan.head
        if (grouping.cells.forall(_.value.contains(Player.X))) {
          Some(Winner(Player.X, grouping))
        } else if (grouping.cells.forall(_.value.contains(Player.O))) {
          Some(Winner(Player.O, grouping))
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

  def location: String = s"""(x:$x, y:$y)"""

}

final case class CellGrouping(cells: List[Cell], direction: String) {

  def rowFromLeft: String = {
    val line  : String  = "------"
    val isEven: Boolean = line.length % 2 == 0
    val half  : Int     = line.length / 2
    val leftMargin      = " " * (if (isEven) half - 1 else half)
    val rightMargin     = " " * (if (isEven) half else half)
    s"""
       +${List.fill(cells.length)(line).mkString("+")}+
       |${cells.sortBy(_.x).map(x => leftMargin + x.value.getOrElse(" ") + rightMargin).mkString("|")}|
       +${List.fill(cells.length)(line).mkString("+")}+"""
  }

}

final case class Winner(player: Player, grouping: CellGrouping) {

  def announceWinner(): Unit = {
    val sorted = grouping.cells.sortBy(x => (x.x, x.y))
    println{
      s"""
         |${"*" * 70}
         |$player is the winner.
         |Cells from ${sorted.head.location} to ${sorted.last.location} : ${grouping.direction}
         |${"*" * 70}
         |""".stripMargin
    }
  }
}