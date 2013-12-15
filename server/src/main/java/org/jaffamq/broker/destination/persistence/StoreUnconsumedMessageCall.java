package org.jaffamq.broker.destination.persistence;


import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class StoreUnconsumedMessageCall implements Callable<PersistedMessageId> {

    private static final Logger LOG = LoggerFactory.getLogger(StoreUnconsumedMessageCall.class);

    private StompMessage messageToStore;
    private UnconsumedMessageRepository repo;

    public StoreUnconsumedMessageCall(StompMessage messageToStore, UnconsumedMessageRepository repo) {
        this.messageToStore = messageToStore;
        this.repo = repo;
    }

    @Override
    public PersistedMessageId call() {
        LOG.info("Before persisting message in repository");
        return repo.persistMessage(messageToStore);
    }
}
