package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.dto.Group;
import org.jaffamq.persistence.database.sql.SelectOperation;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.BIGINT;
import static java.sql.Types.INTEGER;

/**
 * Created by urwisy on 12.01.14.
 */
public class GroupsByUser extends SelectOperation<Group> {

    public GroupsByUser() {
        super("GroupsByUser", "SELECT g.id AS id, g.name AS name FROM security_user_and_group suag" +
                " INNER JOIN security_group g ON (suag.id_group = g.id) WHERE id_user=?", INTEGER);
    }

    @Override
    protected Group mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return new Group(rs.getLong("id"), rs.getString("name"));
    }
}
