//package others
//
//import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}
//
//import utility.Utility
//
//import scala.collection.mutable
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.util.Random
//
///**
//  * Created by Rajat on Sep 25, 2022.
//  */
//
//class Processor2[I, O](max: Int) {
//
//  private val inputBuffer = new AtomicReference[Seq[I]](Nil)
//
//  private val number = new AtomicInteger(0)
//
//  private val inProgress = new AtomicBoolean(false)
//
//  private def slave(input: I)(fn: I => Future[O]): Future[O] = this.synchronized{
//    fn(input).transform(s => {
//      //      inputBuffer.set(inputBuffer.get.tail)
//      val newV = number.decrementAndGet()
//      println(s"newV for input = $input = $newV")
//      if (newV < max && newV >= 0) {
//        val next = max - newV
//        println("calling abc with next = " + next)
//        abc(next)(fn)
//      } else {
//        println("Completed last execution with input = " + input)
//      }
//      s
//    }, f => {
//      //      inputBuffer.set(inputBuffer.get.tail)
//      val newV = number.decrementAndGet()
//      println(s"newV for input = $input = $newV")
//      if (newV < max && newV >= 0) {
//        val next = max - newV
//        println("calling abc with next = " + next)
//        abc(next)(fn)
//      } else {
//        println("Completed last execution with input = " + input)
//      }
//      f
//    })
//  }
//
//
//  private def abc(next: Int)(fn: I => Future[O]): Unit = this.synchronized{
//    val inputs = inputBuffer.get().take(next)
//    println(s"Taking new inputs with next = $next = $inputs ")
//    val updated = inputBuffer.get().drop(next)
//    println(s"Updating with inputs with next = $next = $updated ")
//    inputBuffer.set(updated)
//    number.set(inputs.length)
//    if (inputs.isEmpty) {
//      println("Ending the process ...")
//      inProgress.set(false)
//    }
//    inputs.foreach(i => slave(i)(fn))
//  }
//
//  def oneAtATime(input: I)(fn: I => Future[O]): Unit = this.synchronized{
//    if (inProgress.get()) {
//      println("Adding in Queue --> " + input)
//      inputBuffer.set(inputBuffer.get() :+ input)
//    } else {
//      inProgress.set(true)
//      inputBuffer.set(inputBuffer.get() :+ input)
//      println("Starting the process ...")
//      abc(max)(fn)
//    }
//  }
//
//}
//
//object Processor2 {
//
//  private val registry: mutable.Map[String, Processor2[_, _]] = mutable.Map.empty
//
//  private def getProcessor[I, O](key: String, max: Int): Processor2[I, O] = this.synchronized{
//    registry.getOrElse(key, {
//      val executor = new Processor2[I, O](max)
//      println(s"Created new Processor with key = '$key' ")
//      registry.put(key, executor)
//      executor
//    }).asInstanceOf[Processor2[I, O]]
//  }
//
//  /** To control one execution of Future at a time with inputs in buffer */
//  def oneAtATime[I, O](input: I, max: Int)(fn: I => Future[O]): Unit = {
//    val stackTraceElements = Thread.currentThread.getStackTrace
//    val key: String        = Utility.getCompileTimeKey(stackTraceElements)
//    getProcessor(key, max).oneAtATime(input)(fn)
//  }
//
//}
//
//object Processor2Test extends App {
//
//  case class Abc(id: Long, name: String)
//
//  private def function(x: Abc): Future[String] = Future{
//    println(s"Future $x started")
//    val i = Random.nextInt(5)
//    //    println(s"waiting for $i seconds ...")
//    Thread.sleep(i * 1000)
//    println(s"Future $x ended")
//    s"my name is ${x.name} and id is ${x.id} "
//  }
//
//  import Processor2._
//
//  private def executor(x: Abc): Unit = oneAtATime(x, 2){
//    function
//    //    function(x)
//  }
//
//  (1 to 5).foreach{ x =>
//    val i = Random.nextInt(5)
//    //    println(s"waiting for $i seconds ...")
//    //    Thread.sleep(i * 1000)
//    executor(Abc(x, x.toString))
//  }
//
//
//  Thread.sleep(30000)
//
//}
