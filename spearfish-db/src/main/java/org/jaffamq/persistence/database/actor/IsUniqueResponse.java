package org.jaffamq.persistence.database.actor;

/**
 * Created by urwisy on 2014-05-09.
 */
public class IsUniqueResponse {

    private final boolean unique;

    public IsUniqueResponse(boolean unique) {
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }
}
