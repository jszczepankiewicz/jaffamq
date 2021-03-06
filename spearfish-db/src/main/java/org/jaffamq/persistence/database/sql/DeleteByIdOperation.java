package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 26.01.14.
 */
public class DeleteByIdOperation extends UpdateOperation {

    public DeleteByIdOperation(Table table) {
        super("DeleteByIdOperationFrom" + Table.sqlTableNameOf(table), "DELETE FROM " + Table.sqlTableNameOf(table) + " WHERE id = ?", BIGINT);
    }

}
