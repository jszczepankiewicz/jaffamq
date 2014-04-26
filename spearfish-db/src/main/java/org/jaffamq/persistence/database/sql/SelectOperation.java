package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;
import scala.annotation.meta.param;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: check if we can remove completely this object and replace this with SelectOperationWithMapper
 */
public abstract class SelectOperation<T> extends SQLOperation {

    public SelectOperation(String preparedStatementName, String sql) {
        super(preparedStatementName, sql);
    }

    public SelectOperation(String preparedStatementName, String sql, Integer...parameters) {
        super(preparedStatementName, sql, parameters);
    }

    protected abstract T mapResult(ResultSet rs, int rowNumber) throws SQLException;

    /**
     * Execute select and return first row mapped.
     * @param session
     * @param args
     * @throws java.lang.IllegalStateException when number of results different than 1
     * @return
     */
    public T executeSingle(JDBCSession session, Object...args){

        List<T> results = execute(session, args);

        if(results.size()!=1){
            throw new IllegalStateException("Expected 1 result found " + results.size());
        }

        return results.get(0);
    }


    public T executeEntity(JDBCSession session, Object...args){

        List<T> tuples = execute(session, args);

        if(tuples.size() == 0){
            return null;
        }

        if(tuples.size() > 1){
            throw new IllegalStateException("ex");
        }

        return tuples.get(0);
    }

    public List<T> execute(JDBCSession session, Object...args){

        // here
        PreparedStatement statement = getStatement(session, args);

        ResultSet rs;

        try {
            rs = statement.executeQuery();

        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXECUTE_QUERY_FAILED, e, "for query: " + this.getPreparedStatementName());
        }

        List<T> results = new ArrayList<T>();

        int counter=0;
        try {
            while (rs.next()) {
                results.add(mapResult(rs, counter++));
            }
        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXCEPTION_ON_LOOPING_RESULT_SET, e, "for query name: " + this.getPreparedStatementName()
            + ", sql: [" + this.getSql()+ "]");
        }
        finally{
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
