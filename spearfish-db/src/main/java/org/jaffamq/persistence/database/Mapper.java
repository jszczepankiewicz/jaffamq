package org.jaffamq.persistence.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by urwisy on 13.04.14.
 */
public abstract class Mapper<T> {

    private Table table;

    protected Mapper(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public abstract T mapResult(ResultSet rs, int rowNumber) throws SQLException;
}
