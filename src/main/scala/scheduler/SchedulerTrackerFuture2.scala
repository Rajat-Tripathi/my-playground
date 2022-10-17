package scheduler

import akka.actor.{Cancellable, Scheduler}
import scheduler.ActorSystemImplicits.system

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by Rajat on Apr 08, 2022.
  */

sealed class SchedulerTrackerFuture2(name: String,
                                     initialDelay: FiniteDuration,
                                     interval: FiniteDuration) {
  /** Registry to keep cancellable in memory */
  private final val registry: mutable.Map[String, Cancellable] = mutable.Map.empty

  /** A scheduler for running asynchronous tasks after some deadline in the future */
  private[this] def scheduler: Scheduler = system.scheduler

  /** Creates and add the Cancellable to the registry with the given key */
  final def createScheduler[T](key: String)(task: => Future[T],
                                            cancelIf: T => Boolean,
                                            postCancel: => Unit): Unit = this.synchronized{
    if (registry.contains(key)) {
      println(s" $name : Scheduler already active with key = $key")
    } else {
      registry.put(key, scheduler.scheduleOnce(initialDelay)(invokeFuture(key)(task, cancelIf, postCancel)))
      println(s" $name : Scheduler started with key = $key")
    }
  }

  /** Waits for Future completion */
  private def invokeFuture[T](key: String)(task: => Future[T],
                                           cancelIf: T => Boolean,
                                           postCancel: => Unit): Unit = {
    task.onComplete({
      case Success(value) =>
        if (cancelIf(value)) {
          cancelScheduler(key)
          println("Performing post cancel action ...")
          postCancel
        } else {
          waitForInterval(key)(task, cancelIf, postCancel)
        }
      case Failure(ex)    =>
        println(s" $name : Scheduler failing with msg = ${ex.getMessage}")
        waitForInterval(key)(task, cancelIf, postCancel)
    })
  }

  /** Waits for given interval */
  private def waitForInterval[T](key: String)(task: => Future[T],
                                              cancelIf: T => Boolean,
                                              postCancel: => Unit): Unit = registry.synchronized{
    registry.get(key).foreach{ cancellable =>
      cancellable.cancel()
      registry.put(key, scheduler.scheduleOnce(interval)(invokeFuture(key)(task, cancelIf, postCancel)))
    }
  }

  /** Cancels the Cancellable from registry with the given key */
  private def cancelScheduler(key: String): Unit = this.synchronized{
    registry.get(key).foreach{ cancellable =>
      cancellable.cancel()
      if (cancellable.isCancelled) {
        println(s" $name : Scheduler cancelled for key = $key")
      } else {
        println(s" $name : Scheduler couldn't be cancelled with key = $key")
      }
      registry.remove(key)
    }
  }

  /** Cancels all Cancellable in the registry */
  final def cancelAllSchedulers(): Boolean = this.synchronized{
    println(s" $name : Cancelling all Schedulers ..")
    registry.values.forall(_.cancel())
  }

  //used in test cases
  final def validateIfNoActiveScheduler(): Boolean = this.synchronized{
    registry.isEmpty || registry.values.forall(_.isCancelled)
  }

}


//object TestHi extends App {
//
//  object TestScheduler2 extends SchedulerTrackerFuture2(name = "Test Scheduler", initialDelay = 3.seconds, interval = 3.seconds)
//
//  import com.reactore.core.entitybase.CountContainer
//  import org.joda.time.DateTime
//
//  import scala.util.Random
//
//  TestScheduler2.createScheduler[CountContainer]("myKey")({
//    Future{
//      println("Future started " + DateTime.now().getSecondOfMinute)
//      Thread.sleep(1000)
//      val result = CountContainer(Random.nextInt(11))
//      println(result)
//      println("Future ended " + DateTime.now().getSecondOfMinute)
//      result
//    }
//  }, container => container.count > 5, {
//    println("sending email")
//  })
//
//
//  Thread.sleep(40000)
//}