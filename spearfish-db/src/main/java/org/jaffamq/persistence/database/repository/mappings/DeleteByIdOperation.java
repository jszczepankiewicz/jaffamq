package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
/**
 * Created by urwisy on 26.01.14.
 */
public class DeleteByIdOperation extends UpdateOperation{

    public DeleteByIdOperation(String tablename){
        super("DetelByIdOperationFrom" + tablename, "DELETE FROM " + tablename + " WHERE id = ?", BIGINT);
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
