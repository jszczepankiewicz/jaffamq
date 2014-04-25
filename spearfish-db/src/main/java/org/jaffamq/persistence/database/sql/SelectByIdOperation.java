package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.repository.mappings.Mapper;
import org.jaffamq.persistence.database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Returns entity of type T for provided table name and mapper by querieng by ID of type Long
 */
public class SelectByIdOperation<T> extends SelectOperation<T>{

    private Mapper<T> mapper;

    public SelectByIdOperation(Mapper<T> mapper) {

        super("SelectByIdOperation" + Table.sqlTableNameOf(mapper.getTable()), String.format("SELECT * FROM %s WHERE id=?", Table.sqlTableNameOf(mapper.getTable())), Types.BIGINT);
        this.mapper = mapper;
    }

    @Override
    protected T mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return mapper.mapResult(rs, rowNumber);
    }
}
