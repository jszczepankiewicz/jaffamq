package org.jaffamq.broker.destination.persistence;


import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;

import java.util.concurrent.Callable;

public class PollMessageCall implements Callable<StompMessage> {

    private PersistedMessageId requestedPid;
    private UnconsumedMessageRepository repo;

    public PollMessageCall(PersistedMessageId pid, UnconsumedMessageRepository repo) {
        this.requestedPid = pid;
        this.repo = repo;
    }

    @Override
    public StompMessage call() throws Exception {
        return repo.pollMessage(requestedPid);
    }
}
