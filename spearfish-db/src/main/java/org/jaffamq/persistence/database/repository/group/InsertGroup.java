package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created by urwisy on 13.04.14.
 */
public class InsertGroup  extends UpdateOperation {

    public InsertGroup() {
        super("InsertGroup", "INSERT INTO security_group (id, name) VALUES (?,?)", Types.BIGINT, Types.VARCHAR);
    }
}
