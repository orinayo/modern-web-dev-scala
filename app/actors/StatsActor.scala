package actors

import akka.actor.Actor
import akka.actor.Actor.Receive
import actors.StatsActor.{Ping,RequestReceived,GetStats}

class StatsActor extends Actor {
    var counter = 0

    override def receive: Receive = {
        case Ping => ()
        case RequestReceived => counter += 1
        // The sender() method is available within Actor, and can be used to get a reference to the sender of the request.
        // The ! method, also known as tell, follows the “fire-and-forget” semantics, 
        // i.e sends the message without expecting anything in response.
        case GetStats => sender() ! counter
    }
}

object StatsActor {
    val name = "statsActor"
    val path = s"/user/$name"

    case object Ping
    case object RequestReceived
    case object GetStats
}