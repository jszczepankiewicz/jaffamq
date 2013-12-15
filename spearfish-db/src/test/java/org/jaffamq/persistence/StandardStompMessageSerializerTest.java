package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;
import org.jaffamq.test.StompMessageFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 25.10.13
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public class StandardStompMessageSerializerTest {

    private StompMessageSerializer serializer = new StandardStompMessageSerializer();

    @Test
    public void shouldSerializeToAndFromBytes() throws Exception{

        //  given
        StompMessage msg = StompMessageFactory.createMessage();

        //  when
        byte[] serialized = serializer.toBytes(msg);
        StompMessage unserialized = serializer.fromBytes(serialized);

        //  then
        assertThat(serialized, is(notNullValue()));
        assertThat(serialized.length, is(greaterThan(10)));

        assertThat(unserialized, is(notNullValue()));
        assertThat(unserialized, is(not(sameInstance(msg))));
        assertThat(unserialized, is(equalTo(msg)));
    }
}
