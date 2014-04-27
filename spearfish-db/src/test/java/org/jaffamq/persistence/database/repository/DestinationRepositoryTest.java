package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.CalendarUtilsTest;
import org.jaffamq.persistence.database.DBConst;
import org.jaffamq.persistence.database.destination.Destination;
import org.jaffamq.persistence.database.destination.DestinationRepository;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.group.GroupRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.jaffamq.persistence.database.destination.Destination.Type.QUEUE;
import static org.jaffamq.persistence.database.destination.Destination.Type.TOPIC;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.canBeAdminBy;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.canBeReadBy;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.canBeWriteBy;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.everybody;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.hasName;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.isOfType;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.wasCreatedAt;
import static org.jaffamq.persistence.database.repository.DestinationMatchers.wasJustCreated;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasId;
import static org.jaffamq.persistence.database.repository.IdentifiableMatchers.hasIdSet;


/**
 * Created by urwisy on 15.04.14.
 */
public class DestinationRepositoryTest extends RepositoryTest {

    private DestinationRepository repository;
    private GroupRepository groupRepository;
    private long SOME_DATE_NOT_LONG_AGO = 1398008282884l;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        repository = new DestinationRepository();
        groupRepository = new GroupRepository();
    }

    @Test
    public void shouldSelectQueueDestinationByIdWithoutRelations() {

        //  when
        Destination destination = repository.get(getSession(), 1000l);

        //  then
        assertThat(destination, allOf(
                hasId(1000l),
                hasName("queue/something1"),
                isOfType(QUEUE),
                wasCreatedAt(CalendarUtilsTest.TARGET_DATE),
                canBeAdminBy(everybody()),
                canBeReadBy(everybody()),
                canBeWriteBy(everybody())
        ));

    }

    @Test
    public void shouldSelectTopicDestinationByIdWithoutRelations() {

        //  when
        Destination destination = repository.get(getSession(), 1001l);

        //  then
        assertThat(destination, allOf(
                hasId(1001l),
                hasName("topic/something2"),
                wasCreatedAt(CalendarUtilsTest.TARGET_DATE),
                isOfType(TOPIC),
                canBeAdminBy(everybody()),
                canBeWriteBy(everybody()),
                canBeAdminBy(everybody())
        ));
    }

    @Test
    public void shouldSelectDestinationByIdWithRelations() {

        //  given
        Group group1002 = groupRepository.get(getSession(), 1002l);
        Group group1003 = groupRepository.get(getSession(), 1003l);
        Group group1004 = groupRepository.get(getSession(), 1004l);
        Group group1005 = groupRepository.get(getSession(), 1005l);

        //  when
        Destination destination = repository.get(getSession(), 1002l);

        //  then
        assertThat(destination, allOf(
                hasId(1002l),
                hasName("queue/something3"),
                wasCreatedAt(CalendarUtilsTest.TARGET_DATE),
                canBeAdminBy(group1005, group1004),
                canBeWriteBy(group1003),
                canBeReadBy(group1002)
        ));
    }

    @Test
    public void shouldDeleteDestinationByIdWithoutRelations() {

        //  when
        boolean deleted = repository.delete(getSession(), 1001l);

        //  then
        assertThat(deleted, is(true));
        Destination deletedDestination = repository.get(getSession(), 1001l);
        assertThat(deletedDestination, is(nullValue()));

    }

    @Test
    public void shouldDeleteDestinationByIdWithRelations() {

        //  when
        boolean deleted = repository.delete(getSession(), 1002l);
        Group groupAssociatedWithDestination = groupRepository.get(getSession(), 1002l);

        //  then
        assertThat(deleted, is(true));
        Destination deletedDestination = repository.get(getSession(), 1002l);
        assertThat(deletedDestination, is(nullValue()));

        assertThat(groupAssociatedWithDestination, is(notNullValue()));


    }

    @Test
    public void shouldReturnFalseForUpdatingNotRecognizedDestination() {

        //  given
        Destination destination = new Destination.Builder(
                "shouldReturnFalseForUpdatingNotRecognizedDestination", QUEUE)
                .id(999999l)
                .build();

        //  when
        boolean updated = repository.update(getSession(), destination);

        //  then
        assertThat(updated, is(false));
    }

    @Test
    public void shouldListNonPagedDestinations() {

        //  given
        List<Destination> expected = Arrays.asList(
                repository.get(getSession(), 1000l),
                repository.get(getSession(), 1002l),
                repository.get(getSession(), 1001l)
        );

        //  when
        List<Destination> destinations = repository.list(getSession(), DBConst.NO_LIMIT, DBConst.NO_OFFSET);

        //  then
        assertThat(destinations, is(equalTo(expected)));
    }

    @Test
    public void shouldListPagedDestinations() {

        //  given
        List<Destination> expected = Arrays.asList(
                repository.get(getSession(), 1000l),
                repository.get(getSession(), 1002l));

        //  when
        List<Destination> destinations = repository.list(getSession(), 2, 0);

        //  then
        assertThat(destinations, is(equalTo(expected)));
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
    public void shouldThrowIAEOnLackOfIdentityOfDestinationToUpdate() {

        //  given
        Destination destination = new Destination.Builder("testX", QUEUE).build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to update should have identity set");

        //  when
        repository.update(getSession(), destination);

    }

    @Test
    public void shouldThrowNPEOnNullDestinationToCreate() {

        //  then
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Object to persist should not be null");

        //  when
        repository.create(getSession(), null);
    }

    @Test
    public void shouldThrowIAEOnInvalidCreateDestination() {

        //  given
        Destination destination = new Destination.Builder("queue/test2", QUEUE).id(1l).build();

        //  then
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Object to create should not have identity set");

        //  when
        repository.create(getSession(), destination);

    }

    @Test
    public void shouldCreateDestinationWithoutRelations() {

        //  given
        String name = "queue/testcreate";
        Destination destination = new Destination.Builder(name, QUEUE).build();

        //  when
        Long id = repository.create(getSession(), destination);

        //  then
        assertThat(id, is(greaterThan(0l)));
        Destination created = repository.get(getSession(), id);

        assertThat(created, allOf(
                hasName(name),
                wasJustCreated(),
                hasIdSet(),
                canBeAdminBy(everybody()),
                canBeWriteBy(everybody()),
                canBeReadBy(everybody())
        ));

    }

    @Test
    public void shouldCreateDestinationWithRelations() {

        //  given
        String name = "queue/testcreate";

        Group g1000 = groupRepository.get(getSession(), 1000l);
        Group g1001 = groupRepository.get(getSession(), 1001l);
        Group g1002 = groupRepository.get(getSession(), 1002l);
        Group g1003 = groupRepository.get(getSession(), 1003l);
        Group g1004 = groupRepository.get(getSession(), 1004l);
        Group g1005 = groupRepository.get(getSession(), 1005l);

        Destination destination = new Destination.Builder(name, QUEUE)
                .groupsAuthorizedToAdmin(Sets.newHashSet(g1000, g1001))
                .groupsAuthorizedToRead(Sets.newHashSet(g1002, g1003))
                .groupsAuthorizedToWrite(Sets.newHashSet(g1003, g1004, g1005))
                .build();


        //  when
        Long id = repository.create(getSession(), destination);

        //  then
        assertThat(id, is(greaterThan(0l)));
        Destination created = repository.get(getSession(), id);
        assertThat(created.getCreationTime().getMillis(), is(greaterThan(CalendarUtilsTest.TARGET_DATE_AS_LONG)));
        assertThat(created, allOf(
                hasName(name),
                wasJustCreated(),
                hasIdSet(),
                canBeAdminBy(destination.getAdminAuthorizedGroups()),
                canBeReadBy(destination.getReadAuthorizedGroups()),
                canBeWriteBy(destination.getWriteAuthorizedGroups())
        ));


    }

    @Test
    public void shouldUpdateDestinationWithoutRelations() {

        //  given
        Destination destination = new Destination.Builder("shouldUpdateDestinationWithoutRelations", QUEUE).id(1000l).build();
        Destination destinationBeforeUpdate = repository.get(getSession(), destination.getId());

        //  when
        boolean updated = repository.update(getSession(), destination);
        Destination afterUpdate = repository.get(getSession(), destination.getId());

        //  then
        assertThat(updated, is(true));
        assertThat(afterUpdate, allOf(
                wasCreatedAt(destinationBeforeUpdate.getCreationTime()),
                hasName("shouldUpdateDestinationWithoutRelations"),
                canBeAdminBy(everybody()),
                canBeWriteBy(everybody()),
                canBeReadBy(everybody())
        ));

    }

    @Test
    public void shouldUpdateDestinationWithRelations() {

        //  given
        String name = "queue/testcreate";
        Destination beforeUpdate = repository.get(getSession(), 1002l);

        Group g1000 = groupRepository.get(getSession(), 1000l);
        Group g1001 = groupRepository.get(getSession(), 1001l);
        Group g1002 = groupRepository.get(getSession(), 1002l);
        Group g1003 = groupRepository.get(getSession(), 1003l);
        Group g1004 = groupRepository.get(getSession(), 1004l);
        Group g1005 = groupRepository.get(getSession(), 1005l);

        Destination toUpdate = new Destination.Builder(name, QUEUE)
                .creationtime(beforeUpdate.getCreationTime())
                .id(beforeUpdate.getId())
                .groupsAuthorizedToAdmin(Sets.newHashSet(g1000, g1001))
                .groupsAuthorizedToRead(Sets.newHashSet(g1002, g1003))
                .groupsAuthorizedToWrite(Sets.newHashSet(g1003, g1004, g1005))
                .build();


        //  when
        boolean updated = repository.update(getSession(), toUpdate);

        //  then
        assertThat(updated, is(true));
        Destination updatedDestination = repository.get(getSession(), 1002l);
        assertThat(updatedDestination, allOf(
                hasName(name),
                wasCreatedAt(toUpdate.getCreationTime()),
                canBeAdminBy(toUpdate.getAdminAuthorizedGroups()),
                canBeWriteBy(toUpdate.getWriteAuthorizedGroups()),
                canBeReadBy(toUpdate.getReadAuthorizedGroups())
        ));
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
        boolean isUnique = repository.isUnique(getSession(), "queue/something1");

        //  then
        assertThat(isUnique, is(false));

    }

    @Test
    public void shouldReturnNonUniqueForExistingNameDifferentCase() {

        //  when
        boolean isUnique = repository.isUnique(getSession(), "queue/SOMething1");

        //  then
        assertThat(isUnique, is(false));

    }


}
