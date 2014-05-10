package org.jaffamq.persistence.database.actor;

/**
 * Created by urwisy on 2014-05-04.
 */
public class GetByIdRequest {

    private final Long id;

    public GetByIdRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
