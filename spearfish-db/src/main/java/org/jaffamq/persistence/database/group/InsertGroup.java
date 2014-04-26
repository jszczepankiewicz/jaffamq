package org.jaffamq.persistence.database.group;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 13.04.14.
 */
public class InsertGroup extends UpdateOperation {

    public InsertGroup() {
        super("InsertGroup", "INSERT INTO security_group (id, name, creationtime) VALUES (?,?,?)", BIGINT, VARCHAR, BIGINT);
    }
}
