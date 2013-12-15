package org.jaffamq.test;

import org.jaffamq.Headers;
import org.jaffamq.messages.StompMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for StompMessage objects used in testing.
 */
public class StompMessageFactory {

    /**
     * Create stomp message with random destination.
     * @return the value with random destination.
     */
    public static StompMessage createMessage(){
        return createMessage("destinations" + System.currentTimeMillis());
    }

    /**
     * Create stomp message with hardcoded subscription id, content type of type json and specified destination.
     * @param destination for message.
     * @return the value with predefined values
     */
    public static StompMessage createMessage(String destination){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Headers.CONTENT_TYPE, "application/json");
        headers.put(Headers.SUBSCRIPTION_ID, "345");

        StompMessage message = new StompMessage(destination, "somebody\nsomeline\n", headers, "xyz324");
        return message;
    }
}
