package org.jaffamq.broker.messages.persistence;

import org.jaffamq.persistence.PersistedMessageId;

/**
 * Request to poll unconsumed message
 */
public class PollUnconsumedMessageRequest {

    private final PersistedMessageId pid;

    public PollUnconsumedMessageRequest(PersistedMessageId pid) {
        this.pid = pid;
    }

    public PersistedMessageId getPid() {
        return pid;
    }
}
