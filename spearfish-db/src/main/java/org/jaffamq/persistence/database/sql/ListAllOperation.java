package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.Mapper;

import static java.sql.Types.INTEGER;

/**
 * Created by urwisy on 2014-04-21.
 */
public class ListAllOperation<T> extends SelectOperation<T> {

    public ListAllOperation(Mapper<T> mapper, String orderColumn) {
        super("ListAllOperation" + mapper.getTable() + orderColumn, "SELECT * FROM " +
                        Table.sqlTableNameOf(mapper.getTable()) + " ORDER BY " + orderColumn + "  LIMIT ? OFFSET ?", mapper,
                INTEGER, INTEGER
        );
    }
}
