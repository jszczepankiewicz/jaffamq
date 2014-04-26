package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.sql.SelectOperation;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 12.01.14.
 */
public class GroupsByUser extends SelectOperation<Group> {

    public GroupsByUser() {
        super("GroupsByUser", "SELECT g.id AS id, g.name AS name, g.creationtime AS creationtime FROM security_user_and_group suag" +
                " INNER JOIN security_group g ON (suag.id_group = g.id) WHERE id_user=?", DBConst.GROUP_MAPPER, BIGINT);
    }

}
