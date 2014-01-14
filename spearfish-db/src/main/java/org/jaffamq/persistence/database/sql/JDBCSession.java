package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by urwisy on 06.01.14.
 */
public class JDBCSession {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCSession.class);

    private Connection connection;

    private Map<String, PreparedStatement> compiledStatements = new HashMap<>();

    public JDBCSession(Connection connection){
        this.connection = connection;
    }

    PreparedStatement getCompiledStatement(SQLOperation sql){

        String name = sql.getPreparedStatementName();
        PreparedStatement statement = compiledStatements.get(name);

        if(statement == null){

            LOG.debug("PreparedStatement: {} not found in this session, will create one", name);

            try {
                statement = connection.prepareStatement(sql.getSql());
            } catch (SQLException e) {
                throw new InternalException(Errors.PREPARED_STATEMENT_CREATION_EXCEPTION, e, "In getCompiledStatement");
            }

            compiledStatements.put(name, statement);

        }

        return statement;
    }

    public void dispose(){

        LOG.debug("Disposing JDBCSession with {} prepared statements", compiledStatements.size());

        for(String name:compiledStatements.keySet()){
            PreparedStatement statement = compiledStatements.get(name);
            try {
                statement.close();
            } catch (SQLException e) {
                LOG.warn("SQLException while closing prepared statement", e);
            }
        }
    }
}
