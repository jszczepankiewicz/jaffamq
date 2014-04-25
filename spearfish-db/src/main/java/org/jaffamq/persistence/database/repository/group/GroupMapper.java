package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.mappings.Mapper;

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
        return new Group(rs.getLong("id"), rs.getString("name"));
    }
}