package ex.akka.chapter1_basic

import akka.actor.ActorRef
import akka.actor.ActorSystem
import ex.akka.chapter1_basic.actors.Parent

fun main() {
  val system = ActorSystem.create("system")
  val parent = system.actorOf(Parent.props(), "parent")

  parent.tell(Parent.Trigger, ActorRef.noSender())
}
