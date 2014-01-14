package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by urwisy on 02.01.14.
 */
public abstract class SelectOperation<T> extends SQLOperation {

    public SelectOperation(String preparedStatementName, String sql) {
        super(preparedStatementName, sql);
    }

    public SelectOperation(String preparedStatementName, String sql, Integer...parameters) {

        super(preparedStatementName, sql);

        for(Integer type:parameters){
            addParameter(type);
        }
    }

    protected abstract T mapResult(ResultSet rs, int rowNumber) throws SQLException;

    protected void setParameterValues(PreparedStatement statement, Object...args) throws SQLException {

        for(int columnCounter=0; columnCounter<getParameters().size(); columnCounter++) {

            int type = getParameters().get(columnCounter).intValue();

            int parameterIndex = columnCounter+1;

            switch(type){
                case Types.LONGNVARCHAR:
                case Types.LONGVARCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.NCHAR:
                    String strval = (String)args[columnCounter];
                    statement.setString(parameterIndex, strval);
                    break;
                case Types.INTEGER:
                    Integer intval = (Integer)args[columnCounter];
                    statement.setInt(parameterIndex, intval);
                    break;
                default:
                    throw new IllegalStateException("Type: " + type + " is not implemented yet, you can add mapping here");
            }
        }
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

        PreparedStatement statement = session.getCompiledStatement(this);

        if(getParameters().size()!=args.length){
            throw new InternalException(Errors.PREPARED_STATEMENT_PARAMETERS_LENGTH_NOT_EQUAL, "for query: " + this.getPreparedStatementName() + ", expected: " + getParameters().size() + ", given: " + args.length);
        }

        try {
            setParameterValues(statement, args);
        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXCEPTION_WHILE_SET_VALUE_ON_STATEMENT, e, "for query: " + this.getPreparedStatementName());
        }

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
            throw new InternalException(Errors.SQL_EXCEPTION_ON_LOOPING_RESULT_SET, e, "for query: " + this.getPreparedStatementName());
        }

        LOG.debug("Query returned {} tuple(s)", counter);

        return results;
    }
}
