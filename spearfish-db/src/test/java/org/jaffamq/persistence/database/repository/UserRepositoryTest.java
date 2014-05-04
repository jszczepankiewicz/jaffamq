package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.jaffamq.persistence.database.CalendarUtilsTest;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.group.GroupRepository;
import org.jaffamq.persistence.database.user.User;
import org.jaffamq.persistence.database.user.UserDefaults;
import org.jaffamq.persistence.database.user.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jaffamq.persistence.database.DBConst.NO_LIMIT;
import static org.jaffamq.persistence.database.DBConst.NO_OFFSET;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasId;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasIdSet;
import static org.jaffamq.persistence.database.repository.UserMatchers.hasLogin;
import static org.jaffamq.persistence.database.repository.UserMatchers.hasPassword;
import static org.jaffamq.persistence.database.repository.UserMatchers.hasPasswordhash;
import static org.jaffamq.persistence.database.repository.UserMatchers.isAssignedTo;
import static org.jaffamq.persistence.database.repository.UserMatchers.noGroups;
import static org.jaffamq.persistence.database.repository.UserMatchers.wasCreatedAt;
import static org.jaffamq.persistence.database.repository.UserMatchers.wasJustCreated;


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
    public void shouldReturnPagedListOfUsers() {

        //  given
        List<User> expected = Arrays.asList(
                repository.get(getSession(), 1001L),
                repository.get(getSession(), 1002L),
                repository.get(getSession(), 1003L)
        );

        //  when
        List<User> users = repository.list(getSession(), 3, 2);

        //  then
        assertThat(users, is(equalTo(expected)));

    }


    @Test
    public void shouldReturnNonPagedListOfSuperadminAndTestUsers() {

        //  given
        List<User> expected = Arrays.asList(
                repository.get(getSession(), 1L),
                repository.get(getSession(), 1000L),
                repository.get(getSession(), 1001L),
                repository.get(getSession(), 1002L),
                repository.get(getSession(), 1003L),
                repository.get(getSession(), 1004L)
        );

        //  when
        List<User> users = repository.list(getSession(), NO_LIMIT, NO_OFFSET);

        //  then
        assertThat(users, is(equalTo(expected)));

    }

    @Test
    public void shouldThrowIAEOnInvalidUserToCreate() {

        //  given
        User user = new User.Builder("some").id(9999L).password("somex").build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("User to create should not contain identity");

        //  when
        repository.create(getSession(), user);

    }

    @Test
    public void shouldReturnNullForNotFoundUser() {

        //  when
        User notFoundUser = repository.getUser(getSession(), "nonexisting", "notimportant");

        //  then
        assertThat(notFoundUser, is(nullValue()));
    }


    @Test
    public void shouldHaveAdminUserCreatedAfterStart() {

        //  given
        Set<Group> expectedGroups = Sets.newHashSet(groupRepository.get(getSession(), UserDefaults.ADMINS_GROUP_ID));

        //  when
        User admin = repository.getUser(getSession(), UserDefaults.SUPERADMIN_LOGIN, UserDefaults.SUPERADMIN_PASSWORD_DEFAULT);

        //  then
        assertThat(admin.getId(), is(greaterThan(0L)));
        assertThat(admin, allOf(
                wasCreatedAt(CalendarUtilsTest.TARGET_DATE),
                hasLogin(UserDefaults.SUPERADMIN_LOGIN),
                hasPasswordhash(UserDefaults.SUPERADMIN_PASSWORD_HASH),
                hasPassword(null),
                isAssignedTo(expectedGroups)
        ));
    }

    @Test
    public void shouldReturnNullForGetUserWithNonExistedUser() {

        //  when
        User admin = repository.get(getSession(), 0L);

        //  then
        assertThat(admin, is(nullValue()));

    }

    @Test
    public void shouldReturnUserById() {

        //  when
        User admin = repository.get(getSession(), UserDefaults.SUPERADMIN_ID);
        Set<Group> expectedGroups = Sets.newHashSet(groupRepository.get(getSession(), UserDefaults.ADMINS_GROUP_ID));

        //  then
        assertThat(admin, allOf(
                hasId(UserDefaults.SUPERADMIN_ID),
                hasLogin(UserDefaults.SUPERADMIN_LOGIN),
                hasPasswordhash(UserDefaults.SUPERADMIN_PASSWORD_HASH),
                hasPassword(null),
                wasCreatedAt(CalendarUtilsTest.TARGET_DATE),
                isAssignedTo(expectedGroups)
        ));

    }

    @Test
    public void shouldThrowNPEforCreateUserWithNull() {

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("User to create can not be null");

        //  when
        repository.create(getSession(), null);
    }

    @Test
    public void shouldThrowNPEForCreateUserWithNulledPassword() {

        //  given
        User user = new User.Builder("somex").build();

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Password can not be left empty for new user");

        //  when
        repository.create(getSession(), user);

    }

    @Test
    public void shouldCreateUserWithoutGroup() {

        //  when
        User user = new User.Builder("somelogin").password(UserDefaults.SUPERADMIN_PASSWORD_DEFAULT).build();

        Long createdId = repository.create(getSession(), user);

        //  then
        assertThat(createdId, is(greaterThan(0L)));

        User userRetrieved = repository.get(getSession(), createdId);

        assertThat(userRetrieved, allOf(
                wasJustCreated(),
                hasIdSet(),
                hasLogin("somelogin"),
                hasPasswordhash(UserDefaults.SUPERADMIN_PASSWORD_HASH),
                isAssignedTo(noGroups())
        ));

    }

    @Test
    public void shouldCreateUserWithGroups() {

        //  given
        Set<Group> groups = Sets.newHashSet(
                new Group.Builder("test1").id(1001L).build(),
                new Group.Builder("test2").id(1002L).build()
        );

        //  when
        User toCreate = new User.Builder("a").password(UserDefaults.SUPERADMIN_PASSWORD_DEFAULT).groups(groups).build();
        Long createdId = repository.create(getSession(), toCreate);

        //  then
        User userRetrieved = repository.get(getSession(), createdId);

        assertThat(createdId, is(greaterThan(0L)));
        assertThat(userRetrieved, allOf(
                hasLogin("a"),
                wasJustCreated(),
                hasIdSet(),
                hasPasswordhash(UserDefaults.SUPERADMIN_PASSWORD_HASH),
                isAssignedTo(groups)
        ));
    }

    @Test
    public void shouldThrowNPEOnNullUserToUpdate() {

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("User to update can not be null");

        //  when
        repository.update(getSession(), null);
    }

    @Test
    public void shouldThrowNPEOnLackOfIdentityOfUserToUpdate() {

        //  given
        User user = new User.Builder("testX").password("some").build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to update should have identity set");

        //  when
        repository.update(getSession(), user);

    }

    @Test
    public void shouldReturnFalseForUpdatingNotRecognizedUser() {

        //  given
        User beforeUpdate = repository.get(getSession(), 1004L);
        User toUpdate = new User.Builder("newlogin").id(99999L).passwordhash(beforeUpdate.getPasswordhash()).build();

        //  when
        boolean updated = repository.update(getSession(), toUpdate);

        //  then
        assertThat(updated, is(false));
    }

    @Test
    public void shouldUpdateUserWithoutGroupsAssignedWithPasswordChange() {

        //  given
        User beforeUpdate = repository.get(getSession(), 1004L);
        User toUpdate = new User.Builder("newlogin").id(beforeUpdate.getId())
                .passwordhash(beforeUpdate.getPasswordhash()).password("newpassword").build();

        //  when
        boolean updated = repository.update(getSession(), toUpdate);

        //  then
        User afterUpdate = repository.get(getSession(), 1004L);

        assertThat(updated, is(true));

        assertThat(afterUpdate, allOf(
                hasPasswordhash("55464335b2bcd6862428847e720152892edf299a2796dcd2657d9b1341e1ad65"),
                hasLogin(toUpdate.getLogin()),
                wasCreatedAt(beforeUpdate.getCreationTime())

        ));

    }


    @Test
    public void shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange() {

        //  given
        User beforeChange = repository.get(getSession(), 1002L);
        User toChange = new User.Builder("shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange")
                .id(1002L)
                .passwordhash("thisshouldnotbesaved").build();

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1002L);

        //  then
        assertThat(updated, is(true));

        assertThat(afterUpdate, allOf(
                hasLogin("shouldUpdateUserWithoutGroupsAssignedWithoutPasswordChange"),
                wasCreatedAt(beforeChange.getCreationTime()),
                hasPasswordhash(beforeChange.getPasswordhash()),
                isAssignedTo(noGroups())

        ));

    }

    @Test
    public void shouldUpdateUserWithoutChangingGroups() {

        //  given
        User beforeChange = repository.get(getSession(), 1003L);
        User toChange = new User.Builder("shouldUpdateUserWithoutChangingGroups")
                .id(1003L)
                .passwordhash("thisshouldnotbesaved")
                .groups(beforeChange.getGroups()).build();

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1003L);

        //  then
        assertThat(updated, is(true));

        assertThat(afterUpdate, allOf(
                hasLogin("shouldUpdateUserWithoutChangingGroups"),
                wasCreatedAt(beforeChange.getCreationTime()),
                hasPasswordhash(beforeChange.getPasswordhash()),
                isAssignedTo(beforeChange.getGroups())
        ));
    }


    @Test
    public void shouldUpdateUserWithChangedGroups() {

        //  given
        User beforeChange = repository.get(getSession(), 1003L);
        Set<Group> expectedGroups = Sets.newHashSet(
                groupRepository.get(getSession(), 1002L),
                groupRepository.get(getSession(), 1003L),
                groupRepository.get(getSession(), 1004L));
        User toChange = new User.Builder("shouldUpdateUserWithoutChangingGroups")
                .id(1003L)
                .passwordhash("thisshouldnotbesaved")
                .groups(expectedGroups).build();

        //  when
        boolean updated = repository.update(getSession(), toChange);
        User afterUpdate = repository.get(getSession(), 1003L);

        //  then
        assertThat(updated, is(true));

        assertThat(afterUpdate, allOf(
                hasLogin("shouldUpdateUserWithoutChangingGroups"),
                wasCreatedAt(beforeChange.getCreationTime()),
                hasPasswordhash(beforeChange.getPasswordhash()),
                isAssignedTo(expectedGroups)

        ));
    }

    @Test
    public void shouldDeleteUser() {

        //  when
        boolean deleted = repository.delete(getSession(), 1004L);
        User userAfterUpdate = repository.get(getSession(), 1004L);

        //  then
        assertThat(deleted, is(true));
        assertThat(userAfterUpdate, is(Matchers.nullValue()));

    }

    @Test
    public void shouldReturnUniqueForNonExistingName() {

        //  when
        boolean isUnique = repository.isUnique(getSession(), "shouldReturnUniqueForNonExistingName");

        //  then
        assertThat(isUnique, is(true));
    }


    @Test
    public void shouldReturnNonUniqueForExistingName() {

        //  when
        boolean isUnique = repository.isUnique(getSession(), UserDefaults.SUPERADMIN_LOGIN);

        //  then
        assertThat(isUnique, is(false));

    }

    @Test
    public void shouldReturnNonUniqueForExistingNameDifferentCase() {

        //  when
        boolean isUnique = repository.isUnique(getSession(), UserDefaults.SUPERADMIN_LOGIN.toUpperCase());

        //  then
        assertThat(isUnique, is(false));

    }


}
