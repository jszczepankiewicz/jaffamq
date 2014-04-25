package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.repository.mappings.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Select operation that is using mapper to deserialize objects.
 */
public class SelectOperationWithMapper<T> extends SelectOperation<T> {

    private Mapper<T> mapper;

    public SelectOperationWithMapper(String preparedStatementName, String sql, Mapper<T> mapper, Integer... parameters) {

        super(preparedStatementName, sql, parameters);
        this.mapper = mapper;
    }

    @Override
    protected T mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return mapper.mapResult(rs, rowNumber);
    }
}
