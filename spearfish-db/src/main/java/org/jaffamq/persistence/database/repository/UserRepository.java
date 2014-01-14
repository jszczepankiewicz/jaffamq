package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.dto.Group;
import org.jaffamq.persistence.database.dto.User;
import org.jaffamq.persistence.database.repository.mappings.GroupsByUser;
import org.jaffamq.persistence.database.repository.mappings.UserByLoginAndPassword;
import org.jaffamq.persistence.database.sql.JDBCSession;

import java.util.List;

/**
 * Created by urwisy on 07.01.14.
 */
public class UserRepository {

    public static String SUPERADMIN_LOGIN = "admin";
    public static String SUPERADMIN_PASSWORD_DEFAULT = "xyz321";
    public static String ADMINS_GROUP = "admins";

    private UserByLoginAndPassword userByLoginAndPassword = new UserByLoginAndPassword();
    private GroupsByUser groupsByUser = new GroupsByUser();

    public User getUser(JDBCSession session, String login, String password) {

        User user = userByLoginAndPassword.executeEntity(session, login, password);

        if(user == null){
            return null;
        }

        user = decorateUserWithGroups(session, user);
        return user;
    }

    private User decorateUserWithGroups(JDBCSession session, User user){
        user.setGroups(groupsByUser.execute(session, user.getId()));
        return user;
    }

    /**
     * Retrieve list of users (sorted by id DESC).
     * @param limit starts with 0
     * @param offset start with 0
     * @return
     */
    public List<User> getUserList(JDBCSession session, int limit, int offset){
        return null;
    }

    public User createUser(JDBCSession session, User newUser, String passwordToUse){
        return null;
    }
}
