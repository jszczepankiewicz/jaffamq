package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.sql.DBConst;
import org.jaffamq.persistence.database.sql.SelectOperationWithMapper;

import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 07.01.14.
 */
public class UserByLoginAndPassword extends SelectOperationWithMapper<User> {

    public UserByLoginAndPassword() {
        super("UserByloginAndPassword", "SELECT * FROM security_user WHERE login=? AND passhash=?", DBConst.USER_MAPPER, VARCHAR, VARCHAR);
    }
}
