package pm.lus.eve.listener;

import pm.lus.eve.event.Event;
import pm.lus.eve.event.context.EventContext;
import pm.lus.eve.listener.annotation.Listen;

import java.util.function.BiConsumer;

/**
 * Acts as the marker interface all event listeners have to implement for the bus to recognize them
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Listener {

    // Marker interface

    /**
     * Builds an event listener using a consumer to avoid unnecessary implementation overhead
     *
     * @param type     The type of the event to receive
     * @param listener The consumer which gets executed whenever a suiting event arises
     * @param <T>      The type of the event to receive
     * @return The built event listener
     */
    static <T extends Event> Listener inline(final Class<T> type, final BiConsumer<EventContext, T> listener) {
        return new Listener() {
            @Listen
            public void handle(final EventContext ctx, final T event) {
                listener.accept(ctx, event);
            }
        };
    }

}
