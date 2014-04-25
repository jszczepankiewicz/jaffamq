package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.sql.SelectOperation;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 15.01.14.
 */
public abstract class SelectUserOperation extends SelectOperation<User> {

    protected SelectUserOperation(String preparedStatementName, String sql, Integer... parameters) {
        super(preparedStatementName, sql, parameters);
    }

    @Override
    protected User mapResult(ResultSet rs, int rowNumber) throws SQLException {

        User user = new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("passhash"),
                CalendarUtils.toDateTime(rs.getLong("creationtime")));

        return user;
    }
}
