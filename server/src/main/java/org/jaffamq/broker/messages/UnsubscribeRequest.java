package org.jaffamq.broker.messages;

/**
 *
 */
public class UnsubscribeRequest {

    private final String destination;
    private final String subscriptionId;

    public UnsubscribeRequest(String destination, String subscriptionId) {
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
