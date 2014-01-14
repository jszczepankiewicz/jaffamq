package org.jaffamq.persistence.database.repository;

import org.hamcrest.Matchers;
import org.jaffamq.persistence.database.dto.User;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by urwisy on 12.01.14.
 */
public class UserRepositoryTest extends RepositoryTest {

    private UserRepository repository;

    @Before
    public void init() {
        repository = new UserRepository();
    }

    @Test
    public void shouldHaveAdminUserCreatedAfterStart() throws Exception {

        //  when
        User admin = repository.getUser(getSession(), UserRepository.SUPERADMIN_LOGIN, UserRepository.SUPERADMIN_PASSWORD_DEFAULT);

        //  then
        assertThat(admin, is(notNullValue()));
        assertThat(admin.getId(), is(greaterThan(0)));
        assertThat(admin.getLogin(), is(equalTo(UserRepository.SUPERADMIN_LOGIN)));
        assertThat(admin.getPasswordhash().length(), is(greaterThan(3)));
        assertThat(admin.getGroups(), is(Matchers.notNullValue()));
        assertThat(admin.getGroups().size(), is(equalTo(1)));
        assertThat(admin.getGroups().get(0).getName(), is(equalTo(UserRepository.ADMINS_GROUP)));
    }

}
