package org.jaffamq.persistence.database.group;

import com.google.common.base.Preconditions;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.CheckEntityUnique;
import org.jaffamq.persistence.database.CrudRepository;
import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.IdentityProvider;
import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.sql.CountEntityByName;
import org.jaffamq.persistence.database.sql.DeleteByIdOperation;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.jaffamq.persistence.database.sql.ListAllOperation;
import org.jaffamq.persistence.database.sql.SelectByIdOperation;

import java.util.List;

/**
 * Repository for operation on groups
 */
public class GroupRepository implements CrudRepository<Group>, CheckEntityUnique {

    private DeleteByIdOperation deleteById = new DeleteByIdOperation(Table.GROUP);
    private SelectByIdOperation<Group> selectById = new SelectByIdOperation<>(DBConst.GROUP_MAPPER);
    private InsertGroup insertGroup = new InsertGroup();
    private ListAllOperation listAll = new ListAllOperation(DBConst.GROUP_MAPPER, "name");
    private UpdateGroup updateGroup = new UpdateGroup();
    private CountEntityByName countEntityByName = new CountEntityByName(Table.GROUP);

    @Override
    public List<Group> list(JDBCSession session, int limit, int offset) {
        return listAll.execute(session, limit, offset);
    }

    @Override
    public Group get(JDBCSession session, Long id) {
        return selectById.executeEntity(session, id);
    }

    @Override
    public boolean delete(JDBCSession session, Long id) {
        return deleteById.execute(session, id) > 0;
    }

    @Override
    public Long create(JDBCSession session, Group toCreate) {

        Preconditions.checkNotNull(toCreate, "Object to persist should not be null");
        Preconditions.checkArgument(toCreate.getId() == null, "Object to create should not have identity set");

        Long id = IdentityProvider.getNextIdFor(session, Group.class);

        insertGroup.execute(session, id, toCreate.getName(), CalendarUtils.asLong(toCreate.getCreationtime()));
        return id;
    }

    @Override
    public boolean update(JDBCSession session, Group toUpdate) {

        Preconditions.checkNotNull(toUpdate, "Object to update should not be null");
        Preconditions.checkArgument(toUpdate.getId() != null, "Object to update should have identity set");

        return updateGroup.execute(session, toUpdate.getName(), toUpdate.getId()) > 0;
    }

    @Override
    public boolean isUnique(JDBCSession session, String name) {
        return countEntityByName.executeSingle(session, name) == 0;
    }
}
