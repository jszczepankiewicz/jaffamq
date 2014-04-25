package org.jaffamq.persistence.database.repository.group;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created by urwisy on 13.04.14.
 */
public class UpdateGroup extends UpdateOperation {

    public UpdateGroup() {
        super("UpdateGroup", "UPDATE security_group SET name=? WHERE id=?", Types.VARCHAR, Types.BIGINT);
    }
}
