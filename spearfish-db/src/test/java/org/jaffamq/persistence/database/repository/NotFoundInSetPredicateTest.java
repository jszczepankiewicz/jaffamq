package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.jaffamq.persistence.database.repository.group.Group;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class NotFoundInSetPredicateTest {

    private Group build(Long id, String name){
        return new Group.Builder(name).id(id).build();
    }

    @Test
    public void shouldFoundDifference(){

        //  given
        Set<Group> groupsStored = Sets.newHashSet(build(1l, "a"), build(2l, "b"), build(3l, "c"), build(4l, "d"));
        Set<Group> groupsToStore = Sets.newHashSet(build(1l, "a"), build(3l, "c"), build(4l, "d"), build(5l, "f"), build(6l, "g"));

        //  when
        Set<Group> toPersist = Sets.filter(groupsToStore, new NotFoundInSetPredicate(groupsStored));
        Set<Group> toRemove = Sets.filter(groupsStored, new NotFoundInSetPredicate(groupsToStore));

        //  then
        assertThat(toPersist, is(equalTo((Set)Sets.newHashSet(build(5l, "f"), build(6l, "g")))));
        assertThat(toRemove, is(equalTo((Set)Sets.newHashSet(build(2l, "b")))));
    }

}