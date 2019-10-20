package ex.akka.chapter1_basic.actors

import akka.actor.AbstractLoggingActor
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

class Parent: AbstractLoggingActor() {

  companion object {
    fun props(): Props = Props.create { Parent() }
  }

  object Count
  object Response
  object Trigger

  override fun createReceive(): Receive {
    return ReceiveBuilder
      .create()
      .match(Count::class.java) { onCount() }
      .match(Response::class.java) { onResponse() }
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  private fun onCount() {
    log().info("Count triggered!")
    log().info("${context.children.count()} children accounted!")
  }

  private fun onResponse() {
    log().info("${sender.path()} responded")
  }

  private fun onTrigger() {
    log().info("Parent triggered!")

    (1..9)
      .map { index -> "child-$index" }
      .map { name -> context.actorOf(Child.props(), name) }
      .forEach { actor -> actor.tell(Child.Trigger, self) }

    self.tell(Count, self)
  }
}
