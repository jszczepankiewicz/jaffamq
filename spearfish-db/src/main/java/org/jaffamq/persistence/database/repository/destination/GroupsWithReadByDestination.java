package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.repository.group.GroupMapper;
import org.jaffamq.persistence.database.sql.SelectOperation;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 2014-04-19.
 */
public class GroupsWithReadByDestination extends SelectOperation<Group> {

    public GroupsWithReadByDestination() {
        super("GroupsWithReadByDestination", "SELECT * FROM security_group WHERE id IN(SELECT id_group FROM destination_and_group_with_read " +
                "WHERE id_destination = ?)", new GroupMapper(), BIGINT);
    }
}
