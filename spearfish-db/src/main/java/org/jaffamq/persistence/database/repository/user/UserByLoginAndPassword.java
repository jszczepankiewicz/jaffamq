package org.jaffamq.persistence.database.repository.user;

import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 07.01.14.
 */
public class UserByLoginAndPassword extends SelectUserOperation {

    public UserByLoginAndPassword() {
        super("UserByloginAndPassword", "SELECT * FROM security_user WHERE login=? AND passhash=?", VARCHAR, VARCHAR);
    }
}
