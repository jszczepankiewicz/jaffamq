package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by urwisy on 02.01.14.
 */
public abstract class SQLOperation{

    private final String preparedStatementName;

    private final String sql;

    private List<Integer> parameters = new ArrayList<>();

    protected static final Logger LOG = LoggerFactory.getLogger(SQLOperation.class);

    public SQLOperation(String preparedStatementName, String sql) {
        this.preparedStatementName = preparedStatementName;
        this.sql = sql;
    }


    public SQLOperation(String preparedStatementName, String sql, Integer...parameters) {

        this(preparedStatementName, sql);

        for(Integer type:parameters){
            addParameter(type);
        }
    }


    public String getSql(){
        return sql;
    }

    public String getPreparedStatementName(){
        return preparedStatementName;
    }

    /**
     * Add parameter using some java.sql.Types constant
     * @param type
     */
    protected void addParameter(Integer type){
        parameters.add(type);
    }

    protected List<Integer> getParameters(){
        return parameters;
    }


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
                    statement.setString(parameterIndex, (String)args[columnCounter]);
                    break;
                case Types.BIGINT:
                    statement.setLong(parameterIndex, (Long)args[columnCounter]);
                    break;
                case Types.INTEGER:
                    statement.setInt(parameterIndex, (Integer)args[columnCounter]);
                    break;
                default:
                    throw new IllegalStateException("Type: " + type + " is not implemented yet, you can add mapping here");
            }
        }
    }

    protected PreparedStatement getStatement(JDBCSession session, Object[] args) {

        PreparedStatement statement = session.getCompiledStatement(this);

        if(getParameters().size()!=args.length){
            throw new InternalException(Errors.PREPARED_STATEMENT_PARAMETERS_LENGTH_NOT_EQUAL, "for query: " + this.getPreparedStatementName() + ", expected: " + getParameters().size() + ", given: " + args.length);
        }

        try {
            setParameterValues(statement, args);
        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXCEPTION_WHILE_SET_VALUE_ON_STATEMENT, e, "for query: " + this.getPreparedStatementName());
        }

        return statement;
    }
}
