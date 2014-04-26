package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;

import static java.sql.Types.BIGINT;

/**
 * Created by urwisy on 2014-04-25.
 */
public class MNRelation {

    private final UpdateOperation insertRelation;
    private final UpdateOperation deleteRelation;

    public MNRelation(Table table, String column1, String column2) {
        insertRelation = new UpdateOperation("InsertMN" + table, "INSERT INTO " + Table.sqlTableNameOf(table) + " (" + column1 + "," + column2 + ") VALUES (?,?)", BIGINT, BIGINT);
        deleteRelation = new UpdateOperation("DeleteMN" + table, "DELETE FROM " + Table.sqlTableNameOf(table) + " WHERE " + column1 + "=? AND " + column2 + "=?", BIGINT, BIGINT);
    }

    public int add(JDBCSession session, Long id1, Long id2){
        return insertRelation.execute(session, id1, id2);
    }

    public int remove(JDBCSession session, Long id1, Long id2){
        return deleteRelation.execute(session, id1, id2);
    }


}
