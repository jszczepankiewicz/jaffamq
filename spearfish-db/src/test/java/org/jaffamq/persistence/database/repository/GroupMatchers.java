package org.jaffamq.persistence.database.repository;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.group.Group;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Created by urwisy on 2014-04-27.
 */
public class GroupMatchers {

    /**
     * Simplified test if group is taken from db. It just checks whether group has Id > 0, creationtime != null and name.length > 0
     *
     * @return
     */
    public static Matcher<Group> isSane() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Tests whether object was created no more than couple seconds ago
     *
     * @return
     */
    public static Matcher<Group> wasJustCreated() {
        return new FeatureMatcher<Group, Integer>(not(greaterThan(3)), "seconds since creationtime", "seconds since creationtime") {
            @Override
            protected Integer featureValueOf(final Group actual) {
                return Seconds.secondsBetween(actual.getCreationtime(), CalendarUtils.now()).getSeconds();
            }
        };
    }

    public static Matcher<Group> hasName(final String name) {
        return new FeatureMatcher<Group, String>(equalTo(name), "name", "name") {
            @Override
            protected String featureValueOf(final Group actual) {
                return actual.getName();
            }
        };
    }

    public static Matcher<Group> wasCreatedAt(final DateTime creationtime) {
        return new FeatureMatcher<Group, DateTime>(equalTo(creationtime), "creationtime", "creationtime") {
            @Override
            protected DateTime featureValueOf(final Group actual) {
                return actual.getCreationtime();
            }
        };
    }


}
