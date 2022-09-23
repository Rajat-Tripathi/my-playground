package others

import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Rajat on Sep 23, 2022.
  */

class ParallelExecutor {

  private val counter = new AtomicInteger(0)

  final def parallelism[T](maxParallelExecution: Int)(fn: => Future[T]): Future[T] = {
    require(maxParallelExecution >= 0, "maxParallelExecution can't be negative !!")
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

  private def getCompileTimeKey(stackTraceElements: Array[StackTraceElement]): String = {
    if (stackTraceElements.length >= 3) {
      val ste = stackTraceElements(2)
      ste.getClassName + "_" + ste.getMethodName + "_" + ste.getLineNumber
    } else {
      throw new Exception("Could not get stack trace elements !!")
    }
  }

  private def getParallelExecutor[O](key: String): ParallelExecutor = this.synchronized{
    registry.getOrElse(key, {
      val executor = new ParallelExecutor
      println(s"Created new ParallelExecutor with key = '$key' ")
      registry.put(key, executor)
      executor
    })
  }

  /** To control max parallel execution of Futures */
  def parallelism[T](maxParallelExecution: Int)(fn: => Future[T]): Future[T] = {
    val stackTraceElements = Thread.currentThread.getStackTrace
    val key: String        = getCompileTimeKey(stackTraceElements)
    getParallelExecutor(key).parallelism(maxParallelExecution)(fn)
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
    Thread.sleep(1000)
    executor(x)
  }

  //  def myMethod(v: Int): Future[Boolean] = oneAtATime{
  //    Future.successful(v % 2 == 0)
  //  }


  Thread.sleep(30000)

}