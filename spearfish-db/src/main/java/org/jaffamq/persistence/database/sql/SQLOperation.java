package org.jaffamq.persistence.database.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
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

    public SQLOperation(String preparedStatementName, String sql){
        this.preparedStatementName = preparedStatementName;
        this.sql = sql;
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

}
