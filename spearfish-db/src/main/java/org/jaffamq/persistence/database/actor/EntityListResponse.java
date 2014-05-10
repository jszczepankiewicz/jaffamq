package org.jaffamq.persistence.database.actor;

import java.util.List;

/**
 * Created by urwisy on 2014-05-09.
 */
public class EntityListResponse<T> {

    private final List<T> page;

    public EntityListResponse(List<T> page) {
        this.page = page;
    }

    public List<T> getPage() {
        return page;
    }
}
