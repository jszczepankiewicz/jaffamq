package org.jaffamq.persistence.database;

import org.jaffamq.persistence.database.destination.Destination;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.jaffamq.persistence.database.sql.SelectNextId;
import org.jaffamq.persistence.database.user.User;

import java.util.List;

/**
 * Utility class responsible for providing id values for newly created objects. Inernally using H2 Sequences.
 * H2 is using internally caching sequences.
 */
public class IdentityProvider {

    private static SelectNextId selectNextId = new SelectNextId();

    /**
     * Retrieve next ID for specified type of object (class).
     *
     * @param session
     * @param clazz
     * @return
     */
    public static Long getNextIdFor(JDBCSession session, Class clazz) {

        String seqname;

        if (User.class.equals(clazz)) {
            seqname = "USER_SEQ";
        } else if (Group.class.equals(clazz)) {
            seqname = "GROUP_SEQ";
        } else if (Destination.class.equals(clazz)) {
            seqname = "DESTINATION_SEQ";
        } else {
            throw new IllegalArgumentException("Sequence name not found for class: " + clazz);
        }

        List<Long> ids = selectNextId.execute(session, seqname);

        if (ids.size() != 1) {
            throw new IllegalStateException(String.format("NEXTVAL for sequence: %s returend %d tuples, expected 1", seqname, ids.size()));
        }

        return ids.get(0);
    }
}
