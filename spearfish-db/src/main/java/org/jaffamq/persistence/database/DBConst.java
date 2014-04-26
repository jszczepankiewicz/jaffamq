package org.jaffamq.persistence.database;

import org.jaffamq.persistence.database.destination.DestinationMapper;
import org.jaffamq.persistence.database.group.GroupMapper;
import org.jaffamq.persistence.database.user.UserMapper;

/**
 * Some common constants used in interaction with persistence layer.
 */
public class DBConst {

    public static final int NO_LIMIT = -1;
    public static final int NO_OFFSET = 0;

    public static final GroupMapper GROUP_MAPPER = new GroupMapper();
    public static final UserMapper USER_MAPPER = new UserMapper();
    public static final DestinationMapper DESTINATION_MAPPER = new DestinationMapper();
    public static final LongMapper LONG_MAPPER = new LongMapper();

    private DBConst() {
        //  no instantiation allowed
    }
}
