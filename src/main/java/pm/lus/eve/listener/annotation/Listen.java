package pm.lus.eve.listener.annotation;

import java.lang.annotation.*;

/**
 * Marks a method inside a {@link pm.lus.eve.listener.Listener} as an event receiving listener one
 *
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Listen {

    // The topics to receive event from
    String[] value() default {"**"};

}
