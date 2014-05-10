package org.jaffamq.persistence.database.actor;

/**
 * Created by urwisy on 2014-05-09.
 */
public class IsUniqueRequest {
    private final String name;

    public IsUniqueRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
