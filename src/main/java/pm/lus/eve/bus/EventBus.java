package pm.lus.eve.bus;

import pm.lus.eve.collection.TransformingDataContainer;
import pm.lus.eve.event.Event;
import pm.lus.eve.event.context.EventContext;
import pm.lus.eve.listener.Listener;
import pm.lus.eve.listener.definition.ListenerDefinition;
import pm.lus.eve.listener.definition.ListenerMethodDefinition;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Keeps track of event listeners and processes and routes incoming and outgoing events
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class EventBus implements Closeable {

    private final TransformingDataContainer<Listener, ListenerDefinition> listenerDefinitions;
    private final ExecutorService executorService;

    public EventBus(final ExecutorService executorService) {
        this.listenerDefinitions = new TransformingDataContainer<>(ListenerDefinition::build);
        this.executorService = executorService;
    }

    public EventBus() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * @return The data container used to register event listeners
     */
    public TransformingDataContainer<Listener, ListenerDefinition> listeners() {
        return this.listenerDefinitions;
    }

    /**
     * Emits an event and asynchronously calls all event listeners that opted in to receive it
     *
     * @param topic The raw topic the event belongs to
     * @param event The event itself
     * @return A future to keep track with the listener executing task
     */
    public Future<?> emit(final String topic, final Event event) {
        return this.executorService.submit(() -> {
            final EventContext context = new EventContext(topic);

            for (final ListenerDefinition listenerDefinition : this.listenerDefinitions.getEntities().values()) {
                for (final ListenerMethodDefinition methodDefinition : listenerDefinition.getMethodDefinitions()) {
                    if (methodDefinition.getReceivingTopics().stream().noneMatch(receivingTopic -> receivingTopic.matches(topic))) {
                        continue;
                    }

                    if (!methodDefinition.getReceivingEventType().isAssignableFrom(event.getClass())) {
                        continue;
                    }

                    try {
                        final Method method = methodDefinition.getMethod();
                        final boolean wasAccessible = method.canAccess(listenerDefinition.getInstance());
                        if (!wasAccessible) {
                            method.setAccessible(true);
                        }

                        method.invoke(listenerDefinition.getInstance(), context, event);

                        if (!wasAccessible) {
                            method.setAccessible(false);
                        }
                    } catch (final InvocationTargetException | IllegalAccessException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void close() {
        this.executorService.shutdown();
    }

}
