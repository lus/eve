# eve

`eve` aims at providing a very simple way of working with events efficiently.

## Installation

### Gradle

```groovy
implementation("pm.lus:eve:1.0.0")
```

### Maven

```xml
<dependency>
    <groupId>pm.lus</groupId>
    <artifactId>eve</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Unstable Versions

If you need to use any version not released to Maven Central yet, you can just use [JitPack](https://jitpack.io/#lus/eve) which gives you much control about what branch/release/commit you want to use.

## Usage

`eve` aims at being as simple but effective as possible. This is reflected in the structure we used to build it.

Events are identified both by their **type** and the **topic** they were sent to.

*Event bridges* allow building bridges/adapters between `eve` and other event/messaging services like *NATS* or *Kafka*.

### Event Type Tree

As already stated, events are (partially) identified by their **type**.
Given the precondition that every event type has to implement the root event node interface `Event`, this allows building a node-based event type tree looking like this:

```
Event (interface)
  |
  |-- MyEvent (class)
  |-- MySubNode (interface)
        |
        |-- MyFirstSubEvent (class)
        |-- MySecondSubEvent (class)
  |-- SomeOtherEvent (abstract class)
        |
        |-- FinalEvent (class)
```

Based on this particular structure one can say that every `MySecondSubEvent` is also a `MySubNode`-based event, but not every `MySubNode`-based event is a `MySecondSubEvent` one.

### Event Topic

Event topics are just used as *namespaces* events get sent into.
You may know them by event broker systems like *Kafka* where they are sometimes also referred to as *channels*.

An example topic would be `chat_messages`. Sending an event into this channel will invoke any listener listening to it.
Pretty simple.

Topics may also have multiple levels separated by dots: `chat_messages.by_admins`. Same concept here.

If you want to listen on topic structures like `top.<anything>.sub`, you can use the `*` wildcard (`top.*.sub`) which will match anything **except** dots, effectively ignoring the value of a single level.

The `**` wildcard will match anything **including** dots.
Thus, listening to `**` will listen to any topic structure.
Listening to `top.**.sub` would match both `top.any.topics.in.between.sub` and `top.mid.sub`.

Optional levels (listening both to `channel` and `channel.*`) are currently not implemented. A workaround would be manually listening to these structures.

### Event Listeners

Every event listener has to be a class implementing the `Listener` interface.
Every method annotated with `@Listen([optional topic restrictions])` is considered an event listener and will raise an error if illegally structured.

The first argument the listener function takes always has to be an `EventContext` containing information about the bus invoking the listener and the topic the event was sent to.

The second argument's type has to be `Event` or a child of it. This will restrict the listener to a specific event type or node.
Thus listening on the root type `Event` will receive any event coming in.

An example event listener may look like this:

```java
public class MyListener implements Listener {

    // '**' matches every topic
    @Listen("**")
    public void catchAll(EventContext context, Event event) {
        // Do something
    }

    // Omitting the topic value will use '**' by default
    @Listen
    public void typeSpecificCatchAll(EventContext context, MyEvent event) {
        // Do something
    }

    // Multiple topics are also supported
    @Listen({"some.*.topic", "other.topic"})
    public void specific(EventContext context, MyEvent event) {
        // Do something
    }

}
```

### Event Bridges

Event bridges are used to bridge events between `eve` and anything other.
They have to implement the abstract class `EventBridge<T>`; `T` representing the event type the bridge should apply to (just as the second argument of a listener function).

An event bridge printing all events to stdout may look like this:

```java
public class StdoutBridge extends EventBridge<Event> {

    @Override
    protected void receive(final EventContext context, final Event event) {
        System.out.println(context.getTopic() + "::" + event.getClass().getName());
    }

    // Required for internal purposes; has to return T's class type (Class<T>)
    @Override
    protected Class<Event> getEventType() {
        return Event.class;
    }

}
```

### Event Bus

The event bus is responsible for assembling the lose structures and handling and routing incoming events.

An example setup may look like this:

```java
public class Application {

    public static void main(String[] args) {
        EventBus bus = new EventBus();

        bus.bridges().add(new StdoutBridge());

        bus.listeners().add(Listener.inline(MyEvent.class, (ctx, evt) -> {
            // Doing something
        }));

        bus.emit("some.cool.topic", new MyEvent("my data"));

        // Further logic
    }

}
```

## Community

Additionally to the tools GitHub provides to contribute to this project, there is a [Discord server](https://go.lus.pm/discord) where you can communicate with others using `eve`.
