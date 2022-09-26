package others

import java.util.concurrent.atomic.AtomicReference

import utility.Utility

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
  * Created by Rajat on Sep 25, 2022.
  */

class Processor[I, O] {

  private val inputBuffer = new AtomicReference[Seq[I]](Nil)

  private def slave(input: I)(fn: I => Future[O]): Future[O] = {
    fn(input).transform(s => {
      inputBuffer.set(inputBuffer.get.tail)
      master(fn)
      s
    }, f => {
      inputBuffer.set(inputBuffer.get.tail)
      master(fn)
      f
    })
  }

  private def master(fn: I => Future[O]): Unit = {
    if (inputBuffer.get().nonEmpty) {
      println("Processing in progress ...")
      slave(inputBuffer.get().head)(fn)
    } else {
      println("No more inputs to process !!")
    }
  }

  def oneAtATime(input: I)(fn: I => Future[O]): Unit = {
    if (inputBuffer.get().isEmpty) {
      inputBuffer.set(inputBuffer.get() :+ input)
      println("Starting the process ...")
      master(fn)
    } else {
      inputBuffer.set(inputBuffer.get() :+ input)
    }
  }

}

object Processor {

  private val registry: mutable.Map[String, Processor[_, _]] = mutable.Map.empty

  private def getProcessor[I, O](key: String): Processor[I, O] = this.synchronized{
    registry.getOrElse(key, {
      val executor = new Processor[I, O]
      println(s"Created new Processor with key = '$key' ")
      registry.put(key, executor)
      executor
    }).asInstanceOf[Processor[I, O]]
  }

  /** To control one execution of Future at a time with inputs in buffer */
  def oneAtATime[I, O](input: I)(fn: I => Future[O]): Unit = {
    val stackTraceElements = Thread.currentThread.getStackTrace
    val key: String        = Utility.getCompileTimeKey(stackTraceElements)
    getProcessor(key).oneAtATime(input)(fn)
  }

}

object ProcessorTest extends App {

  case class Abc(id: Long, name: String)

  private def function(x: Abc): Future[String] = Future{
    println(s"Future $x started")
    val i = Random.nextInt(5)
    println(s"waiting for $i seconds ...")
    Thread.sleep(i * 1000)
    println(s"Future $x ended")
    s"my name is ${x.name} and id is ${x.id} "
  }

  import Processor._

  private def executor(x: Abc): Unit = oneAtATime(x){
    function
    //    function(x)
  }

  (1 to 3).foreach{ x =>
    val i = Random.nextInt(5)
    println(s"waiting for $i seconds ...")
    Thread.sleep(i * 1000)
    executor(Abc(x, x.toString))
  }


  Thread.sleep(30000)

}
