package org.jaffamq.broker.messages.persistence;

import org.jaffamq.persistence.PersistedMessageId;

/**
 * Message returned after sucessfull storing unconsumed message.
 */
public class StoreUnconsumedMessageResponse {

    private final PersistedMessageId mid;
    private final String destination;

    public StoreUnconsumedMessageResponse(PersistedMessageId mid, String destination) {
        this.mid = mid;
        this.destination = destination;
    }

    public PersistedMessageId getMid() {
        return mid;
    }

    public String getDestination() {
        return destination;
    }
}
