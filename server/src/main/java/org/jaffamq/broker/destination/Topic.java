package org.jaffamq.broker.destination;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.Subscription;
import org.jaffamq.broker.destination.Destination;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;

/**
 * Destination that represents Topic. All topic subscribers receive same message.
 */
public class Topic extends Destination {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public Topic(String destination) {
        super(destination);
    }

    @Override
    protected void onStompMessage(StompMessage message) {

        log.info("Received StompMessage from {} with message-id: {}, number of current subscriptions: {}", getSender(), message.getMessageId(), subscriptions.size());

        for (Subscription subscription : subscriptions) {
            subscription.getSubscriber().tell(new SubscribedStompMessage(message, subscription.getSubscriptionId()), getSelf());
            log.info("Sent message to subscriber: {}", subscription.getSubscriber());
        }
    }
}
