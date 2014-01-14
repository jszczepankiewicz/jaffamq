package org.jaffamq.persistence.database.sql;

/**
 * Created by urwisy on 02.01.14.
 */
public class UpdateOperation extends SQLOperation{

    public UpdateOperation(String preparedStatementName, String sql) {
        super(preparedStatementName, sql);
    }
}
