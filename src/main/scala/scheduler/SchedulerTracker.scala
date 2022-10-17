package scheduler

import akka.actor.Cancellable
import scheduler.ActorSystemImplicits.system

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by Rajat on Apr 08, 2022.
  */

sealed class SchedulerTracker(name: String,
                              initialDelay: FiniteDuration,
                              interval: FiniteDuration) {
  /** Registry to keep cancellable in memory */
  private final val registry: mutable.Map[String, Cancellable] = mutable.Map.empty

  /** Creates and add the Cancellable to the registry with the given key */
  final def createScheduler(key: String)(job: => Unit): Unit = this.synchronized{
    if (isNoActiveScheduler(key)) {
      val cancellable = system.scheduler.scheduleWithFixedDelay(initialDelay, interval){ () =>
        Try(job).recover({
          case ex => println(s" $name : Scheduler failing with msg = ${ex.getMessage}")
        })
      }
      println(s" $name : Scheduler started with key = $key")
      registry.put(key, cancellable)
    } else {
      println(s" $name : Scheduler already active with key = $key")
    }
  }

  /** Cancels the Cancellable from registry with the given key */
  final def cancelScheduler(key: String): Unit = this.synchronized{
    registry.get(key).foreach{ cancellable =>
      cancellable.cancel()
      if (cancellable.isCancelled) {
        println(s" $name : Scheduler cancelled for key = $key")
        registry.remove(key)
      } else {
        println(s" $name : Scheduler couldn't be cancelled with key = $key")
      }
    }
  }

  /** returns true IF no active scheduler found in registry with given key ELSE false. */
  final private def isNoActiveScheduler(key: String): Boolean = this.synchronized{
    registry.get(key).forall(_.isCancelled)
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