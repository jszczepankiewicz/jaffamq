package org.jaffamq.persistence.journal;

import journal.io.api.Location;
import org.jaffamq.persistence.PersistedMessageId;

/**
 * Holder of
 */
public class JournalMessageMessageId implements PersistedMessageId {

    private final Location location;

    public JournalMessageMessageId(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "JournalMessageMessageId{" +
                "location=" + location +
                '}';
    }
}
