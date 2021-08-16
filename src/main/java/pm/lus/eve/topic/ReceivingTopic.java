package pm.lus.eve.topic;

import java.util.regex.Pattern;

/**
 * Represents an event topic listeners listen to
 * This class is subject to change in order not to use RegExes anymore
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class ReceivingTopic {

    private final Pattern pattern;

    private ReceivingTopic(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Compiles a new receiving topic using a raw string representation
     * '*' will match any part (without a dot)
     * '**' will match any multiple parts (including dots)
     *
     * @param raw The raw string representation
     * @return The compiled receiving topic
     */
    public static ReceivingTopic compile(final String raw) {
        final String rawPattern = Pattern.quote(raw)
                .replace("**", "\\E([^\\.]+)\\Q")
                .replace("*", "\\E(.)+\\Q");

        final Pattern pattern = Pattern.compile(rawPattern);

        return new ReceivingTopic(pattern);
    }

    /**
     * Checks whether or not a raw incoming topic matches this receiving one
     *
     * @param raw The raw incoming topic
     * @return Whether or not it matches this receiving one
     */
    public boolean matches(final String raw) {
        return this.pattern.matcher(raw).matches();
    }

}
