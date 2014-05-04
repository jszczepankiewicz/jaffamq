package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.NotFoundInSetPredicate;
import org.jaffamq.persistence.database.group.Group;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class NotFoundInSetPredicateTest {

    private Group build(Long id, String name) {
        return new Group.Builder(name).id(id).build();
    }

    @Test
    public void shouldFoundDifference() {

        //  given
        Set<Group> groupsStored = Sets.newHashSet(build(1L, "a"), build(2L, "b"), build(3L, "c"), build(4L, "d"));
        Set<Group> groupsToStore = Sets.newHashSet(build(1L, "a"), build(3L, "c"), build(4L, "d"), build(5L, "f"), build(6L, "g"));

        //  when
        Set<Group> toPersist = Sets.filter(groupsToStore, new NotFoundInSetPredicate(groupsStored));
        Set<Group> toRemove = Sets.filter(groupsStored, new NotFoundInSetPredicate(groupsToStore));

        //  then
        assertThat(toPersist, is(equalTo((Set) Sets.newHashSet(build(5L, "f"), build(6L, "g")))));
        assertThat(toRemove, is(equalTo((Set) Sets.newHashSet(build(2L, "b")))));
    }

}