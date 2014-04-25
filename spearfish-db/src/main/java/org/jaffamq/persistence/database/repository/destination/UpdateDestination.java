package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created on 2014-04-19.
 */
public class UpdateDestination extends UpdateOperation{

    public UpdateDestination() {
        super("UpdateDestination", "UPDATE destination SET name=? WHERE id=?", Types.VARCHAR, Types.BIGINT);
    }
}
