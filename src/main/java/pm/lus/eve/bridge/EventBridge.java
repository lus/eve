package pm.lus.eve.bridge;

import pm.lus.eve.event.Event;
import pm.lus.eve.event.context.EventContext;

/**
 * Represents an abstract class event bridges have to extend
 * The reason for a separation of normal event listeners is that event bridges get executed in a separate thread
 *
 * @param <T> The (super)type of the event this bridge applies to
 */
public abstract class EventBridge<T extends Event> {

    /**
     * Gets called whenever an event comes in
     *
     * @param context The corresponding event context
     * @param event   The event itself
     */
    protected abstract void receive(EventContext context, T event);

    /**
     * Has to return the event type the bridge implementation receives
     * This is used because using reflect for generic types is not safe and having this method is not a big drawback
     *
     * @return The event type the bridge implementation receives
     */
    protected abstract Class<T> getEventType();

    /**
     * Calls this event bridge but only calls the receive method if the passed event is of the required type
     *
     * @param ctx   The event context of the execution
     * @param event The event itself
     * @return Whether or not the event bridge was actually executed
     */
    public boolean call(final EventContext ctx, final Event event) {
        if (!this.getEventType().isAssignableFrom(event.getClass())) {
            return false;
        }
        this.receive(ctx, (T) event);
        return true;
    }

}
