package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;
import org.jaffamq.test.StompMessageFactory;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;


public class StandardStompMessageSerializerTest {

    private StompMessageSerializer serializer = new StandardStompMessageSerializer();

    @Test
    public void shouldSerializeToAndFromBytes() {

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
                        is(notNullValue()),
                        is(not(sameInstance(msg))),
                        is(equalTo(msg)))
        );

    }
}
