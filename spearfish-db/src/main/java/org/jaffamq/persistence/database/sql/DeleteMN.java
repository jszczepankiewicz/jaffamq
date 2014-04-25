package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;

import java.sql.Types;

/**
 * Created by urwisy on 2014-04-22.
 */
public class DeleteMN extends UpdateOperation{

    public DeleteMN(Table table, String column1, String column2) {
        super("DeleteMN" + table, "DELETE FROM " + Table.sqlTableNameOf(table) + " WHERE " + column1 + "=? AND " + column2 + "=?", Types.BIGINT, Types.BIGINT);
    }
}
