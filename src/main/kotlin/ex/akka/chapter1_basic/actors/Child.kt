package ex.akka.chapter1_basic.actors

import akka.actor.AbstractLoggingActor
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

class Child : AbstractLoggingActor() {

  companion object {
    fun props(): Props = Props.create { Child() }
  }

  object Trigger

  override fun createReceive(): Receive {
    return ReceiveBuilder
      .create()
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  private fun onTrigger() {
    log().info("Child triggered!")
    sender.tell(Parent.Response, self)
  }
}
