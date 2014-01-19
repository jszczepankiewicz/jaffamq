package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.dto.Group;
import org.jaffamq.persistence.database.dto.User;
import org.jaffamq.persistence.database.repository.mappings.SelectNextId;
import org.jaffamq.persistence.database.sql.JDBCSession;

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
    public static Long getNextIdFor(JDBCSession session, Class clazz){

        String seqname;

        if(User.class.equals(clazz)){
            seqname = "USER_SEQ";
        }
        else if(Group.class.equals(clazz)){
            seqname = "GROUP_SEQ";
        }
        else{
            throw new IllegalArgumentException("Sequence name not found for class: " + clazz);
        }

        List<Long> ids = selectNextId.execute(session, seqname);

        if(ids.size() != 1){
            throw new IllegalStateException(String.format("NEXTVAL for sequence: %s returend %d tuples, expected 1", seqname, ids.size()));
        }

        return ids.get(0);
    }
}
