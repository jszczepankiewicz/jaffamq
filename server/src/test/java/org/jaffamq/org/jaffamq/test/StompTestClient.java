package org.jaffamq.org.jaffamq.test;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 01.09.13
 * Time: 19:44
 * To change this template use File | Settings | File Templates.
 */
public interface StompTestClient {

    static final Charset ENC=Charset.forName("UTF-8");

    /**
     * Release all resources.
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * Send Stomp frame and wait for response maximum X miliseconds.
     * @param frameResource
     * @return
     * @throws IOException
     */
    String sendFrameAndWaitForResponseFrame(String frameResource) throws IOException;
    String getResponseOrTimeout() throws IOException;

    void sendFrame(String frameResource) throws IOException;

    String connectSendAndGrabAnswer(String requestResourcePath) throws IOException;
}
