package org.jaffamq.persistence.database.repository.destination;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.CheckEntityUnique;
import org.jaffamq.persistence.database.repository.CrudRepository;
import org.jaffamq.persistence.database.repository.IdentityProvider;
import org.jaffamq.persistence.database.repository.NotFoundInSetPredicate;
import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.sql.CountEntityByName;
import org.jaffamq.persistence.database.sql.DeleteByIdOperation;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.jaffamq.persistence.database.sql.ListAllOperation;
import org.jaffamq.persistence.database.sql.MNRelation;
import org.jaffamq.persistence.database.sql.SelectByIdOperation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by urwisy on 15.04.14.
 */
public class DestinationRepository implements CrudRepository<Destination>, CheckEntityUnique {

    private DeleteByIdOperation deleteById = new DeleteByIdOperation(Table.DESTINATION);
    private UpdateDestination updateDestination = new UpdateDestination();

    private SelectByIdOperation<Destination> selectById = new SelectByIdOperation<>(DBConst.DESTINATION_MAPPER);

    private GroupsWithAdminByDestination groupsWithAdmin = new GroupsWithAdminByDestination();
    private GroupsWithReadByDestination groupsWithRead = new GroupsWithReadByDestination();
    private GroupsWithWriteByDestination groupsWithWrite = new GroupsWithWriteByDestination();
    private InsertDestination insertDestination = new InsertDestination();

    private ListAllOperation listAll = new ListAllOperation(DBConst.DESTINATION_MAPPER, "name");
    private CountEntityByName countEntityByName = new CountEntityByName(Table.DESTINATION);

    private MNRelation groupsToReadFromDestination = new MNRelation(Table.DESTINATION_AND_GROUP_WITH_READ, "id_destination", "id_group");
    private MNRelation groupsWithWriteToDestination = new MNRelation(Table.DESTINATION_AND_GROUP_WITH_WRITE, "id_destination", "id_group");
    private MNRelation groupsWithAdminToDestination = new MNRelation(Table.DESTINATION_AND_GROUP_WITH_ADMIN, "id_destination", "id_group");

    @Override
    public boolean isUnique(JDBCSession session, String name) {
        return countEntityByName.executeSingle(session, name) == 0;
    }

    @Override
    public Destination get(JDBCSession session, Long id) {

        Destination retval = selectById.executeEntity(session, id);

        if (retval == null) {
            return retval;
        }

        decorateWithRelations(session, Arrays.asList(retval));

        return retval;
    }

    private void decorateWithRelations(JDBCSession session, List<Destination> destinations) {

        for (Destination destination : destinations) {
            destination.setAdminAuthorizedGroups(new HashSet<>(groupsWithAdmin.execute(session, destination.getId())));
            destination.setReadAuthorizedGroups(new HashSet<>(groupsWithRead.execute(session, destination.getId())));
            destination.setWriteAuthorizedGroups(new HashSet<>(groupsWithWrite.execute(session, destination.getId())));
        }
    }

    @Override
    public boolean delete(JDBCSession session, Long id) {
        return deleteById.execute(session, id) > 0;
    }

    @Override
    public Long create(JDBCSession session, Destination toCreate) {

        Preconditions.checkNotNull(toCreate, "Object to persist should not be null");
        Preconditions.checkArgument(toCreate.getId() == null, "Object to create should not have identity set");

        Long id = IdentityProvider.getNextIdFor(session, Destination.class);
        insertDestination.execute(session, id, toCreate.getName(), CalendarUtils.nowAsLong(), String.valueOf(toCreate.getType().toValue()));

        for (Group group : toCreate.getReadAuthorizedGroups()) {
            groupsToReadFromDestination.add(session, id, group.getId());
        }

        for (Group group : toCreate.getWriteAuthorizedGroups()) {
            groupsWithWriteToDestination.add(session, id, group.getId());
        }

        for (Group group : toCreate.getAdminAuthorizedGroups()) {
            groupsWithAdminToDestination.add(session, id, group.getId());
        }

        return id;
    }

    private void synchronizeRelationsToGroups(JDBCSession session, MNRelation relation, Set<Group> persisted, Set<Group> toPersist, Long destinationId) {

        Set<Group> diffToPersist = Sets.filter(toPersist, new NotFoundInSetPredicate(persisted));
        Set<Group> diffToRemove = Sets.filter(persisted, new NotFoundInSetPredicate(toPersist));

        for (Group group : diffToPersist) {
            relation.add(session, destinationId, group.getId());
        }

        for (Group group : diffToRemove) {
            relation.remove(session, destinationId, group.getId());
        }
    }

    @Override
    public boolean update(JDBCSession session, Destination toUpdate) {

        Preconditions.checkNotNull(toUpdate, "Object to update should not be null");
        Preconditions.checkArgument(toUpdate.getId() != null, "Object to update should have identity set");

        boolean updated = updateDestination.execute(session, toUpdate.getName(), toUpdate.getId()) > 0;

        if (updated) {
            //  change assignments
            Destination updatedDestination = get(session, toUpdate.getId());

            synchronizeRelationsToGroups(session, groupsToReadFromDestination,
                    updatedDestination.getReadAuthorizedGroups(), toUpdate.getReadAuthorizedGroups(), toUpdate.getId());
            synchronizeRelationsToGroups(session, groupsWithWriteToDestination,
                    updatedDestination.getWriteAuthorizedGroups(), toUpdate.getWriteAuthorizedGroups(), toUpdate.getId());
            synchronizeRelationsToGroups(session, groupsWithAdminToDestination,
                    updatedDestination.getAdminAuthorizedGroups(), toUpdate.getAdminAuthorizedGroups(), toUpdate.getId());

        }

        return updated;
    }

    @Override
    public List<Destination> list(JDBCSession session, int limit, int offset) {
        List<Destination> results = listAll.execute(session, limit, offset);
        decorateWithRelations(session, results);
        return results;
    }


}
