package org.jaffamq.persistence;

import org.jaffamq.Headers;
import org.jaffamq.messages.StompMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 24.10.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class FastStompMessageSerializerTest {

    private FastStompMessageSerializer serializer = new FastStompMessageSerializer();

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
