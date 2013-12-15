package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;

import java.util.List;
import java.util.Map;

/**
 * Repository for operation (blocking) related to unconsumed messages.
 */
public interface UnconsumedMessageRepository {

    int MAXIMUM_POLL_DURATION_MS = 2000;
    int MAXIMUM_PERSIST_DURATION_MS = 2000;

    Map<String, List<PersistedMessageId>> getPersistedMessagesByLocation();

    /**
     * Initialize repository. After returning from this method repository is ready to serve operations.
     */
    void init();

    void shutdown();

    /**
     * Persist message in repository and return id that can be used to further retrieve message from repo.
     * @param message
     * @return persisted message unique id
     */
    PersistedMessageId persistMessage(StompMessage message);

    /**
     * Retrieve and delete from repository this particular message.
     * @return message or null if not found.
     */
    StompMessage pollMessage(PersistedMessageId id);



}
