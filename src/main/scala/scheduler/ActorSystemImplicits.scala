package scheduler

import akka.actor.ActorSystem

/**
  * Created by Rajat on Oct 17, 2022.
  */

object ActorSystemImplicits {

  implicit lazy val system: ActorSystem = ActorSystem("ReactorActorSystem")

}
