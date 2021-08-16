package pm.lus.eve.event.context;

import pm.lus.eve.bus.EventBus;

/**
 * Represents a context passed to every event listener
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class EventContext {

    private final EventBus bus;
    private final String topic;

    public EventContext(final EventBus bus, final String topic) {
        this.bus = bus;
        this.topic = topic;
    }

    /**
     * @return The bus that called the corresponding event
     */
    public EventBus getBus() {
        return this.bus;
    }

    /**
     * @return The topic the corresponding event was called for
     */
    public String getTopic() {
        return this.topic;
    }

}
