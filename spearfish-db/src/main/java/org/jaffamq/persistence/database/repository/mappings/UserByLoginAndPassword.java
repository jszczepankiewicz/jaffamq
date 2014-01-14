package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.dto.User;
import org.jaffamq.persistence.database.sql.SelectOperation;
import org.joda.time.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 07.01.14.
 */
public class UserByLoginAndPassword extends SelectOperation<User>{

    public UserByLoginAndPassword() {
        super("UserByloginAndPassword", "SELECT * FROM security_user WHERE login=? AND passhash=?", VARCHAR, VARCHAR);
    }

    @Override
    protected User mapResult(ResultSet rs, int rowNumber) throws SQLException {

        User user = new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("passhash"),
                DateTimeZone.forOffsetMillis(rs.getInt("creationtime")));

        return user;
    }
}
