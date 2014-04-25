package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.repository.group.Group;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class NotFoundInSetPredicateTest {

    @Test
    public void shouldFoundDifference(){

        //  given
        Set<Group> groupsStored = Sets.newHashSet(new Group(1l, "a"), new Group(2l, "b"), new Group(3l, "c"), new Group(4l, "d"));
        Set<Group> groupsToStore = Sets.newHashSet(new Group(1l, "a"), new Group(3l, "c"), new Group(4l, "d"), new Group(5l, "f"), new Group(6l, "g"));

        //  when
        Set<Group> toPersist = Sets.filter(groupsToStore, new NotFoundInSetPredicate(groupsStored));
        Set<Group> toRemove = Sets.filter(groupsStored, new NotFoundInSetPredicate(groupsToStore));

        //  then
        assertThat(toPersist, is(equalTo((Set)Sets.newHashSet(new Group(5l, "f"), new Group(6l, "g")))));
        assertThat(toRemove, is(equalTo((Set)Sets.newHashSet(new Group(2l, "b")))));
    }

}