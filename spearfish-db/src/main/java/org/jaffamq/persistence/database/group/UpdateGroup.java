package org.jaffamq.persistence.database.group;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 13.04.14.
 */
public class UpdateGroup extends UpdateOperation {

    public UpdateGroup() {
        super("UpdateGroup", "UPDATE security_group SET name=? WHERE id=?", VARCHAR, BIGINT);
    }
}
