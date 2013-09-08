package org.jaffamq.broker.messages;

import akka.util.ByteString;
import org.jaffamq.Headers;

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

    /**
     * Serializes to String to be transmitted.
     * TODO: change that to ByteString
     * @return
     */
    public String toTransmit(){
        final String NL="\r\n";
        StringBuilder builder = new StringBuilder();
        builder.append("MESSAGE");
        builder.append(NL);
        builder.append("subscription:");
        builder.append(subscriptionId);
        System.out.println("XXXXXXXXXXXX: " + subscriptionId);
        System.out.println("XXXXXXXXXXXX: " + subscriptionId.length());
        builder.append(NL);
        builder.append("message-id:");
        builder.append(getHeaders().get(Headers.SET_MESSAGE_ID));
        builder.append(NL);
        builder.append("destination:");
        builder.append(getDestination());
        builder.append(NL);
        builder.append("content-type:");
        builder.append(getHeaders().get(Headers.CONTENT_TYPE));
        builder.append(NL);
        builder.append(NL);
        builder.append(getBody());
        builder.append("\000\n");

        return builder.toString();
    }
}
