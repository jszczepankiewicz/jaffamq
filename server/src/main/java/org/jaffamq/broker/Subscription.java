package org.jaffamq.broker;

import akka.actor.ActorRef;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 30.08.13
 * Time: 23:22
 * To change this template use File | Settings | File Templates.
 */
public class Subscription {

    private final String subscriptionId;
    private final ActorRef subscriber;

    public Subscription(String subscriptionId, ActorRef subscriber) {
        this.subscriptionId = subscriptionId;
        this.subscriber = subscriber;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public ActorRef getSubscriber() {
        return subscriber;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        if (subscriber != null ? !subscriber.equals(that.subscriber) : that.subscriber != null) return false;
        if (subscriptionId != null ? !subscriptionId.equals(that.subscriptionId) : that.subscriptionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subscriptionId != null ? subscriptionId.hashCode() : 0;
        result = 31 * result + (subscriber != null ? subscriber.hashCode() : 0);
        return result;
    }
}
