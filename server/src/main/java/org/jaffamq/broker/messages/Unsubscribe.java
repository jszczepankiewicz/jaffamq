package org.jaffamq.broker.messages;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 27.08.13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class Unsubscribe {

    private final String destination;
    private final String subscriptionId;

    public Unsubscribe(String destination, String subscriptionId) {
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
