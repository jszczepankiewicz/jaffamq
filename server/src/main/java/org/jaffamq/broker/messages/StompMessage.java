package org.jaffamq.broker.messages;

import java.util.Map;

/**
 * Message class that contains typical Stomp message:
 * destination, body, headers
 */
public class StompMessage {

    private final String destination;
    private final String body;
    private final Map<String, String> headers;
    private final String messageId;

    public StompMessage(String destination, String body, Map<String, String> headers, String messageId) {
        this.destination = destination;
        this.body = body;
        this.headers = headers;
        this.messageId = messageId;
    }

    public String getDestination() {
        return destination;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMessageId() {
        return messageId;
    }
}
