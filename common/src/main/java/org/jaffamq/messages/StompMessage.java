package org.jaffamq.messages;

import java.io.Serializable;
import java.util.Map;

/**
 * Message class that contains typical Stomp message:
 * destination, body, headers
 */
public class StompMessage implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StompMessage that = (StompMessage) o;

        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (headers != null ? !headers.equals(that.headers) : that.headers != null) return false;
        if (messageId != null ? !messageId.equals(that.messageId) : that.messageId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = destination != null ? destination.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
        return result;
    }
}
