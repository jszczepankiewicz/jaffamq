package org.jaffamq.persistence;

import org.jaffamq.Headers;
import org.jaffamq.messages.StompMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * FIXME: remove me (reploace with common-test)
 */
public class StompMessageFactory {

    public static StompMessage createMessage(){
        return createMessage("destinations" + System.currentTimeMillis());
    }

    public static StompMessage createMessage(String destination){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Headers.CONTENT_TYPE, "application/json");
        headers.put(Headers.SUBSCRIPTION_ID, "345");

        StompMessage message = new StompMessage(destination, "somebody\nsomeline\n", headers, "xyz324");
        return message;
    }
}
