package org.jaffamq.persistence.database.destination;

import org.jaffamq.persistence.database.sql.UpdateOperation;

import static java.sql.Types.BIGINT;
import static java.sql.Types.VARCHAR;

/**
 * Created on 2014-04-19.
 */
public class InsertDestination extends UpdateOperation {

    public InsertDestination() {
        super("InsertDestination", "INSERT INTO destination (id, name, creationtime, nature) VALUES (?,?,?,?)", BIGINT, VARCHAR, BIGINT, VARCHAR);
    }
}
