package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.CalendarUtilsTest;
import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.repository.group.GroupRepository;
import org.jaffamq.persistence.database.repository.user.User;
import org.jaffamq.persistence.database.repository.user.UserDefaults;
import org.jaffamq.persistence.database.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import static org.jaffamq.persistence.database.sql.DBConst.NO_LIMIT;
import static org.jaffamq.persistence.database.sql.DBConst.NO_OFFSET;
/**
 * Created by urwisy on 12.01.14.
 */
public class UserRepositoryTest extends RepositoryTest {

    private UserRepository repository;
    private GroupRepository groupRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        repository = new UserRepository();
        groupRepository = new GroupRepository();
    }

    @Test
    public void shouldReturnPagedListOfUsers(){

        //  given
        List<User> expected = Arrays.asList(
                repository.get(getSession(), 1001l),
                repository.get(getSession(), 1002l),
                repository.get(getSession(), 1003l)
        );

        //  when
        List<User> users = repository.list(getSession(), 3, 2);

        //  then
        assertThat(users, is(equalTo(expected)));

    }


    @Test
    public void shouldReturnNonPagedListOfSuperadminAndTestUsers(){

        //  given
        List<User> expected = Arrays.asList(
                repository.get(getSession(), 1l),
                repository.get(getSession(), 1000l),
                repository.get(getSession(), 1001l),
                repository.get(getSession(), 1002l),
                repository.get(getSession(), 1003l),
                repository.get(getSession(), 1004l)
        );

        //  when
        List<User> users = repository.list(getSession(), NO_LIMIT, NO_OFFSET);

        //  then
        assertThat(users, is(equalTo(expected)));

    }

    @Test
    public void shouldThrowIAEOnInvalidUserToCreate(){

        //  given
        User user = new User(1l, "some", "someother", CalendarUtils.now());
        user.setPassword("x");

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("User to create should not contain identity");

        //  when
        repository.create(getSession(), user);

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
        Set<Group> expectedGroups = Sets.newHashSet(groupRepository.get(getSession(), UserDefaults.ADMINS_GROUP_ID));

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId(), is(greaterThan(0l)));
        assertThat(admin.getLogin(), is(equalTo(UserDefaults.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(equalTo(expectedGroups)));
    }

    @Test
    public void shouldReturnNullForGetUserWithNonExistedUser(){

        //  when
        User admin = repository.get(getSession(), 0l);

        //  then
        assertThat(admin, is(nullValue()));

    }

    @Test
    public void shouldReturnUserById(){

        //  when
        User admin = repository.get(getSession(), UserDefaults.SUPERADMIN_ID);
        Set<Group> expectedGroups = Sets.newHashSet(groupRepository.get(getSession(), UserDefaults.ADMINS_GROUP_ID));

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId().intValue(), is(equalTo(UserDefaults.SUPERADMIN_ID.intValue())));
        assertThat(admin.getLogin(), is(equalTo(UserDefaults.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(admin.getGroups(), is(equalTo(expectedGroups)));

    }

    @Test
    public void shouldThrowNPEforCreateUserWithNull() throws Exception{

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("User to create can not be null");

        //  when
        repository.create(getSession(), null);
    }

    @Test
    public void shouldThrowNPEForCreateUserWithNulledPassword() throws Exception{

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Password can not be left empty for new user");

        //  when
        repository.create(getSession(), new User("somex", null));

    }

    @Test
    public void shouldCreateUserWithoutGroup() throws Exception{

        //  when
        User user = new User("somelogin", UserDefaults.SUPERADMIN_PASSWORD_DEFAULT);
        Long createdId = repository.create(getSession(), user);

        //  then
        assertThat(createdId, is(greaterThan(0l)));

        User userRetrieved = repository.get(getSession(), createdId);

        assertThat(userRetrieved.getId(), is(greaterThan(0l)));
        assertThat(userRetrieved.getLogin(), is(equalTo("somelogin")));
        assertThat(userRetrieved.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(userRetrieved.getGroups(), is(empty()));
    }

    @Test
    public void shouldCreateUserWithGroups(){

        //  given
        Set<Group> groups = Sets.newHashSet(
            new Group(1001l, "test1"),
            new Group(1002l, "test2"));

        //  when
        User toCreate = new User("a", UserDefaults.SUPERADMIN_PASSWORD_DEFAULT);
        toCreate.setGroups(groups);
        Long createdId = repository.create(getSession(), toCreate);

        //  then
        assertThat(createdId, is(notNullValue()));
        User userRetrieved = repository.get(getSession(), createdId);
        assertThat(userRetrieved.getLogin(), is(equalTo("a")));
        assertThat(userRetrieved.getPasswordhash(), is(equalTo(UserDefaults.SUPERADMIN_PASSWORD_HASH)));
        assertThat(userRetrieved.getGroups(), containsInAnyOrder(groups.toArray()));

    }

    @Test
    public void shouldThrowNPEOnNullUserToUpdate(){

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("User to update can not be null");

        //  when
        repository.update(getSession(), null);
    }

    @Test
    public void shouldThrowNPEOnLackOfIdentityOfUserToUpdate(){

        //  given
        User user = new User(null, "testX", "some", CalendarUtils.now());

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to update should have identity set");

        //  when
        repository.update(getSession(), user);

    }

    @Test
    public void shouldReturnFalseForUpdatingNotRecognizedUser(){

        //  given
        User beforeUpdate = repository.get(getSession(), 1004l);
        User toUpdate = new User(99999l, "newlogin", beforeUpdate.getPasswordhash(), CalendarUtils.now());

        //  when
        boolean updated = repository.update(getSession(), toUpdate);

        //  then
        assertThat(updated, is(false));
    }

    @Test
    public void shouldUpdateUserWithoutGroupsAssignedWithPasswordChange(){

        //  given
        User beforeUpdate = repository.get(getSession(), 1004l);
        User toUpdate = new User(beforeUpdate.getId(), "newlogin", beforeUpdate.getPasswordhash(), CalendarUtils.now());
        toUpdate.setPassword("newpassword");

        //  when
        boolean updated = repository.update(getSession(), toUpdate);

        //  then
        User afterUpdate = repository.get(getSession(), 1004l);

        assertThat(updated, is(true));
        assertThat(afterUpdate, is(not(equalTo(beforeUpdate))));
        assertThat(afterUpdate.getPasswordhash(), is(equalTo("55464335b2bcd6862428847e720152892edf299a2796dcd2657d9b1341e1ad65")));
        assertThat("update should not affect creationtime", afterUpdate.getCreationTime(), is(equalTo(beforeUpdate.getCreationTime())));
        assertThat(afterUpdate.getLogin(), is(equalTo(toUpdate.getLogin())));

    }


    @Test
    public void shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange(){

        //  given
        User beforeChange = repository.get(getSession(), 1002l);
        User toChange = new User(1002l, "shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange", "thisshouldnotbesaved", CalendarUtils.now());

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1002l);

        //  then
        assertThat(updated, is(true));
        assertThat(afterUpdate.getLogin(), is(equalTo("shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange")));
        assertThat(afterUpdate.getCreationTime(), is(equalTo(beforeChange.getCreationTime())));
        assertThat(afterUpdate.getPasswordhash(), is(equalTo(beforeChange.getPasswordhash())));

    }

    @Test
    public void shouldUpdateUserWithoutChangingGroups(){

        //  given
        User beforeChange = repository.get(getSession(), 1003l);
        User toChange = new User(1003l, "shouldUpdateUserWithoutChangingGroups", "thisshouldnotbesaved", CalendarUtils.now());
        toChange.setGroups(beforeChange.getGroups());

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1003l);

        //  then
        assertThat(updated, is(true));
        assertThat(afterUpdate.getLogin(), is(equalTo("shouldUpdateUserWithoutChangingGroups")));
        assertThat(afterUpdate.getCreationTime(), is(equalTo(beforeChange.getCreationTime())));
        assertThat(afterUpdate.getPasswordhash(), is(equalTo(beforeChange.getPasswordhash())));
        assertThat(afterUpdate.getGroups(), is(equalTo(beforeChange.getGroups())));
    }


    @Test
    public void shouldUpdateUserWithChangedGroups(){

        //  given
        User beforeChange = repository.get(getSession(), 1003l);
        User toChange = new User(1003l, "shouldUpdateUserWithoutChangingGroups", "thisshouldnotbesaved", CalendarUtils.now());
        Set<Group> expectedGroups = Sets.newHashSet(
                groupRepository.get(getSession(), 1002l),
                groupRepository.get(getSession(), 1003l),
                groupRepository.get(getSession(), 1004l));
        toChange.setGroups(expectedGroups);

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1003l);

        //  then
        assertThat(updated, is(true));
        assertThat(afterUpdate.getLogin(), is(equalTo("shouldUpdateUserWithoutChangingGroups")));
        assertThat(afterUpdate.getCreationTime(), is(equalTo(beforeChange.getCreationTime())));
        assertThat(afterUpdate.getPasswordhash(), is(equalTo(beforeChange.getPasswordhash())));
        assertThat(afterUpdate.getGroups(), is(equalTo(expectedGroups)));
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
        boolean deleted = repository.delete(getSession(), 1004l);
        User userAfterUpdate = repository.get(getSession(), 1004l);

        //  then
        assertThat(deleted, is(true));
        assertThat(userAfterUpdate, is(Matchers.nullValue()));

    }

    @Test
    public void shouldReturnUniqueForNonExistingName(){

        //  when
        boolean isUnique = repository.isUnique(getSession(), "shouldReturnUniqueForNonExistingName");

        //  then
        assertThat(isUnique, is(true));
    }


    @Test
    public void shouldReturnNonUniqueForExistingName(){

        //  when
        boolean isUnique = repository.isUnique(getSession(), UserDefaults.SUPERADMIN_LOGIN);

        //  then
        assertThat(isUnique, is(false));

    }




}
