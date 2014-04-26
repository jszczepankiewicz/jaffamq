package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 2014-04-26.
 */
public class LongMapper extends Mapper<Long> {

    public LongMapper() {
        super(null);
    }

    @Override
    public Table getTable() {
        throw new UnsupportedOperationException("LongMapper can not be used is not associated with ");
    }

    @Override
    public Long mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return rs.getLong(1);
    }
}
