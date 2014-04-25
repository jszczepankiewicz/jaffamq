package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;

import java.sql.Types;

/**
 * Created by urwisy on 2014-04-22.
 */
public class InsertMN extends UpdateOperation{

    public InsertMN(Table table, String column1, String column2) {
        super("InsertMN" + table, "INSERT INTO " + Table.sqlTableNameOf(table) + " (" + column1 + "," + column2 + ") VALUES (?,?)", Types.BIGINT, Types.BIGINT);
    }

}
