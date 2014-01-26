package org.jaffamq.persistence.database.repository.mappings.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 25.01.14.
 */
public class InsertGroupAndUser extends UpdateOperation {

    public InsertGroupAndUser() {
        super("InsertGroupAndUser", "INSERT INTO security_user_and_group (id_user, id_group) VALUES (?,?)", BIGINT, BIGINT);
    }
}
