package org.jaffamq.persistence.database.group;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.Mapper;
import org.jaffamq.persistence.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapping for group
 */
public class GroupMapper extends Mapper<Group> {

    public GroupMapper() {
        super(Table.GROUP);
    }

    @Override
    public Group mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return new Group.Builder(
                rs.getString("name"))
                .id(rs.getLong("id"))
                .creationtime(CalendarUtils.toDateTime(rs.getLong("creationtime")))
                .build();
    }
}
