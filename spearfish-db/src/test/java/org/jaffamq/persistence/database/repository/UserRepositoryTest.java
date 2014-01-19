package org.jaffamq.persistence.database.repository;

import org.hamcrest.Matchers;
import org.jaffamq.persistence.database.dto.User;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
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
        assertThat(users.get(0).getPasswordhash(), is(equalTo(UserRepository.SUPERADMIN_PASSWORD_HASH)));
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
        User admin = repository.getUser(getSession(), UserRepository.SUPERADMIN_LOGIN, UserRepository.SUPERADMIN_PASSWORD_DEFAULT);

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId(), is(greaterThan(0l)));
        assertThat(admin.getLogin(), is(equalTo(UserRepository.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserRepository.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(Matchers.notNullValue()));
        assertThat(admin.getGroups().size(), is(equalTo(1)));
        assertThat(admin.getGroups().get(0).getName(), is(equalTo(UserRepository.ADMINS_GROUP)));
    }

    @Test
    public void shouldReturnNullForGetUserWithNonExistedUser(){

    }

    @Test
    public void shouldReturnUserById(){

        //  when
        User admin = repository.getUser(getSession(), UserRepository.SUPERADMIN_ID);

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId().intValue(), is(equalTo(UserRepository.SUPERADMIN_ID.intValue())));
        assertThat(admin.getLogin(), is(equalTo(UserRepository.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserRepository.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(Matchers.notNullValue()));
        assertThat(admin.getGroups().size(), is(equalTo(1)));
        assertThat(admin.getGroups().get(0).getName(), is(equalTo(UserRepository.ADMINS_GROUP)));
    }

    @Test
    public void shouldCreateUserWithoutGroup() throws Exception{

        //  when
        Long afterCreation = repository.createUser(getSession(), "somelogin", UserRepository.SUPERADMIN_PASSWORD_DEFAULT, Collections.EMPTY_LIST);

        //  then
        assertThat(afterCreation, is(notNullValue()));

        User userRetrieved = repository.getUser(getSession(), afterCreation);

        assertThat(userRetrieved.getId(), is(greaterThan(0l)));
        assertThat(userRetrieved.getLogin(), is(equalTo("somelogin")));
        assertThat(userRetrieved.getPasswordhash(), is(equalTo(UserRepository.SUPERADMIN_PASSWORD_HASH)));


    }

    @Ignore
    @Test
    public void shouldCreateUserWithGroups(){

    }

    @Ignore
    @Test
    public void shouldUpdateUserWithoutChangingGroups(){

    }

    @Ignore
    @Test
    public void shouldUpdateUserWithChangedGroups(){

    }

    @Ignore
    @Test
    public void shouldDeleteUser(){

    }





}
