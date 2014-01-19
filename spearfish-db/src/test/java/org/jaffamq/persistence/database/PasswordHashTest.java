package org.jaffamq.persistence.database;

import org.jaffamq.persistence.database.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by urwisy on 14.01.14.
 */
public class PasswordHashTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnHashForEmptyString() {

        //  given
        String emptyString = "";

        //  when
        String hash = PasswordHash.hash(emptyString);

        //  then
        assertThat(hash, is(equalTo("73865413c1c7b277c002fd705f14b568d18ae167366adcf69b575c52bcec7dc9")));
    }

    @Test
    public void shouldReturnHashForNonEmptyString() {

        //  given
        String password = UserRepository.SUPERADMIN_PASSWORD_DEFAULT;

        //  when
        String hash = PasswordHash.hash(password);

        //  then
        assertThat(hash, is(equalTo(UserRepository.SUPERADMIN_PASSWORD_HASH)));

    }

    @Test
    public void shouldThrowIAEForNullPassword() {

        //  then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("String to hash should not be null");

        //  when
        PasswordHash.hash(null);

    }
}
