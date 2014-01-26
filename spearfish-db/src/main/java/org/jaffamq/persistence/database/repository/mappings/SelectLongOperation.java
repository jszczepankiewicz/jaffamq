package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.sql.JDBCSession;
import org.jaffamq.persistence.database.sql.SelectOperation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by urwisy on 26.01.14.
 */
public abstract class SelectLongOperation extends SelectOperation<Long>{

    public SelectLongOperation(String statement, Integer...args) {
        super("SelectLongOperation", statement, args);
    }


    @Override
    protected Long mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return rs.getLong(1);
    }
}
