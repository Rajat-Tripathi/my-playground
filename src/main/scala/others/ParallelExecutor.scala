package others

import java.util.concurrent.atomic.AtomicInteger

import utility.Utility

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Rajat on Sep 23, 2022.
  */

class ParallelExecutor(maxParallelExecution: Int) {

  require(maxParallelExecution >= 0, "maxParallelExecution can't be negative !!")

  private val counter = new AtomicInteger(0)

  final def parallelism[T](fn: => Future[T]): Future[T] = {
    if (counter.get() < maxParallelExecution) {
      counter.incrementAndGet()
      fn.transform(s => {
        counter.decrementAndGet()
        s
      }, f => {
        counter.decrementAndGet()
        f
      })
    } else {
      println(s"Max $maxParallelExecution parallel execution allowed !!")
      Future.failed(new Exception(s"Max $maxParallelExecution parallel execution allowed !!"))
    }
  }

}

object ParallelExecutor {

  private val registry: mutable.Map[String, ParallelExecutor] = mutable.Map.empty

  private def getParallelExecutor[O](key: String, maxParallelExecution: Int): ParallelExecutor = this.synchronized{
    registry.getOrElse(key, {
      val executor = new ParallelExecutor(maxParallelExecution)
      println(s"Created new ParallelExecutor with key = '$key' ")
      registry.put(key, executor)
      executor
    })
  }

  /** To control max parallel execution of Futures */
  def parallelism[T](maxParallelExecution: Int)(fn: => Future[T]): Future[T] = {
    val stackTraceElements = Thread.currentThread.getStackTrace
    val key: String        = Utility.getCompileTimeKey(stackTraceElements)
    getParallelExecutor(key, maxParallelExecution).parallelism(fn)
  }

}

object ParallelExecutorTest extends App {

  private def function(x: Int): Future[Boolean] = Future{
    println(s"Future $x started")
    Thread.sleep(3000)
    println(s"Future $x ended")
    true
  }

  import ParallelExecutor._

  private def executor(x: Int): Future[Boolean] = parallelism(3){
    function(x)
    //    function(x)
  }

  (1 to 50).foreach{ x =>
    //    Thread.sleep(1000)
    executor(x)
  }

  //  def myMethod(v: Int): Future[Boolean] = oneAtATime{
  //    Future.successful(v % 2 == 0)
  //  }


  Thread.sleep(30000)

}