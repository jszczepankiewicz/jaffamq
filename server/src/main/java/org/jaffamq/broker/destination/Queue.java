package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.Subscription;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.UnsubscribeRequest;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageResponse;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageResponse;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Destination of type Queue.
 */
public class Queue extends Destination {

    /*
        TODO: It would be more clever to store not all of them in memory ;)
     */
    private java.util.Queue<PersistedMessageId> unconsumedMessages = new ArrayDeque();

    private boolean refreshIterator;

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());


    private Iterator iterator;

    private ActorRef storeUnconsumedMessageService;
    private ActorRef pollUnconsumedMessageService;

    public Queue(String destination, ActorRef storeUnconsumedMessageService, ActorRef pollUnconsumedMessageService, java.util.Queue<PersistedMessageId> unconsumedMessagesOnStartup) {

        super(destination);
        log.info("Creation of queue for destination: {}", destination);

        this.storeUnconsumedMessageService = storeUnconsumedMessageService;
        this.pollUnconsumedMessageService = pollUnconsumedMessageService;

        iterator = subscriptions.iterator();

        //  TODO: convert to Queue
        if (unconsumedMessagesOnStartup != null) {
            unconsumedMessages = new ArrayDeque<>(unconsumedMessagesOnStartup);
        }

    }

    private void firePollUnconsumedRequestIfAvailable() {

        if (!unconsumedMessages.isEmpty()) {
            log.info("Detected {} unconsumed messages for destination: {}", unconsumedMessages.size(), destination);
            pollUnconsumedMessageService.tell(new PollUnconsumedMessageRequest(unconsumedMessages.peek()), getSelf());
        } else {
            log.info("Not detected unconsumed messages for destination: {}", destination);
        }
    }

    @Override
    protected void onSubscriberRegister(SubscriberRegister register) {

        super.onSubscriberRegister(register);
        firePollUnconsumedRequestIfAvailable();
    }

    @Override
    protected void onPollUnconsumedMessageResponse(PollUnconsumedMessageResponse response) {

        log.debug("Received PollUnconsumedMessageResponse");
        onStompMessage(response.getMessage());
        boolean removed = unconsumedMessages.remove(response.getPersistedMessageId());

        if (!removed) {
            log.warning("Tried to removed nonexistent persistedMessageId equalTo: {} on collection with size: {}", response.getPersistedMessageId(), unconsumedMessages.size());
        }

        firePollUnconsumedRequestIfAvailable();
    }

    @Override
    protected void onStoreUnconsumedMessageResponse(StoreUnconsumedMessageResponse response) {
        log.debug("Received StoreUnconsumedMessageResponse");
        unconsumedMessages.add(response.getMid());
    }

    @Override
    protected void onSubscriptionRemoved(UnsubscribeRequest unsubscribeRequest) {
        refreshIterator = true;
    }

    /**
     * Grabs the next subscriber. Currently simple round robin like.
     * TODO: optimise for single subscription.
     *
     * @return
     */
    private Subscription getSubscriptionToSendMessage() {

        if (refreshIterator) {
            iterator = subscriptions.iterator();
            refreshIterator = false;
        }

        if (iterator.hasNext()) {
            return (Subscription) iterator.next();
        }

        //  there is no more elements in this iterator we need to create new iterator
        iterator = subscriptions.iterator();

        //  this below should NOT trow exception because in onStompMessage we check that there are elements.
        return (Subscription) iterator.next();
    }

    @Override
    protected void onStompMessage(StompMessage message) {

        if (subscriptions.isEmpty()) {
            log.info("No active subscribers to: {} storing message with id: {} for future consumption", destination, message.getMessageId());
            //  no active subscriptions, we should store it
            storeUnconsumedMessageService.tell(new StoreUnconsumedMessageRequest(message), getSelf());

            return;
        } else {
            log.info("Active subscribers to: {} are present, will not store message with id: {} for future consumption", destination, message.getMessageId());
        }

        Subscription subscription = getSubscriptionToSendMessage();
        subscription.getSubscriber().tell(new SubscribedStompMessage(message, subscription.getSubscriptionId()), getSelf());

    }
}
