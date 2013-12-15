package org.jaffamq.broker.destination.persistence;

import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by urwisy on 14.12.13.
 */
public class UnconsumedMessageRepositoryStub implements UnconsumedMessageRepository {

    @Override
    public Map<String, List<PersistedMessageId>> getPersistedMessagesByLocation() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public PersistedMessageId persistMessage(StompMessage message) {
        return null;
    }

    @Override
    public StompMessage pollMessage(PersistedMessageId id) {
        return null;
    }
}
