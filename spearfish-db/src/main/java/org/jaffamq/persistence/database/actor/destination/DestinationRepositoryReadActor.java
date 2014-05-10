package org.jaffamq.persistence.database.actor.destination;

import org.jaffamq.persistence.database.actor.CrudRepositoryReadActor;
import org.jaffamq.persistence.database.destination.Destination;
import org.jaffamq.persistence.database.destination.DestinationRepository;

import javax.sql.DataSource;

/**
 * Created by urwisy on 2014-05-04.
 */
public class DestinationRepositoryReadActor extends CrudRepositoryReadActor<Destination> {

    public DestinationRepositoryReadActor(DestinationRepository repository, DataSource dataSource) {
        super(repository, dataSource);
    }
}
