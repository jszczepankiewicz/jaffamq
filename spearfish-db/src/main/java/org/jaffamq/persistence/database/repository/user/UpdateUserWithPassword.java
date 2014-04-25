package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created by urwisy on 2014-04-23.
 */
public class UpdateUserWithPassword  extends UpdateOperation {

    public UpdateUserWithPassword() {
        super("UpdateUserWithPassword", "UPDATE security_user SET login=?,passhash=? WHERE id=?", Types.VARCHAR, Types.VARCHAR, Types.BIGINT);
    }
}
