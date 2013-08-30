package org.jaffamq.broker.messages;

/**
 * Indicates messages that should be send to specific TCP client. It contains individual subscriptionId specific for client.
 */
public class SubscribedStompMessage extends StompMessage {

    private final String subscriptionId;

    public SubscribedStompMessage(StompMessage source, String subscriptionId) {
        super(source.getDestination(), source.getBody(), source.getHeaders());
        this.subscriptionId = subscriptionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscribedStompMessage that = (SubscribedStompMessage) o;

        if (subscriptionId != null ? !subscriptionId.equals(that.subscriptionId) : that.subscriptionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return subscriptionId != null ? subscriptionId.hashCode() : 0;
    }
}
