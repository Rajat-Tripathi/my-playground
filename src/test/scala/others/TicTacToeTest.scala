package others

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

/**
  * Created by Rajat on Sep 16, 2022.
  */

class TicTacToeTest extends AnyWordSpec with Matchers with MockitoSugar with ScalaFutures {

  "TicTacToe" must {

    "findWinner" in {
      val row1   = List(
        Cell(x = 1, y = 1, value = Some(Player.O)),
        Cell(x = 2, y = 1, value = Some(Player.X)),
        Cell(x = 3, y = 1, value = Some(Player.O))
      )
      val row2   = List(
        Cell(x = 1, y = 2, value = Some(Player.X)),
        Cell(x = 2, y = 2, value = Some(Player.O)),
        Cell(x = 3, y = 2, value = Some(Player.O))
      )
      val row3   = List(
        Cell(x = 1, y = 3, value = Some(Player.O)),
        Cell(x = 2, y = 3, value = Some(Player.X)),
        Cell(x = 3, y = 3, value = None)
      )
      val cells  = List(row1, row2, row3).flatten
      val result = TicTacToe.findWinner(cells)
      assert(result.isDefined)
      result.get.announceWinner()
    }

    //    "" in {
    //      val row2   = List(
    //        Cell(x = 1, y = 2, value = Some(Player.X)),
    //        Cell(x = 2, y = 2, value = Some(Player.O)),
    //        Cell(x = 3, y = 2, value = Some(Player.O))
    //      )
    //
    //      println(row2.map(_.output).mkString)
    //    }


  }
}
