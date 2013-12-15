package org.jaffamq.broker.messages.persistence;

import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;

/**
 * Message send after poll unconsumed message returned from repository.
 */
public class PollUnconsumedMessageResponse {

    private final StompMessage message;
    private final PersistedMessageId persistedMessageId;

    public PollUnconsumedMessageResponse(StompMessage message, PersistedMessageId persistedMessageId) {
        this.message = message;
        this.persistedMessageId = persistedMessageId;
    }

    public StompMessage getMessage() {
        return message;
    }

    public PersistedMessageId getPersistedMessageId() {
        return persistedMessageId;
    }
}
