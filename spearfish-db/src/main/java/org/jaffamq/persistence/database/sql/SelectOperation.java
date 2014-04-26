package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;
import org.jaffamq.persistence.database.Mapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * All select operations that takes some arguments and returns single or multiple results.
 */
public abstract class SelectOperation<T> extends SQLOperation {

    private Mapper<T> mapper;

    public SelectOperation(String preparedStatementName, String sql, Mapper<T> mapper) {
        super(preparedStatementName, sql);
        this.mapper = mapper;
    }

    public SelectOperation(String preparedStatementName, String sql, Mapper<T> mapper, Integer... parameters) {
        super(preparedStatementName, sql, parameters);
        this.mapper = mapper;
    }

    protected T mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return mapper.mapResult(rs, rowNumber);
    }

    /**
     * Execute select and return first row mapped.
     *
     * @param session
     * @param args
     * @return
     * @throws java.lang.IllegalStateException when number of results different than 1
     */
    public T executeSingle(JDBCSession session, Object... args) {

        List<T> results = execute(session, args);

        if (results.size() != 1) {
            throw new IllegalStateException("Expected 1 result found " + results.size());
        }

        return results.get(0);
    }


    public T executeEntity(JDBCSession session, Object... args) {

        List<T> tuples = execute(session, args);

        if (tuples.size() == 0) {
            return null;
        }

        if (tuples.size() > 1) {
            throw new IllegalStateException("ex");
        }

        return tuples.get(0);
    }

    public List<T> execute(JDBCSession session, Object... args) {

        // here
        PreparedStatement statement = getStatement(session, args);

        ResultSet rs;

        try {
            rs = statement.executeQuery();

        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXECUTE_QUERY_FAILED, e, "for query: " + this.getPreparedStatementName());
        }

        List<T> results = new ArrayList<T>();

        int counter = 0;
        try {
            while (rs.next()) {
                results.add(mapResult(rs, counter++));
            }
        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXCEPTION_ON_LOOPING_RESULT_SET, e, "for query name: " + this.getPreparedStatementName()
                    + ", sql: [" + this.getSql() + "]");
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.warn("SQLException while closing ResultSet", e);
            }
        }

        LOG.debug("Query returned {} tuple(s)", counter);

        return results;
    }


}
