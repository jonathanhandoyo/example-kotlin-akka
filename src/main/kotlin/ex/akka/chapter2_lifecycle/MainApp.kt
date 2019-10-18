package ex.akka.chapter2_lifecycle

import akka.actor.ActorRef
import akka.actor.ActorSystem
import ex.akka.chapter2_lifecycle.actors.Parent

fun main() {
  val system = ActorSystem.create("system")
  val parent = system.actorOf(Parent.props(), "parent")

  parent.tell(Parent.Trigger, ActorRef.noSender())
}
