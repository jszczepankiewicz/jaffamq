package org.jaffamq.broker;

import org.jaffamq.messages.StompMessage;

/**
 * Interface that should be implemented by class that
 * sends StompMessages.
 */
public interface StompMessageSender {

    void sendStompMessage(StompMessage message);
}
