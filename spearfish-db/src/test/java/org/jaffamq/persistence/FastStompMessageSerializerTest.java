package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;
import org.jaffamq.test.StompMessageFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test for FST serializer.
 */
public class FastStompMessageSerializerTest {

    private FastStompMessageSerializer serializer = new FastStompMessageSerializer();

    @Test
    public void shouldSerializeToAndFromBytes() throws Exception {

        //  given
        StompMessage msg = StompMessageFactory.createMessage();

        //  when
        byte[] serialized = serializer.toBytes(msg);
        StompMessage unserialized = serializer.fromBytes(serialized);

        //  then
        assertThat(serialized, is(notNullValue()));
        assertThat(serialized.length, is(greaterThan(10)));

        assertThat(unserialized,
                allOf(
                        notNullValue(),
                        not(sameInstance(msg)),
                        equalTo(msg)));
    }
}
