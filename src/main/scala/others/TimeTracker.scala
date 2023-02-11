package others

import org.joda.time.format.PeriodFormat
import org.joda.time.{DateTime, Period, PeriodType}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by Rajat on Dec 16, 2022.
  */

object TimeTracker {

  private def getDuration(start: DateTime, end: DateTime): String = {
    val period = new Period(start, end, PeriodType.millis())
    PeriodFormat.getDefault.print(period.normalizedStandard())
  }

  private def getParams(params: Seq[(String, Any)]): String = {
    if (params.toList.nonEmpty) {
      "for " + params.toList.map{ case (k, v) => s"'$k' = $v" }.mkString(" , ")
    } else {
      ""
    }
  }

  private def isWithinTimeFrame(start: DateTime,
                                end: DateTime,
                                greaterThan: Option[Duration],
                                lesserThan: Option[Duration]): Boolean = {
    val millis = new Period(start, end, PeriodType.millis()).getMillis
    (greaterThan, lesserThan) match {
      case (Some(gt), Some(lt)) =>
        require(gt.<(lt), "greaterThan must be less than lesserThan Duration !!")
        millis >= gt.toMillis && millis <= lt.toMillis
      case (Some(gt), None)     => millis >= gt.toMillis
      case (None, Some(lt))     => millis <= lt.toMillis
      case (None, None)         => true
    }
  }

  /** To log time taken by a block to execute asynchronously */
  final def trackFuture[T](name: String, params: (String, Any)*)(fn: => Future[T])(implicit executor: ExecutionContext): Future[T] = {
    trackFuture[T](name, None, None, params: _*)(fn)
  }

  /** To log time taken by a block to execute */
  final def track[T](name: String, params: (String, Any)*)(fn: => T): T = {
    track[T](name, None, None, params: _*)(fn)
  }

  /** To log time taken by a block to execute asynchronously, only if executed within given TimeFrame duration */
  final def trackFuture[T](name: String,
                           greaterThan: Option[Duration],
                           lesserThan: Option[Duration],
                           params: (String, Any)*,
                          )(fn: => Future[T])(implicit executor: ExecutionContext): Future[T] = {
    val start = DateTime.now()
    fn.transform[T]({ s: T =>
      val end = DateTime.now()
      if (isWithinTimeFrame(start, end, greaterThan, lesserThan)) {
        val duration = getDuration(start, end)
        println(s"'$name' took $duration ${getParams(params)}")
      }
      s
    }, { ex: Throwable =>
      val end = DateTime.now()
      if (isWithinTimeFrame(start, end, greaterThan, lesserThan)) {
        val duration = getDuration(start, end)
        println(s"'$name' took $duration to fail ${getParams(params)} with msg = ${ex.getMessage}")
      }
      ex
    })
  }

  /** To log time taken by a block to execute, only if executed within given TimeFrame duration */
  final def track[T](name: String,
                     greaterThan: Option[Duration],
                     lesserThan: Option[Duration],
                     params: (String, Any)*,
                    )(fn: => T): T = {
    val start = DateTime.now()
    Try[T]{
      fn
    } match {
      case Success(value) =>
        val end = DateTime.now()
        if (isWithinTimeFrame(start, end, greaterThan, lesserThan)) {
          val duration = getDuration(start, end)
          println(s"'$name' took $duration ${getParams(params)}")
        }
        value

      case Failure(ex) =>
        val end = DateTime.now()
        if (isWithinTimeFrame(start, end, greaterThan, lesserThan)) {
          val duration = getDuration(start, end)
          println(s"'$name' took $duration to fail ${getParams(params)} with msg = ${ex.getMessage}")
        }
        throw ex
    }
  }

}