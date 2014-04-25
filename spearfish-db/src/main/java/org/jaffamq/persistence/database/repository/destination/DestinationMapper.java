package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.mappings.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 15.04.14.
 */
public class DestinationMapper extends Mapper<Destination> {

    public DestinationMapper() {
        super(Table.DESTINATION);
    }

    @Override
    public Destination mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return new Destination(rs.getLong("Id"), rs.getString("name"), CalendarUtils.toDateTime(rs.getLong("creationtime")));
    }
}
