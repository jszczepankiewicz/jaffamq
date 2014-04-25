package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.sql.DBConst;
import org.jaffamq.persistence.database.sql.SelectOperation;
import org.jaffamq.persistence.database.sql.SelectOperationWithMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 12.01.14.
 * TODO: change my package to repository that is using this query
 */
public class GroupsByUser extends SelectOperationWithMapper<Group> {

    public GroupsByUser() {
        super("GroupsByUser", "SELECT g.id AS id, g.name AS name FROM security_user_and_group suag" +
                " INNER JOIN security_group g ON (suag.id_group = g.id) WHERE id_user=?", DBConst.GROUP_MAPPER, BIGINT);
    }

}
