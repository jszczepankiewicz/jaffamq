package org.jaffamq.persistence.database.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 2014-04-23.
 */
public class UpdateUserWithPassword extends UpdateOperation {

    public UpdateUserWithPassword() {
        super("UpdateUserWithPassword", "UPDATE security_user SET login=?,passhash=? WHERE id=?", VARCHAR, VARCHAR, BIGINT);
    }
}
