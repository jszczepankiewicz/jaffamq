package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created by urwisy on 19.01.14.
 */
public class UpdateUserWithoutPassword extends UpdateOperation {

    public UpdateUserWithoutPassword() {
        super("UpdateUserWithoutPassword", "UPDATE security_user SET login=? WHERE id=?", Types.VARCHAR, Types.BIGINT);
    }
}
