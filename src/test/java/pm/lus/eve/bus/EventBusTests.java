package pm.lus.eve.bus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pm.lus.eve.bridge.EventBridge;
import pm.lus.eve.event.Event;
import pm.lus.eve.event.context.EventContext;
import pm.lus.eve.listener.Listener;
import pm.lus.eve.listener.annotation.Listen;
import pm.lus.eve.src.OtherSimpleEvent;
import pm.lus.eve.src.SimpleEvent;

public class EventBusTests {

    @Test
    public void basicEventDelivery() {
        final EventBus bus = new EventBus();

        final int[] called = new int[]{0};

        bus.listeners().add(Listener.inline(Event.class, (ctx, event) -> {
            called[0]++;
        }));

        bus.emit("foo.bar", new Event() {
        }).join();

        Assertions.assertEquals(1, called[0]);
    }

    @Test
    public void topicBasedEventDelivery() {
        final EventBus bus = new EventBus();

        final int[] called = new int[]{0};

        bus.listeners().add(new Listener() {

            @Listen("some.event")
            public void a(final EventContext context, final Event event) {
                called[0]++;
            }

            @Listen("some.other.event")
            public void b(final EventContext context, final Event event) {
                called[0]++;
            }

        });

        bus.emit("some.event", new Event() {
        }).join();

        Assertions.assertEquals(1, called[0]);
    }

    @Test
    public void typeBasedEventDelivery() {
        final EventBus bus = new EventBus();

        final int[] called = new int[]{0, 0};

        bus.listeners().add(new Listener() {

            @Listen
            public void first(final EventContext context, final SimpleEvent event) {
                called[0]++;
            }

            @Listen
            public void second(final EventContext context, final OtherSimpleEvent event) {
                called[1]++;
            }

            @Listen
            public void both(final EventContext context, final Event event) {
                called[0]++;
                called[1]++;
            }

        });

        bus.emit("foo.bar", new SimpleEvent()).join();
        bus.emit("foo.bar", new OtherSimpleEvent()).join();

        Assertions.assertArrayEquals(new int[]{3, 3}, called);
    }

    @Test
    public void basicBridgeExecution() {
        final EventBus bus = new EventBus();

        final int[] called = new int[]{0};

        bus.bridges().add(new EventBridge<>() {

            @Override
            protected void receive(final EventContext context, final Event event) {
                called[0]++;
            }

            @Override
            protected Class<Event> getEventType() {
                return Event.class;
            }

        });

        bus.emit("foo.bar", new SimpleEvent());
        bus.emit("foo.bar", new OtherSimpleEvent());

        Assertions.assertEquals(2, called[0]);
    }

    @Test
    public void typeBasedBridgeExecution() {
        final EventBus bus = new EventBus();

        final int[] called = new int[]{0};

        bus.bridges().add(new EventBridge<SimpleEvent>() {

            @Override
            protected void receive(final EventContext context, final SimpleEvent event) {
                called[0]++;
            }

            @Override
            protected Class<SimpleEvent> getEventType() {
                return SimpleEvent.class;
            }

        });

        bus.emit("foo.bar", new SimpleEvent());
        bus.emit("foo.bar", new OtherSimpleEvent());

        Assertions.assertEquals(1, called[0]);
    }

}
