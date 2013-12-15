package org.jaffamq;

import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;


import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.core.IsEqual.equalTo;


public class FrameTest {

    @Test
    public void shouldEncodeHeader(){

        //  given
        String source = "ęćśżabc\rxyz\nuzy:iz\\";

        //  when
        String encoded = Frame.encodeHeaderValue(source);

        //  then
        assertThat(encoded, is(equalTo("ęćśżabc\\rxyz\\nuzy\\ciz\\\\")));
    }

    @Test
    public void shouldDecodeHeader(){

        //  given
        String source = "ęćśżabc\\rxyz\\nuzy\\ciz\\\\";

        //  when
        String decoded = Frame.decodeHeaderValue(source);

        //  then
        assertThat(decoded, is(equalTo("ęćśżabc\rxyz\nuzy:iz\\")));

    }
}
