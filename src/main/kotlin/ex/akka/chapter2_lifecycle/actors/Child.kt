package ex.akka.chapter2_lifecycle.actors

import akka.actor.AbstractLoggingActor
import akka.actor.Props

class Child : AbstractLoggingActor() {

  companion object {
    fun props(): Props = Props.create { Child() }
  }

  object Trigger

  override fun createReceive(): Receive {
    return receiveBuilder()
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  override fun preStart() = log().info("PreStart for ${self.path().name()}")
  override fun postStop() = log().info("PostStop for ${self.path().name()}")

  private fun onTrigger() {
    sender.tell(Parent.Response, self)
  }
}
