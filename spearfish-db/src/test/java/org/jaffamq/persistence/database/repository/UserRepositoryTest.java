package org.jaffamq.persistence.database.repository;

import org.hamcrest.Matchers;
import org.jaffamq.persistence.database.dto.Group;
import org.jaffamq.persistence.database.dto.User;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import static org.jaffamq.persistence.database.sql.DBConst.NO_LIMIT;
import static org.jaffamq.persistence.database.sql.DBConst.NO_OFFSET;
/**
 * Created by urwisy on 12.01.14.
 */
public class UserRepositoryTest extends RepositoryTest {

    private UserRepository repository;

    @Before
    public void init() {
        repository = new UserRepository();
    }

    @Test
    public void shouldReturnNonPagedListOfSuperadminAndTestUsers(){

        //  when
        List<User> users = repository.getUserList(getSession(),NO_LIMIT,NO_OFFSET);

        //  then
        assertThat(users.size(), is(equalTo(6)));
        assertThat(users.get(0).getId(), is(equalTo(1l)));
        assertThat(users.get(0).getLogin(), is(equalTo("admin")));
        assertThat(users.get(0).getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(users.get(0).getCreationTime(), is(equalTo(DateTimeZone.forOffsetMillis(1))));
        assertThat(users.get(0).getGroups().size(), is(equalTo(1)));
        assertThat(users.get(1).getId(), is(equalTo(1000l)));
        assertThat(users.get(1).getLogin(), is(equalTo("someuser1")));
        assertThat(users.get(1).getGroups().size(), is(equalTo(0)));
        assertThat(users.get(2).getId(), is(equalTo(1001l)));
        assertThat(users.get(2).getLogin(), is(equalTo("someuser2")));
        assertThat(users.get(2).getGroups().size(), is(equalTo(0)));
        assertThat(users.get(3).getId(), is(equalTo(1002l)));
        assertThat(users.get(3).getLogin(), is(equalTo("someuser3")));
        assertThat(users.get(3).getGroups().size(), is(equalTo(0)));
        assertThat(users.get(4).getId(), is(equalTo(1003l)));
        assertThat(users.get(4).getLogin(), is(equalTo("someuser4")));
        assertThat(users.get(4).getGroups().size(), is(equalTo(0)));
        assertThat(users.get(5).getId(), is(equalTo(1004l)));
        assertThat(users.get(5).getLogin(), is(equalTo("someuser5")));
        assertThat(users.get(5).getGroups().size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnNullForNotFoundUser() throws Exception{

        //  when
        User notFoundUser = repository.getUser(getSession(), "nonexisting", "notimportant");

        //  then
        assertThat(notFoundUser, is(nullValue()));
    }


    @Test
    public void shouldHaveAdminUserCreatedAfterStart() throws Exception {

        //  when
        User admin = repository.getUser(getSession(), UserDefaults.SUPERADMIN_LOGIN, UserDefaults.SUPERADMIN_PASSWORD_DEFAULT);

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId(), is(greaterThan(0l)));
        assertThat(admin.getLogin(), is(equalTo(UserDefaults.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(Matchers.notNullValue()));
        assertThat(admin.getGroups().size(), is(equalTo(1)));
        assertThat(admin.getGroups().get(0).getName(), is(equalTo(UserDefaults.ADMINS_GROUP)));
    }

    @Test
    public void shouldReturnNullForGetUserWithNonExistedUser(){

        //  when
        User admin = repository.getUser(getSession(), 0l);

        //  then
        assertThat(admin, is(nullValue()));

    }

    @Test
    public void shouldReturnUserById(){

        //  when
        User admin = repository.getUser(getSession(), UserDefaults.SUPERADMIN_ID);

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId().intValue(), is(equalTo(UserDefaults.SUPERADMIN_ID.intValue())));
        assertThat(admin.getLogin(), is(equalTo(UserDefaults.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(Matchers.notNullValue()));
        assertThat(admin.getGroups().size(), is(equalTo(1)));
        assertThat(admin.getGroups().get(0).getName(), is(equalTo(UserDefaults.ADMINS_GROUP)));
    }

    @Test
    public void shouldCreateUserWithoutGroup() throws Exception{

        //  when
        Long afterCreation = repository.createUser(getSession(), "somelogin", UserDefaults.SUPERADMIN_PASSWORD_DEFAULT, Collections.EMPTY_LIST);

        //  then
        assertThat(afterCreation, is(notNullValue()));

        User userRetrieved = repository.getUser(getSession(), afterCreation);

        assertThat(userRetrieved.getId(), is(greaterThan(0l)));
        assertThat(userRetrieved.getLogin(), is(equalTo("somelogin")));
        assertThat(userRetrieved.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
    }

    @Test
    public void shouldCreateUserWithGroups(){

        //  given
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(1001, "test1"));
        groups.add(new Group(1002, "test2"));

        //  when
        Long createdId = repository.createUser(getSession(), "a", UserDefaults.SUPERADMIN_PASSWORD_DEFAULT, groups);

        //  then
        assertThat(createdId, is(notNullValue()));
        User userRetrieved = repository.getUser(getSession(), createdId);
        assertThat(userRetrieved.getLogin(), is(equalTo("a")));
        assertThat(userRetrieved.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(userRetrieved.getGroups(), containsInAnyOrder(groups.toArray()));

    }

    @Ignore
    @Test
    public void shouldUpdateUserWithoutChangingGroups(){



    }

    @Ignore
    @Test
    public void shouldUpdateUserWithChangedGroups(){

    }

    @Test
    public void shouldShouldFoundUserByLogin(){

        //  when
        boolean found = repository.userWithLoginExists(getSession(), UserDefaults.SUPERADMIN_LOGIN);

        //  then
        assertThat(found, is(true));

    }

    @Test
    public void shouldNotFoundUserByLogin(){

        //  hen
        boolean found = repository.userWithLoginExists(getSession(), "unknown!@#");

        //  then
        assertThat(found, is(false));
    }

    @Test
    public void shouldDeleteUser(){

        //  when
        boolean deleted = repository.deleteUser(getSession(), 1004l);
        User userAfterUpdate = repository.getUser(getSession(), 1004l);

        //  then
        assertThat(deleted, is(true));
        assertThat(userAfterUpdate, is(Matchers.nullValue()));

    }





}
