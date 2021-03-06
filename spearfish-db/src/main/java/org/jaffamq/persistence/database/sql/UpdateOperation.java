package org.jaffamq.persistence.database.sql;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Threadsafe sql operation object responsible for inserting something new into db.
 */
public class UpdateOperation extends SQLOperation {

    public UpdateOperation(String preparedStatementName, String sql, Integer... args) {
        super(preparedStatementName, sql, args);
    }

    public int execute(JDBCSession session, Object... args) {

        PreparedStatement statement = getStatement(session, args);

        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new InternalException(Errors.SQL_EXCEPTION_ON_EXECUTE_UPDATE, e, "for query: " + this.getPreparedStatementName());
        }

    }


}
