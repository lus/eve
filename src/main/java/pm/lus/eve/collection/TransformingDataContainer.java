package pm.lus.eve.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Acts as a map-like data structure which transforms its values based on the key using a transformer
 * Additionally it implements the builder pattern to allow a more fluent code flow
 *
 * @param <T> The type of the key
 * @param <R> The type of the value which gets derived from the key
 */
public class TransformingDataContainer<T, R> {

    private final Map<T, R> entities;
    private final Function<T, R> transformer;

    public TransformingDataContainer(final Function<T, R> transformer) {
        this.entities = new ConcurrentHashMap<>();
        this.transformer = transformer;
    }

    /**
     * Transforms the given entity and adds it to the internal map
     *
     * @param entity The entity to use as the key
     * @return The new data container state
     */
    public TransformingDataContainer<T, R> add(final T entity) {
        this.entities.put(entity, this.transformer.apply(entity));
        return this;
    }

    /**
     * Removes the value stored under the given key from the internal map
     *
     * @param entity The entity to use as the key
     * @return The new data container state
     */
    public TransformingDataContainer<T, R> remove(final T entity) {
        this.entities.remove(entity);
        return this;
    }

    /**
     * @return The modifiable internal map
     */
    public Map<T, R> getEntities() {
        return this.entities;
    }

}
