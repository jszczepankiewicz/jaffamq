package org.jaffamq.persistence.database.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 19.01.14.
 */
public class UpdateUserWithoutPassword extends UpdateOperation {

    public UpdateUserWithoutPassword() {
        super("UpdateUserWithoutPassword", "UPDATE security_user SET login=? WHERE id=?", VARCHAR, BIGINT);
    }
}
