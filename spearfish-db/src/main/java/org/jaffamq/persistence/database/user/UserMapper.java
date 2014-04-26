package org.jaffamq.persistence.database.user;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.Mapper;
import org.jaffamq.persistence.database.Table;

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

        return new User.Builder(
                rs.getString("login"))
                .creationtime(CalendarUtils.toDateTime(rs.getLong("creationtime")))
                .id(rs.getLong("id"))
                .passwordhash(rs.getString("passhash"))
                .build();
    }
}
