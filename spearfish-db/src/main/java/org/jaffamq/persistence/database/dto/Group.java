package org.jaffamq.persistence.database.dto;

/**
 * Represent security group attached to users.
 */
public class Group {

    private final long id;
    private final String name;

    public Group(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
