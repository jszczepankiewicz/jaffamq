package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.dto.User;
import org.jaffamq.persistence.database.sql.SelectOperation;
import org.joda.time.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 15.01.14.
 */
public abstract class SelectUserOperation  extends SelectOperation<User> {

    protected SelectUserOperation(String preparedStatementName, String sql, Integer... parameters) {
        super(preparedStatementName, sql, parameters);
    }

    @Override
    protected User mapResult(ResultSet rs, int rowNumber) throws SQLException {

        User user = new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("passhash"),
                DateTimeZone.forOffsetMillis(rs.getInt("creationtime")));

        return user;
    }
}
