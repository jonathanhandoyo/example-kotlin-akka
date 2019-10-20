package ex.akka.chapter2_lifecycle.actors

import akka.actor.AbstractLoggingActor
import akka.actor.PoisonPill
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

class Parent : AbstractLoggingActor() {

  companion object {
    fun props(): Props = Props.create { Parent() }
  }

  object Response
  object Trigger

  override fun createReceive(): Receive {
    return ReceiveBuilder
      .create()
      .match(Response::class.java) { onResponse() }
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  override fun preStart() = log().info("PreStart for ${self.path().name()}")
  override fun postStop() = log().info("PostStop for ${self.path().name()}")

  private fun onTrigger() {
    (1..9).toList()
      .map { index -> "child-$index" }
      .map { name -> context.actorOf(Child.props(), name) }
      .forEach { actor -> actor.tell(Child.Trigger, self) }
  }

  private fun onResponse() {
    sender.tell(PoisonPill.getInstance(), self)
  }
}
