package org.jaffamq.broker.destination;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.Subscription;
import org.jaffamq.broker.destination.Destination;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;

import java.util.Iterator;

/**
 * Destination of type Queue.
 */
public class Queue extends Destination {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private Iterator iterator;

    public Queue(String destination) {
        super(destination);
        iterator = subscriptions.iterator();
    }

    /**
     * Grabs the next subscriber. Currently simple round robin like.
     * TODO: optimise for single subscription.
     * @return
     */
    private Subscription getSubscriptionToSendMessage(){


        if(iterator.hasNext()){
            return (Subscription)iterator.next();
        }

        iterator = subscriptions.iterator();
        /*
            This below should NOT trow exception because in onStompMessage we check that there are elements.
         */
        return (Subscription)iterator.next();
    }

    @Override
    protected void onStompMessage(StompMessage message) {

        if(subscriptions.size() == 0){
            //  no subscribers available nothing to do
            return;
        }

        Subscription subscription = getSubscriptionToSendMessage();
        subscription.getSubscriber().tell(new SubscribedStompMessage(message, subscription.getSubscriptionId()), getSelf());

    }
}
