package org.jaffamq.persistence.database.repository.mappings.user;

import org.jaffamq.persistence.database.repository.mappings.SelectUserOperation;

import java.sql.Types;

/**
 * Created by urwisy on 19.01.14.
 */
public class SelectUserById extends SelectUserOperation {

    public SelectUserById() {
        super("SelectUserById", "SELECT * FROM security_user WHERE id = ?", Types.BIGINT);
    }
}
