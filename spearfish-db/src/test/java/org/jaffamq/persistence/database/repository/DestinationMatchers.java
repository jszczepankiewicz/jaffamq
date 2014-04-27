package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.destination.Destination;
import org.jaffamq.persistence.database.group.Group;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Created by urwisy on 2014-04-27.
 */
public class DestinationMatchers {

    public static Set<Group> everybody() {
        return Collections.emptySet();
    }


    /**
     * Tests whether object was created no more than couple seconds ago
     *
     * @return
     */
    public static Matcher<Destination> wasJustCreated() {
        return new FeatureMatcher<Destination, Integer>(not(greaterThan(3)), "seconds since creationtime", "seconds since creationtime") {
            @Override
            protected Integer featureValueOf(final Destination actual) {
                return Seconds.secondsBetween(actual.getCreationTime(), CalendarUtils.now()).getSeconds();
            }
        };
    }

    public static Matcher<Destination> hasName(final String name) {
        return new FeatureMatcher<Destination, String>(equalTo(name), "name", "name") {
            @Override
            protected String featureValueOf(final Destination actual) {
                return actual.getName();
            }
        };
    }

    public static Matcher<Destination> wasCreatedAt(final DateTime creationtime) {
        return new FeatureMatcher<Destination, DateTime>(equalTo(creationtime), "creationtime", "creationtime") {
            @Override
            protected DateTime featureValueOf(final Destination actual) {
                return actual.getCreationTime();
            }
        };
    }

    public static Matcher<Destination> isOfType(final Destination.Type type) {
        return new FeatureMatcher<Destination, Destination.Type>(equalTo(type), "type", "type") {
            @Override
            protected Destination.Type featureValueOf(final Destination actual) {
                return actual.getType();
            }
        };
    }

    public static Matcher<Destination> canBeReadBy(final Group... groups) {
        return canBeReadBy(Sets.newHashSet(groups));
    }

    public static Matcher<Destination> canBeWriteBy(final Group... groups) {
        return canBeWriteBy(Sets.newHashSet(groups));
    }

    public static Matcher<Destination> canBeAdminBy(final Group... groups) {
        return canBeAdminBy(Sets.newHashSet(groups));
    }

    public static Matcher<Destination> canBeReadBy(final Set<Group> groups) {
        return new FeatureMatcher<Destination, Set<Group>>(equalTo(groups), "readAuthorizedGroups", "readAuthorizedGroups") {
            @Override
            protected Set<Group> featureValueOf(final Destination actual) {
                return actual.getReadAuthorizedGroups();
            }
        };
    }

    public static Matcher<Destination> canBeWriteBy(final Set<Group> groups) {
        return new FeatureMatcher<Destination, Set<Group>>(equalTo(groups), "writeAuthorizedGroups", "writeAuthorizedGroups") {
            @Override
            protected Set<Group> featureValueOf(final Destination actual) {
                return actual.getWriteAuthorizedGroups();
            }
        };
    }

    public static Matcher<Destination> canBeAdminBy(final Set<Group> groups) {
        return new FeatureMatcher<Destination, Set<Group>>(equalTo(groups), "adminAuthorizedGroups", "adminAuthorizedGroups") {
            @Override
            protected Set<Group> featureValueOf(final Destination actual) {
                return actual.getAdminAuthorizedGroups();
            }
        };
    }


}
