package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 19.01.14.
 */
public class InsertUser extends UpdateOperation {

    public InsertUser() {
        super("InsertUser", "INSERT INTO security_user (id, login, passhash, creationtime) values (?,?,?,?)", BIGINT, VARCHAR, VARCHAR, BIGINT);
    }

}
