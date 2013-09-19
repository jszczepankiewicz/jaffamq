package org.jaffamq.broker.messages;

/**
 * Message send by Topic to the ClientSessionHandler (through TopicDestinationManager) when unsubscrption is done.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsubscriptionConfirmed that = (UnsubscriptionConfirmed) o;

        if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
        if (subscriptionId != null ? !subscriptionId.equals(that.subscriptionId) : that.subscriptionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subscriptionId != null ? subscriptionId.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}
