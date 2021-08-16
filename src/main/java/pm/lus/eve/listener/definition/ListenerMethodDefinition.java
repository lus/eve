package pm.lus.eve.listener.definition;

import pm.lus.eve.event.Event;
import pm.lus.eve.listener.annotation.Listen;
import pm.lus.eve.topic.ReceivingTopic;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Defines an event receiving listener method inside a {@link pm.lus.eve.listener.Listener}
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class ListenerMethodDefinition {

    private final Method method;
    private final Set<ReceivingTopic> receivingTopics;
    private final Class<? extends Event> receivingEventType;

    private ListenerMethodDefinition(final Method method, final Set<ReceivingTopic> receivingTopics, final Class<? extends Event> receivingEventType) {
        this.method = method;
        this.receivingTopics = receivingTopics;
        this.receivingEventType = receivingEventType;
    }

    public static ListenerMethodDefinition build(final Method method) {
        final Set<ReceivingTopic> receivingTopics = Arrays.stream(method.getAnnotation(Listen.class).value())
                .map(ReceivingTopic::compile)
                .collect(Collectors.toUnmodifiableSet());
        final Class<? extends Event> receivingEventType = (Class<? extends Event>) method.getParameterTypes()[1];

        return new ListenerMethodDefinition(method, receivingTopics, receivingEventType);
    }

    public Method getMethod() {
        return this.method;
    }

    public Set<ReceivingTopic> getReceivingTopics() {
        return this.receivingTopics;
    }

    public Class<? extends Event> getReceivingEventType() {
        return this.receivingEventType;
    }

}
