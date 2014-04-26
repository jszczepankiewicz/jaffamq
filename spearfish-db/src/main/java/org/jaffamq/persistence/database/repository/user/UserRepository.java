package org.jaffamq.persistence.database.repository.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.PasswordHash;
import org.jaffamq.persistence.database.Table;
import org.jaffamq.persistence.database.repository.CheckEntityUnique;
import org.jaffamq.persistence.database.repository.CrudRepository;
import org.jaffamq.persistence.database.repository.IdentityProvider;
import org.jaffamq.persistence.database.repository.NotFoundInSetPredicate;
import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.repository.group.GroupsByUser;
import org.jaffamq.persistence.database.sql.CountEntityByName;
import org.jaffamq.persistence.database.sql.DeleteByIdOperation;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.jaffamq.persistence.database.sql.ListAllOperation;
import org.jaffamq.persistence.database.sql.MNRelation;
import org.jaffamq.persistence.database.sql.SelectByIdOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repository for User table interaction.
 */
public class UserRepository implements CrudRepository<User>, CheckEntityUnique {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private UserByLoginAndPassword userByLoginAndPassword = new UserByLoginAndPassword();
    private SelectByIdOperation<User> selectById = new SelectByIdOperation<>(DBConst.USER_MAPPER);
    private GroupsByUser groupsByUser = new GroupsByUser();
    private ListAllOperation listAll = new ListAllOperation(DBConst.USER_MAPPER, "login");
    private InsertUser insertUser = new InsertUser();
    private DeleteByIdOperation deleteUser = new DeleteByIdOperation(Table.USER);
    private CountEntityByName countUserByLogin = new CountEntityByName(Table.USER, "login");
    private UpdateUserWithoutPassword updateUserWithoutPassword = new UpdateUserWithoutPassword();
    private UpdateUserWithPassword updateUserWithPassword = new UpdateUserWithPassword();
    private MNRelation userAndGroupRelations = new MNRelation(Table.USER_AND_GROUP, "id_user", "id_group");


    /**
     * @param session  current session
     * @param login    loginname
     * @param password plain password
     * @return
     */
    public User getUser(JDBCSession session, String login, String password) {

        User user = userByLoginAndPassword.executeEntity(session, login, PasswordHash.hash(password));

        if (user == null) {
            return null;
        }

        user = decorateUserWithGroups(session, user);
        return user;
    }

    /**
     * Get user by PK
     *
     * @param session
     * @param id
     * @return
     */
    public User get(JDBCSession session, Long id) {

        List<User> users = selectById.execute(session, id);

        if (users.size() == 0) {
            return null;
        }

        if (users.size() > 1) {
            throw new IllegalStateException("Expected 0 or 1 results but found more than that");
        }

        return decorateUserWithGroups(session, users.get(0));
    }

    private User decorateUserWithGroups(JDBCSession session, User user) {
        user.setGroups(new HashSet<>(groupsByUser.execute(session, user.getId())));
        return user;
    }

    /**
     * Retrieve list of users (sorted by id DESC).
     *
     * @param limit  starts with minus x (<0 means no limit)
     * @param offset start with 0
     * @return
     */
    @Override
    public List<User> list(JDBCSession session, int limit, int offset) {

        List<User> users = listAll.execute(session, limit, offset);

        for (User user : users) {
            decorateUserWithGroups(session, user);
        }

        return users;
    }

    @Override
    public Long create(JDBCSession session, User toCreate) {

        Preconditions.checkNotNull(toCreate, "User to create can not be null");
        Preconditions.checkNotNull(toCreate.getPassword(), "Password can not be left empty for new user");
        Preconditions.checkArgument(toCreate.getId() == null, "User to create should not contain identity");

        Long id = IdentityProvider.getNextIdFor(session, User.class);
        insertUser.execute(session,
                id,
                toCreate.getLogin(),
                PasswordHash.hash(toCreate.getPassword()),
                CalendarUtils.asLong(toCreate.getCreationTime()));

        if (toCreate.getGroups().size() > 0) {

            //  do something
            for (Group group : toCreate.getGroups()) {
                userAndGroupRelations.add(session, id, group.getId());
            }
        }

        LOG.debug("Created user with id: {}", id);
        return id;
    }

    private void synchronizeRelationsToGroups(JDBCSession session, Set<Group> persisted, Set<Group> toPersist, Long userId) {

        Set<Group> diffToPersist = Sets.filter(toPersist, new NotFoundInSetPredicate(persisted));
        Set<Group> diffToRemove = Sets.filter(persisted, new NotFoundInSetPredicate(toPersist));

        for (Group group : diffToPersist) {
            userAndGroupRelations.add(session, userId, group.getId());
        }

        for (Group group : diffToRemove) {
            userAndGroupRelations.remove(session, userId, group.getId());
        }
    }

    @Override
    public boolean update(JDBCSession session, User toUpdate) {

        Preconditions.checkNotNull(toUpdate, "User to update can not be null");
        Preconditions.checkArgument(toUpdate.getId() != null, "Object to update should have identity set");

        int affected;
        if (toUpdate.getPassword() == null) {
            affected = updateUserWithoutPassword.execute(session, toUpdate.getLogin(), toUpdate.getId());
        } else {
            affected = updateUserWithPassword.execute(session, toUpdate.getLogin(), PasswordHash.hash(toUpdate.getPassword()), toUpdate.getId());
        }
        if (affected == 1) {
            User beforeChange = get(session, toUpdate.getId());
            synchronizeRelationsToGroups(session, beforeChange.getGroups(), toUpdate.getGroups(), toUpdate.getId());
        }

        return affected == 1;
    }

    /**
     * Delete user by id
     *
     * @param session
     * @param id
     * @return true if user found and removed
     */
    public boolean delete(JDBCSession session, Long id) {
        return deleteUser.execute(session, id) > 0;
    }

    @Override
    public boolean isUnique(JDBCSession session, String name) {
        return countUserByLogin.executeSingle(session, name) == 0;
    }
}
