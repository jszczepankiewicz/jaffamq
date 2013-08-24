package org.jaffamq;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 22.08.13
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
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
