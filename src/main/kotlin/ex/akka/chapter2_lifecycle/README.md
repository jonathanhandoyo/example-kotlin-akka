In this chapter we will explore the lifecycle of an actor. In any actor systems, actors are created and destroyed countless times, so it's obvious that we need to understand the lifecycle of an actor.

Let's just jump right into it. There are four lifecycle callbacks that every actor has, and all of them are generally implemented with empty behavior so you should only implement what you need to override. How you implement this is exactly by overriding the function.

### 1. Pre Start

This is a callback that gets triggered _asynchronously_ when the actor is created. By default this provides an empty behavior, but in general this can be used to implement initiation logic.

There are 2 schools of thoughts on what this callback should do. You either
* Do everything you need to do in this callback, or
* Defer that logic and trigger it by passing a message to `self`

Anyways, that is up to you.  

### 2. Post Stop

This is a callback that gets triggered _asynchronously_ when the actor actor is destroyed. By default this provides an empty behavior, but in general this can be used to implement clean-up logic.

### 3. Pre Restart

This is a callback that gets triggered on a _crashed_ actor _before_ it gets restarted. By default this provides an implementation that disposes all its children then calls `postStop()`. 

### 4. Post Restart

This is a callback that gets triggered on a _crashed_ actor _after_ it gets restarted. By default this provides an implementation that calls `preStart()`.
