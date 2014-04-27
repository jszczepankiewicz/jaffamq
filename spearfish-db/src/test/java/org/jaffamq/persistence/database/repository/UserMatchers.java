package org.jaffamq.persistence.database.repository;

import com.google.common.collect.Sets;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jaffamq.persistence.database.CalendarUtils;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.user.User;
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
public class UserMatchers {

    public static Set<Group> noGroups() {
        return Collections.EMPTY_SET;
    }

    /**
     * Tests whether object was created no more than couple seconds ago
     *
     * @return
     */
    public static Matcher<User> wasJustCreated() {
        return new FeatureMatcher<User, Integer>(not(greaterThan(3)), "seconds since creationtime", "seconds since creationtime") {
            @Override
            protected Integer featureValueOf(final User actual) {
                return Seconds.secondsBetween(actual.getCreationTime(), CalendarUtils.now()).getSeconds();
            }
        };
    }

    public static Matcher<User> hasLogin(final String name) {
        return new FeatureMatcher<User, String>(equalTo(name), "login", "login") {
            @Override
            protected String featureValueOf(final User actual) {
                return actual.getLogin();
            }
        };
    }

    public static Matcher<User> wasCreatedAt(final DateTime creationtime) {
        return new FeatureMatcher<User, DateTime>(equalTo(creationtime), "creationtime", "creationtime") {
            @Override
            protected DateTime featureValueOf(final User actual) {
                return actual.getCreationTime();
            }
        };
    }

    public static Matcher<User> hasPassword(final String name) {
        return new FeatureMatcher<User, String>(equalTo(name), "password", "password") {
            @Override
            protected String featureValueOf(final User actual) {
                return actual.getPassword();
            }
        };
    }

    public static Matcher<User> hasPasswordhash(final String name) {
        return new FeatureMatcher<User, String>(equalTo(name), "passwordhash", "passwordhash") {
            @Override
            protected String featureValueOf(final User actual) {
                return actual.getPasswordhash();
            }
        };
    }

    public static Matcher<User> isAssignedTo(final Group... groups) {
        return isAssignedTo(Sets.newHashSet(groups));
    }

    public static Matcher<User> isAssignedTo(final Set<Group> groups) {
        return new FeatureMatcher<User, Set<Group>>(equalTo(groups), "groups", "groups") {
            @Override
            protected Set<Group> featureValueOf(final User actual) {
                return actual.getGroups();
            }
        };
    }


}
