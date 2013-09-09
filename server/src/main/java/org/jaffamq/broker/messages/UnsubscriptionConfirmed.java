package org.jaffamq.broker.messages;

/**
 * Message send by Topic to the ClientSessionHandler (through DestinationManager) when unsubscrption is done.
 * This message is sign to the ClientSessionHandler to remove mapping between destination and subscrptionId.
 */
public class UnsubscriptionConfirmed {

    private final String subscriptionId;
    private final String destination;

    public UnsubscriptionConfirmed(String subscriptionId, String destination) {
        this.subscriptionId = subscriptionId;
        this.destination = destination;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getDestination() {
        return destination;
    }
}
