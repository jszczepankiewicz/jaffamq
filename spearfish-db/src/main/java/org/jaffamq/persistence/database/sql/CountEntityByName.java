package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;

import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 2014-04-21.
 */
public class CountEntityByName extends SelectLongOperation {

    public CountEntityByName(Table table) {
        this(table, "name");
    }

    public CountEntityByName(Table table, String columnWithName) {
        super("CountEntityByName", "SELECT count(*) FROM " + Table.sqlTableNameOf(table) + " WHERE LOWER(" + columnWithName + ")=LOWER(?)", VARCHAR);
    }
}
