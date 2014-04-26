package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.IdentityProvider;
import org.jaffamq.persistence.database.group.Group;
import org.jaffamq.persistence.database.user.User;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by urwisy on 19.01.14.
 */
public class IdentityProviderTest extends RepositoryTest {


    @Test
    public void shouldReturnNextIdentityForUser() {

        //  when
        Long id = IdentityProvider.getNextIdFor(getSession(), User.class);

        //  then
        assertThat(id, is(equalTo(10000L)));
        id = IdentityProvider.getNextIdFor(getSession(), User.class);
        assertThat(id, is(equalTo(10001L)));

    }

    @Test
    public void shouldReturnNextIDForGroup() {

        //  when
        Long id = IdentityProvider.getNextIdFor(getSession(), Group.class);

        //  then
        assertThat(id, is(equalTo(10000L)));
        id = IdentityProvider.getNextIdFor(getSession(), Group.class);
        assertThat(id, is(equalTo(10001L)));
    }
}
