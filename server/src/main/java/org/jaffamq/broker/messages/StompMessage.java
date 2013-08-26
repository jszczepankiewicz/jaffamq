package org.jaffamq.broker.messages;

import akka.util.ByteString;

import java.util.HashMap;

/**
 * Message class that contains typical Stomp message:
 * destination, body, headers
 */
public class StompMessage {

    private final String destination;
    private final ByteString body;
    private final HashMap<String, String> headers;

    public StompMessage(String destination, ByteString body, HashMap<String, String> headers) {
        this.destination = destination;
        this.body = body;
        this.headers = headers;
    }

    public String getDestination() {
        return destination;
    }

    public ByteString getBody() {
        return body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
