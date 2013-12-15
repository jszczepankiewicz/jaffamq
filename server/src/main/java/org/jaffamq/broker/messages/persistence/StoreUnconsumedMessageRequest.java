package org.jaffamq.broker.messages.persistence;

import org.jaffamq.messages.StompMessage;

/**
 * Request to store message in persistent storage that can not be consumed currently due to lack of consumers for
 * given destination.
 */
public class StoreUnconsumedMessageRequest {
    private final StompMessage message;

    public StoreUnconsumedMessageRequest(StompMessage message) {
        this.message = message;
    }

    public StompMessage getMessage() {
        return message;
    }
}
