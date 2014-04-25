package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.*;
import org.jaffamq.persistence.database.repository.mappings.*;

import java.sql.*;

/**
 * Created by urwisy on 2014-04-21.
 */
public class ListAllOperation<T> extends SelectOperationWithMapper<T> {

    public ListAllOperation(Mapper<T> mapper, String orderColumn) {
        super("ListAllOperation" + mapper.getTable() + orderColumn, "SELECT * FROM " +
                        Table.sqlTableNameOf(mapper.getTable()) + " ORDER BY " + orderColumn + "  LIMIT ? OFFSET ?", mapper,
                Types.INTEGER, Types.INTEGER
        );
    }
}
