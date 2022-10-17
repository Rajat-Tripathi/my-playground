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

sealed class SchedulerTrackerFuture(name: String,
                                    initialDelay: FiniteDuration,
                                    interval: FiniteDuration) {
  /** Registry to keep cancellable in memory */
  private final val registry: mutable.Map[String, Cancellable] = mutable.Map.empty

  /** A scheduler for running asynchronous tasks after some deadline in the future */
  private[this] def scheduler: Scheduler = system.scheduler

  /** Creates and add the Cancellable to the registry with the given key */
  final def createScheduler[T](key: String)(task: => Future[T]): Unit = this.synchronized{
    if (registry.contains(key)) {
      println(s" $name : Scheduler already active with key = $key")
    } else {
      registry.put(key, scheduler.scheduleOnce(initialDelay)(invokeFuture(key)(task)))
      println(s" $name : Scheduler started with key = $key")
    }
  }

  /** Waits for Future completion */
  private def invokeFuture[T](key: String)(task: => Future[T]): Unit = {
    task.onComplete({
      case Success(_)  => waitForInterval(key)(task)
      case Failure(ex) =>
        println(s" $name : Scheduler failing with msg = ${ex.getMessage}")
        waitForInterval(key)(task)
    })
  }

  /** Waits for given interval */
  private def waitForInterval[T](key: String)(task: => Future[T]): Unit = registry.synchronized{
    registry.get(key).foreach{ cancellable =>
      cancellable.cancel()
      registry.put(key, scheduler.scheduleOnce(interval)(invokeFuture(key)(task)))
    }
  }

  /** Cancels the Cancellable from registry with the given key */
  final def cancelScheduler(key: String): Unit = this.synchronized{
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
//  object TestScheduler extends SchedulerTrackerFuture(name = "Test Scheduler", initialDelay = 3.seconds, interval = 3.seconds)
//
//  import org.joda.time.DateTime
//
//  TestScheduler.createScheduler[Boolean]("myKey"){
//    Future{
//      println("Future started " + DateTime.now().getSecondOfMinute)
//      Thread.sleep(1000)
//      println("Future ended " + DateTime.now().getSecondOfMinute)
//      true
//    }
//
//    //        Future.failed(new Exception("some error"))
//  }
//
//  Thread.sleep(15000)
//  TestScheduler.cancelScheduler("myKey")
//
//  Thread.sleep(40000)
//}