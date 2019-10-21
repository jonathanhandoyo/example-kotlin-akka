## Chapter 2 - Fault Tolerance

In this chapter we will explore the fault tolerance of an actor system. So, the "Resilience" aspect of Reactive Manifesto. We will do this in two steps, first we will explore the lifecycle of an actor inside an actor system, then we will explore how to add resiliency to the actor system with this knowledge.

### Lifecycle of an Actor

In any actor systems, actors are created and destroyed countless times, so it's obvious that we need to understand the lifecycle of an actor. 

The lifecycle of an actor are controlled with callbacks. There are four lifecycle callbacks that every actor has, and all of them are generally implemented with default behavior so you should only implement what you need to override. How you implement this is exactly by overriding the function.

#### 1. Pre Start

This is a callback that gets triggered _asynchronously_ when the actor is created. By default this provides an empty behavior, but in general this can be used to implement initiation logic.

There are 2 schools of thoughts on what this callback should do. You either
* Do everything you need to do in this callback, or
* Defer that logic and trigger it by passing a message to `self`

Anyways, that is up to you. But a few things you can do inside this callbacks (to give you a few ideas) include:
* Registering to some sort of mothership (not our `ActorSystem`, since it obviously knows)
* Scheduling something that should run recurrently or with delay

#### 2. Post Stop

This is a callback that gets triggered _asynchronously_ when the actor is destroyed. By default this provides an empty behavior, but in general this can be used to implement clean-up logic.

#### 3. Pre Restart

This is a callback that gets triggered on a _crashed_ actor _before_ it gets restarted. By default this provides an implementation that disposes all its children then calls `postStop()`.

There are some things unintuitive about this:
* It's triggered on a _crashed_ actor
* It's triggered _before_ it gets restarted
* It calls `postStop()` (remember: `preRestart()` calls `postStop()`) 

#### 4. Post Restart

This is a callback that gets triggered on a _crashed_ actor _after_ it gets restarted. By default this provides an implementation that calls `preStart()`.

Counter-unintuitively (if that's even a word):
* It's triggered on a _crashed_ actor
* It's triggered _after_ it gets restarted
* It calls `preStart()` (remember: `postRestart()` calls `preStart()`)

### Supervising an Actor
