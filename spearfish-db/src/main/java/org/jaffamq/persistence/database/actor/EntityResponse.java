package org.jaffamq.persistence.database.actor;

/**
 * Created by urwisy on 2014-05-09.
 */
public class EntityResponse<T> {
    private final T entity;

    public EntityResponse(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}
