package org.jaffamq.broker.transaction;

import org.jaffamq.RequestValidationFailedException;
import org.jaffamq.broker.StompMessageSender;
import org.jaffamq.messages.StompMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * In memory transaction. It is used as in-memory buffer. Please note that currently number of messages is not limited thus
 * it may lead to out of memory error.
 * <p/>
 * Since: 28.09.13
 */
public class VolatileMemoryTransaction implements Transaction {

    private static final Logger LOG = LoggerFactory.getLogger(VolatileMemoryTransaction.class);

    private Status status = Status.STARTED;

    private StompMessageSender sender;

    private final String clientTransactionName;

    private java.util.Queue<StompMessage> uncommitedMessages = new ArrayDeque<>();

    public VolatileMemoryTransaction(String clientTransactionName, StompMessageSender sender) {
        this.clientTransactionName = clientTransactionName;
        this.sender = sender;

    }

    @Override
    public void addStompMessage(StompMessage message) {
        uncommitedMessages.add(message);
    }

    @Override
    public Iterator<StompMessage> getMessagesInTransaction() {
        return uncommitedMessages.iterator();
    }

    @Override
    public void commit() throws RequestValidationFailedException {

        LOG.info("Transaction [{}] has {} uncommited messages", clientTransactionName, uncommitedMessages.size());

        TransactionStatusVerifier.assertTransactionStatus(Status.COMMITED, status);

        Iterator<StompMessage> messagesIterator = getMessagesInTransaction();
        while (messagesIterator.hasNext()) {
            sender.sendStompMessage(messagesIterator.next());
        }

        this.status = Status.COMMITED;
    }


    @Override
    public void rollback() throws RequestValidationFailedException {

        TransactionStatusVerifier.assertTransactionStatus(Status.ROLLBACKED, status);

        uncommitedMessages = new ArrayDeque<>();

        status = Status.ROLLBACKED;
    }

    public String getName() {
        return clientTransactionName;
    }
}
