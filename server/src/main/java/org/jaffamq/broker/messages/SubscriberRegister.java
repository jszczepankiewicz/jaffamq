package org.jaffamq.broker.messages;

/**
 * Message send to register sender as destination subscriber.
 */
public class SubscriberRegister {

    private final String destination;
    private final String subscriptionId;

    public SubscriberRegister(String destination, String subscriptionId) {
        this.destination = destination;
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getDestination() {
        return destination;
    }
}
