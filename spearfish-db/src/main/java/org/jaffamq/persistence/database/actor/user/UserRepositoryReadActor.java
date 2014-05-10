package org.jaffamq.persistence.database.actor.user;

import org.jaffamq.persistence.database.actor.CrudRepositoryReadActor;
import org.jaffamq.persistence.database.user.User;
import org.jaffamq.persistence.database.user.UserRepository;

import javax.sql.DataSource;

/**
 * Created by urwisy on 2014-05-04.
 */
public class UserRepositoryReadActor extends CrudRepositoryReadActor<User> {

    public UserRepositoryReadActor(UserRepository repository, DataSource dataSource) {
        super(repository, dataSource);
    }
}
