package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.repository.destination.DestinationMapper;
import org.jaffamq.persistence.database.repository.group.GroupMapper;
import org.jaffamq.persistence.database.repository.user.UserMapper;

/**
 * Some common constants used in interaction with persistence layer.
 */
public class DBConst {

    public static final int NO_LIMIT = -1;
    public static final int NO_OFFSET = 0;

    public static final GroupMapper GROUP_MAPPER = new GroupMapper();
    public static final UserMapper USER_MAPPER = new UserMapper();
    public static final DestinationMapper DESTINATION_MAPPER = new DestinationMapper();

    private DBConst() {
        //  no instantiation allowed
    }
}
