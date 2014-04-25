package org.jaffamq.persistence.database.repository.destination;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import java.sql.Types;

/**
 * Created on 2014-04-19.
 */
public class InsertDestination extends UpdateOperation {

    public InsertDestination() {
        super("InsertDestination", "INSERT INTO destination (id, name, creationtime) VALUES (?,?,?)", Types.BIGINT, Types.VARCHAR, Types.BIGINT);
    }
}
