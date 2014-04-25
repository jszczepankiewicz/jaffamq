package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
/**
 * Created by urwisy on 26.01.14.
 */
public class DeleteByIdOperation extends UpdateOperation{

    public DeleteByIdOperation(Table table){
        super("DeleteByIdOperationFrom" + Table.sqlTableNameOf(table), "DELETE FROM " + Table.sqlTableNameOf(table) + " WHERE id = ?", BIGINT);
    }
    /**
     *
     * @param tablename name of the table to remove tuple from
     * @param idcolumn name of id column
     */
    public DeleteByIdOperation(String tablename, String idcolumn) {
        super("DetelByIdOperationFrom" + tablename, "DELETE FROM " + tablename + " WHERE " + idcolumn + " = ?", BIGINT);
    }

}
