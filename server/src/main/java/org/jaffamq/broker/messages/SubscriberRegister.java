package org.jaffamq.broker.messages;

/**
 * Message send to register sender as destination subscriber.
 */
public class SubscriberRegister {

    private final String destination;

    public SubscriberRegister(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
