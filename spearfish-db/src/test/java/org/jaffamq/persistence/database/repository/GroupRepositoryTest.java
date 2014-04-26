package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.CalendarUtilsTest;
import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.repository.group.Group;
import org.jaffamq.persistence.database.repository.group.GroupRepository;
import org.jaffamq.persistence.database.repository.user.User;
import org.jaffamq.persistence.database.repository.user.UserDefaults;
import org.jaffamq.persistence.database.repository.user.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Created by urwisy on 19.01.14.
 */
public class GroupRepositoryTest extends RepositoryTest {

    private GroupRepository repository;
    private UserRepository userRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        repository = new GroupRepository();
        userRepository = new UserRepository();
    }

    @Test
    public void shouldHaveAdminsGroupAfterStartup() {

        //  when
        Group group = repository.get(getSession(), UserDefaults.ADMINS_GROUP_ID);

        //  then
        assertThat(group.getId(), is(equalTo(UserDefaults.ADMINS_GROUP_ID)));
        assertThat(group.getName(), is(equalTo(UserDefaults.ADMINS_GROUP)));
    }

    @Test
    public void shouldSelectGroupById() {

        //  when
        Group group = repository.get(getSession(), 1000l);

        //  then
        assertThat(group.getId(), is(equalTo(1000l)));
        assertThat(group.getName(), is(equalTo("test0")));
        assertThat(group.getCreationtime(), is(equalTo(CalendarUtilsTest.TARGET_DATE)));

    }

    @Test
    public void shouldDeleteGroupWithoutRelations() {

        //  when
        boolean deleted = repository.delete(getSession(), 1002l);
        User user1 = userRepository.get(getSession(), 1003l);
        User user2 = userRepository.get(getSession(), 1004l);

        //  then
        assertThat(deleted, is(true));
        Group deletedGroup = repository.get(getSession(), 1002l);
        assertThat(deletedGroup, is(nullValue()));
        assertThat(user1, is(notNullValue()));
        assertThat(user2, is(notNullValue()));

    }


    @Test
    public void shouldDeleteGroupWithRelations() {

        //  when
        boolean deleted = repository.delete(getSession(), 1003l);

        //  when
        assertThat(deleted, is(true));
        Group deletedGroup = repository.get(getSession(), 1003l);
        assertThat(deletedGroup, is(nullValue()));

    }

    @Test
    public void shouldUpdateGroup() {

        //  given
        Group groupToUpdate = new Group.Builder("shouldUpdateGroup").id(1l).build();

        //  when
        boolean isUpdated = repository.update(getSession(), groupToUpdate);

        //  then
        Group updated = repository.get(getSession(), 1l);
        assertThat(isUpdated, is(true));
        assertThat(updated.getName(), is(equalTo(groupToUpdate.getName())));

    }

    @Test
    public void shouldCreateGroup() {

        //  given
        final String EXPECTED_NAME = "shouldCreateGroupWithoutRelations";
        Group toCreate = new Group.Builder(EXPECTED_NAME).build();

        //  when
        Long id = repository.create(getSession(), toCreate);
        Group created = repository.get(getSession(), id);

        //  then
        assertThat(id, is(greaterThan(0l)));
        assertThat(created.getName(), is(equalTo(EXPECTED_NAME)));
        assertThat(created.getCreationtime(), is(equalTo(toCreate.getCreationtime())));

    }

    @Test
    public void shouldReturnFalseForUpdatingNotRecognizedGroup() {

        //  given
        Group group = new Group.Builder("999name").id(9999l).build();

        //  when
        boolean updated = repository.update(getSession(), group);

        //  then
        assertThat(updated, is(false));
    }

    @Test
    public void shouldThrowNPEOnNullDestinationToUpdate() {

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Object to update should not be null");

        //  when
        repository.update(getSession(), null);
    }

    @Test
    public void shouldThrowIAEOnLackOfIdentityInGroupToUpdate() {

        //  given
        Group g = new Group.Builder("testX").build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to update should have identity set");

        //  when
        repository.update(getSession(), g);

    }

    @Test
    public void shouldThrowNPEOnNullGroupToCreate() {

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Object to persist should not be null");

        //  when
        repository.create(getSession(), null);
    }

    @Test
    public void shouldThrowIAEOnInvalidCreateDestination() {

        //  given
        Group g = new Group.Builder("testX").id(1l).build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to create should not have identity set");

        //  when
        repository.create(getSession(), g);

    }

    @Test
    public void shouldListNonPagedGroups() {

        //  given
        List<Group> expected = Arrays.asList(
                new Group.Builder("admins").id(1l).build(),
                new Group.Builder("test0").id(1000l).build(),
                new Group.Builder("test1").id(1001l).build(),
                new Group.Builder("test2").id(1002l).build(),
                new Group.Builder("test3").id(1003l).build(),
                new Group.Builder("test4").id(1004l).build(),
                new Group.Builder("test5").id(1005l).build());

        //  when
        List<Group> found = repository.list(getSession(), DBConst.NO_LIMIT, DBConst.NO_OFFSET);

        //  then
        assertThat(found, is(equalTo(expected)));

    }

    @Test
    public void shouldListPagedGroups() {

        //  given
        List<Group> expected = Arrays.asList(
                new Group.Builder("test0").id(1000l).build(),
                new Group.Builder("test1").id(1001l).build(),
                new Group.Builder("test2").id(1002l).build());

        //  when
        List<Group> found = repository.list(getSession(), 3, 1);

        //  then
        assertThat(found, is(equalTo(expected)));

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
        boolean isUnique = repository.isUnique(getSession(), UserDefaults.ADMINS_GROUP);

        //  then
        assertThat(isUnique, is(false));
    }


    @Test
    public void shouldReturnNonUniqueForExistingNameDifferentCase() {

        //  when
        boolean isUnique = repository.isUnique(getSession(), UserDefaults.ADMINS_GROUP.toUpperCase());

        //  then
        assertThat(isUnique, is(false));
    }


}
