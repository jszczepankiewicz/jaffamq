package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.mappings.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 2014-04-21.
 */
public class UserMapper extends Mapper<User> {

    public UserMapper() {
        super(Table.USER);
    }

    @Override
    public User mapResult(ResultSet rs, int rowNumber) throws SQLException {

        return new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("passhash"),
                CalendarUtils.toDateTime(rs.getLong("creationtime")));
    }
}
