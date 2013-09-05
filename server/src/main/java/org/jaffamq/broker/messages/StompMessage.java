package org.jaffamq.broker.messages;

import akka.util.ByteString;

import java.util.Map;

/**
 * Message class that contains typical Stomp message:
 * destination, body, headers
 */
public class StompMessage {

    private final String destination;
    private final ByteString body;
    private final Map<String, String> headers;

    public StompMessage(String destination, ByteString body, Map<String, String> headers) {
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
