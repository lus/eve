package pm.lus.eve.collection;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Acts as a wrapper around a (concurrent) HashSet which implements the builder pattern
 *
 * @param <T> The type of the entities to hold
 * @author Lukas Schulte Pelkum
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleDataContainer<T> {

    private final Set<T> entities;

    public SimpleDataContainer() {
        this.entities = ConcurrentHashMap.newKeySet();
    }

    /**
     * Adds a new entity to the internal set
     *
     * @param entity The entity to add
     * @return The new data container state
     */
    public SimpleDataContainer<T> add(final T entity) {
        this.entities.add(entity);
        return this;
    }

    /**
     * Removes an entity from the internal set
     *
     * @param entity The entity to remove
     * @return The new data container state
     */
    public SimpleDataContainer<T> remove(final T entity) {
        this.entities.remove(entity);
        return this;
    }

    /**
     * @return The modifiable internal set
     */
    public Set<T> getEntities() {
        return this.entities;
    }

}
