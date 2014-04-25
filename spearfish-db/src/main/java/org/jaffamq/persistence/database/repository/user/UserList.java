package org.jaffamq.persistence.database.repository.user;

import static java.sql.Types.INTEGER;

/**
 * Created by urwisy on 15.01.14.
 */
public class UserList extends SelectUserOperation {

    public UserList() {
        super("UserList", "SELECT * FROM security_user LIMIT ? OFFSET ?", INTEGER, INTEGER);
    }

}
