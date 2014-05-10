package org.jaffamq.persistence.database.actor.group;

import org.jaffamq.persistence.database.actor.CrudRepositoryReadActor;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.group.GroupRepository;

import javax.sql.DataSource;

/**
 * Actor for read-only interaction with GroupRepository.
 * WARNING: it is using blocking sql repository so every request-reply access to this class MUST use timeout.
 */
public class GroupRepositoryReadActor extends CrudRepositoryReadActor<Group> {

    public GroupRepositoryReadActor(GroupRepository repository, DataSource dataSource) {
        super(repository, dataSource);
    }
}
