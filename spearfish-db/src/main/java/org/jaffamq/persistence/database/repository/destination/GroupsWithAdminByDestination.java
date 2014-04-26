package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.repository.group.GroupMapper;
import org.jaffamq.persistence.database.sql.SelectOperationWithMapper;

import java.sql.Types;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 2014-04-19.
 */
public class GroupsWithAdminByDestination extends SelectOperationWithMapper<Group> {

    public GroupsWithAdminByDestination() {
        super("GroupsWithAdminByDestination", "SELECT * FROM security_group WHERE id IN(SELECT id_group FROM destination_and_group_with_admin " +
                "WHERE id_destination = ?)", new GroupMapper(), BIGINT);
    }
}
