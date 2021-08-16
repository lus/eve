package pm.lus.eve.event.context;

/**
 * Represents a context passed to every event listener
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class EventContext {

    private final String topic;

    public EventContext(final String topic) {
        this.topic = topic;
    }

    /**
     * @return The topic the corresponding event was called for
     */
    public String getTopic() {
        return this.topic;
    }

}
