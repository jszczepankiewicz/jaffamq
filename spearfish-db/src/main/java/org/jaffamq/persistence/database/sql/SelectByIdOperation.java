package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Mapper;
import org.jaffamq.persistence.database.Table;

import static java.sql.Types.BIGINT;

/**
 * Returns entity of type T for provided table name and mapper by querieng by ID of type Long
 */
public class SelectByIdOperation<T> extends SelectOperation<T> {

    public SelectByIdOperation(Mapper<T> mapper) {
        super("SelectByIdOperation" + Table.sqlTableNameOf(mapper.getTable()),
                String.format("SELECT * FROM %s WHERE id=?", Table.sqlTableNameOf(mapper.getTable())), mapper, BIGINT);
    }

}
