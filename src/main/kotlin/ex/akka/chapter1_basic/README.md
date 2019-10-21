## Chapter 1 - The Basics

In this chapter we will explore the basics of "Message Driven" aspect of Reactive Manifesto. After all, in the concept of actor system, actors communicate exclusively by exchanging messages. 
 
Actors are object that encapsulate states and behavior. Some people understand actors passing messages as people in factory pipelines.

Actors (like any organizations) should form hierarchies. A particular actor should oversee certain high-level function of the application. This actor (we can call them Guardians) then should split its function into smaller lower-level functions. You can do this as many times as you reasonably need. For this purpose, the higher-level actor should start the child actors that it supervises. In this chapter we will only focus on the simpler Parent-Child hierarchy so it's easier to simulate. But you can expand the hierarchy as expansive as you need. 

`ActorSystem` is basically the root level actor for your application, as such your hierarchy will start from your `ActorSystem`. An application will have one ActorSystem (obviously) and you can create it as such:

```kotlin
fun main() {
  val system = ActorSystem.create("system")
}
```

From this piece of code, there are a few things to note:
* You instantiate your `ActorSystem` directly from your `main()` function.
* Your system will create hierarchical paths starting from your `ActorSystem` as `akka://system`.
* Your system will run indefinitely until the `ActorSystem` is terminated.

From here, you can create your actors. Typically the first few actors you create will be your guardians, but let's keep it simple and keep to a Parent-Child actor hierarchy. Let's arrange a Parent-Child actors where your `ActorSystem` will create the `Parent` and send a message to it. Then your `Parent` will create the `Child` and send a message to it.

### 1. Main

```kotlin
fun main() {
  val system = ActorSystem.create("system")
  val parent = system.actorOf(Parent.props(), "parent")
  parent.tell(Parent.Trigger, ActorRef.noSender())
}
```

A few things to note here:
* You never instantiate the class of the actor, instead you instantiate `ActorRef`.
* You instantiate an `ActorRef` by calling `.actorOf(props: Props, name: String)`.
* `Props` is a configuration point of an `ActorRef`.

### 2. Parent

```kotlin
class Parent: AbstractLoggingActor() {
  
  companion object {
    fun props(): Props = Props.create { Parent() }
  }

  object Trigger

  override fun createReceive(): Receive {
    return ReceiveBuilder
      .create()
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  private fun onTrigger() {
    log().info("Parent triggered!")

    (1..9)
      .map { index -> "child-$index" }
      .map { name -> context.actorOf(Child.props(), name) }
      .forEach { actor -> actor.tell(Child.Trigger, self) }
  }
}
```

A few things to note here:
* Your actor needs to extend something from the `Actor` class hierarchy, the easiest one: `AbstractLoggingActor`.
* Your actor should have a factory function that provides a `Props` instance to help instantiate it.
* Your actor should define their messages _internally_, where it's easier to predict.
* Your actor should define their messages as `data class` if they carry information or `object` if they don't.

Now that we've got the small stuffs out of the way, let's explore this actor.

Every actor should define how it should behave given certain kinds of messages. You do this by overriding the abstract `createReceive` function. Generally this is a case of matching a message to a function, so it's best to defer the logic to another `private fun onXxx(message: T): Unit { ... }`. This is to separate the routing logic and the execution logic.

There are 3 general constructs for matching:
1. `.matchEquals(message: M, fn: ((M) -> Unit))` for specific message, works best with `object` message
2. `.match(type: Class<M>, fn: ((M) -> Unit))` for typed message, works best with `data class` message
3. `.matchAny(fn: ((Any) -> Unit))` for generic message, works best for dead letters

In this example, when the `Parent` receives a `Trigger` message, it will create 9 child actors and send them `Trigger` message. You "send" a message by calling the `.tell(message: T, sender: ActorRef)` function and passing in the message (within the dictionary of the target actor, obviously) and the sender (typically `self`, or in some rare cases: `ActorRef.noSender()`)

### 3. Child

```kotlin
class Child: AbstractLoggingActor() {

  companion object {
    fun props(): Props = Props.create { Child() }
  }

  object Trigger

  override fun createReceiver(): Receive {
    return ReceiveBuilder
      .create()
      .match(Trigger::class.java) { onTrigger() }
      .build()
  }

  private fun onTrigger() {
    log().info("Child triggered!")
  }
}
```

Everything explained about the `Parent` stays true here. After all, they are all actors.

Additional things to note here is that there were 9 `Child` actors created, they all have their own names and paths. You can observe this from the logs generated. `AbstractLoggingActor` logs the paths of the actor doing the logging, and this is very helpful when we need to troubleshoot on any misbehaving actor(s).  
