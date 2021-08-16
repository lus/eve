package pm.lus.eve.listener.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.lus.eve.event.Event;
import pm.lus.eve.event.context.EventContext;
import pm.lus.eve.listener.Listener;
import pm.lus.eve.listener.annotation.Listen;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines an event {@link Listener}
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class ListenerDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerDefinition.class);

    private final Listener instance;
    private final Set<ListenerMethodDefinition> methodDefinitions;

    private ListenerDefinition(final Listener instance, final Set<ListenerMethodDefinition> methodDefinitions) {
        this.instance = instance;
        this.methodDefinitions = methodDefinitions;
    }

    public static ListenerDefinition build(final Listener instance) {
        final Class<? extends Listener> clazz = instance.getClass();

        final Set<ListenerMethodDefinition> methodDefinitions = new HashSet<>();

        for (final Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Listen.class)) {
                continue;
            }

            // Event listeners have to accept exactly 2 parameters
            if (method.getParameterCount() != 2) {
                LOGGER.warn(
                        "method '{}#{}' is marked as an event listener but has an invalid parameter count ({} != {}); skipping!",
                        clazz.getName(),
                        method.getName(),
                        method.getParameterCount(),
                        2
                );
                continue;
            }

            final Class<?>[] parameterTypes = method.getParameterTypes();

            // The first parameter always has to be the event context
            if (parameterTypes[0] != EventContext.class) {
                LOGGER.warn(
                        "method '{}#{}' is marked as an event listener but receives no event context as the first parameter (found '{}' but expected '{}'); skipping!",
                        clazz.getName(),
                        method.getName(),
                        parameterTypes[0].getName(),
                        EventContext.class.getName()
                );
                continue;
            }

            // The second argument always has to be any event type
            if (!Event.class.isAssignableFrom(parameterTypes[1])) {
                LOGGER.warn(
                        "method '{}#{}' is marked as an event listener but receives no event (or subclass) as the second parameter; skipping!",
                        clazz.getName(),
                        method.getName()
                );
                continue;
            }

            methodDefinitions.add(ListenerMethodDefinition.build(method));
        }

        return new ListenerDefinition(instance, methodDefinitions);
    }

}
