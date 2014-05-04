package org.jaffamq.persistence.database.repository;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jaffamq.persistence.database.Identifiable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Created by urwisy on 2014-04-27.
 */
public class IdentifiableMatchers {

    public static <T extends Identifiable> Matcher<T> hasIdSet() {
        return new FeatureMatcher<T, Long>((greaterThan(0L)), "id", "id") {
            @Override
            protected Long featureValueOf(final T actual) {
                return actual.getId();
            }
        };
    }

    public static <T extends Identifiable> Matcher<T> hasId(final Long id) {
        return new FeatureMatcher<T, Long>(equalTo(id), "id", "id") {
            @Override
            protected Long featureValueOf(T actual) {
                return actual.getId();
            }
        };
    }
}
