package others

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Rajat on Feb 11, 2023.
  */

object ListParallelExecutor {

  implicit class LisMap[A](list: List[A]) {

    def parExecute[R](n: Int)(fn: A => Future[R])(implicit executor: ExecutionContext): Future[List[R]] = {

      def parExecuteInternal(grouped: List[List[A]], acc: List[R]): Future[List[R]] = {
        if (grouped.nonEmpty) {
          Future.sequence{
            grouped.head.map(ele => fn(ele))
          }.flatMap{ res =>
            parExecuteInternal(grouped.tail, acc ::: res)
          }
        } else {
          Future.successful(acc)
        }
      }

      parExecuteInternal(list.grouped(n).toList, Nil)
    }

  }

}
