package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.PasswordHash;
import org.jaffamq.persistence.database.dto.Group;
import org.jaffamq.persistence.database.dto.User;
import org.jaffamq.persistence.database.repository.mappings.GroupsByUser;
import org.jaffamq.persistence.database.repository.mappings.user.InsertGroupAndUser;
import org.jaffamq.persistence.database.repository.mappings.user.InsertUser;
import org.jaffamq.persistence.database.repository.mappings.UserByLoginAndPassword;
import org.jaffamq.persistence.database.repository.mappings.UserList;
import org.jaffamq.persistence.database.repository.mappings.user.SelectUserById;
import org.jaffamq.persistence.database.sql.JDBCSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Repository for User table interaction.
 */
public class UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    public static String SUPERADMIN_LOGIN = "admin";
    public static Long SUPERADMIN_ID = 1l;
    public static String SUPERADMIN_PASSWORD_DEFAULT = "xyz321";
    public static String SUPERADMIN_PASSWORD_HASH = "8be94e85158147d335c31e7401565942785e79d7d446cc41f6427422d6755371";
    public static String ADMINS_GROUP = "admins";

    private UserByLoginAndPassword userByLoginAndPassword = new UserByLoginAndPassword();
    private SelectUserById selectUserById = new SelectUserById();
    private GroupsByUser groupsByUser = new GroupsByUser();
    private UserList userList = new UserList();
    private InsertUser insertUser = new InsertUser();
    private InsertGroupAndUser insertGroupAndUser = new InsertGroupAndUser();

    /**
     *
     * @param session current session
     * @param login loginname
     * @param password plain password
     * @return
     */
    public User getUser(JDBCSession session, String login, String password) {

        User user = userByLoginAndPassword.executeEntity(session, login, PasswordHash.hash(password));

        if(user == null){
            return null;
        }

        user = decorateUserWithGroups(session, user);
        return user;
    }

    /**
     * Get user by PK
     * @param session
     * @param id
     * @return
     */
    public User getUser(JDBCSession session, Long id){

        List<User> users = selectUserById.execute(session, id);

        if(users.size() == 0){
            return null;
        }

        if(users.size() > 1){
            throw new IllegalStateException("Expected 0 or 1 results but found more than that");
        }

        return decorateUserWithGroups(session, users.get(0));
    }

    private User decorateUserWithGroups(JDBCSession session, User user){
        user.setGroups(groupsByUser.execute(session, user.getId()));
        return user;
    }

    /**
     * Retrieve list of users (sorted by id DESC).
     * @param limit starts with minus x (<0 means no limit)
     * @param offset start with 0
     * @return
     */
    public List<User> getUserList(JDBCSession session, int limit, int offset){

        List<User> users = userList.execute(session, limit, offset);

        for(User user:users){
            decorateUserWithGroups(session, user);
        }

        return users;
    }

    /**
     * Create user with given characteristics
     * @param session
     *
     * @param passwordToUse
     * @return ID of the newly created user
     */
    public Long createUser(JDBCSession session, String login, String passwordToUse, List<Group> groups){

        LOG.debug("Creating user with login: {}", login);
        Long id = IdentityProvider.getNextIdFor(session, User.class);
        insertUser.insert(session,
                id,
                login,
                PasswordHash.hash(passwordToUse),
                100);

        if(groups.size()>0){

            //  do something
            for(Group group:groups){
                insertGroupAndUser.insert(session,id, group.getId());
            }
        }

        LOG.debug("Created user with id: {}", id);
        return id;
    }

    /**
     *
     * @param session
     * @param updatedUser ignored fields are: id, passwordhash, creationstamp
     * @param newPasswordToSet null indicates that no change to password is required, if set it will update the password
     *                         and consequently the passwordhash
     * @return true if tuple for update found in db, false otherwise
     */
    public boolean updateUser(JDBCSession session, User updatedUser, String newPasswordToSet){
        return false;
    }
}
