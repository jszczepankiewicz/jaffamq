package org.jaffamq.broker.transaction;

import org.jaffamq.RequestValidationFailedException;
import org.jaffamq.broker.messages.StompMessage;

import java.util.Iterator;

/**
 * Transaction interface. According to STOMP protocol transaction are volatile and in memory.
 */
public interface Transaction {

    /**
     * Get transaction name (set by client).
     *
     * @return client transaction name
     */
    String getName();

    /**
     * Commit the transaction.
     */
    void commit() throws RequestValidationFailedException;

    /**
     * Rollback the transaction;
     */
    void rollback() throws RequestValidationFailedException;


    /**
     * Add uncommited message to transaction.
     * @param message
     */
    void addStompMessage(StompMessage message);

    /**
     * Get next uncommited message.
     * NOTE: you can only get uncommited message once.
     * TODO: remove this? we do not need that.
     * @return StompMessage if there is uncommited message, null otherwise.
     */
    Iterator<StompMessage> getMessagesInTransaction();

    enum Status{
        STARTED,
        COMMITED,
        ROLLBACKED;
    }
}


