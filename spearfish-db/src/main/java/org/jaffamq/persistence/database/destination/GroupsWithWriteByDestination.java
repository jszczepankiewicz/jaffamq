package org.jaffamq.persistence.database.destination;

import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.group.GroupMapper;
import org.jaffamq.persistence.database.sql.SelectOperation;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 2014-04-19.
 */
public class GroupsWithWriteByDestination extends SelectOperation<Group> {

    public GroupsWithWriteByDestination() {
        super("GroupsWithWriteByDestination", "SELECT * FROM security_group WHERE id IN(SELECT id_group FROM destination_and_group_with_write " +
                "WHERE id_destination = ?)", new GroupMapper(), BIGINT);
    }
}
