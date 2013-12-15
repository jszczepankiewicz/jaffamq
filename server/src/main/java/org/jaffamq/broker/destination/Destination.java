package org.jaffamq.broker.destination;

import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.Subscription;
import org.jaffamq.broker.messages.UnsubscribeRequest;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageResponse;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageResponse;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.UnsubscriptionConfirmed;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Common class for all destination types
 */
public abstract class Destination extends UntypedActor {

    protected final String destination;

    protected Set<Subscription> subscriptions = new LinkedHashSet<Subscription>();

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public Destination(String destination) {
        this.destination = destination;
    }

    protected abstract void onStompMessage(StompMessage message);

    /**
     * Called when unsubscription message was successfully finished.
     *
     * @param unsubscribeRequest
     */
    protected abstract void onSubscriptionRemoved(UnsubscribeRequest unsubscribeRequest);

    @Override
    public void onReceive(Object o){

        log.info("Destination actor: [{}] onReceive: {}", destination, o);

        if (o instanceof StompMessage) {
            StompMessage m = (StompMessage) o;

            onStompMessage(m);

           log.info("Received StompMessage from {} with message-id: {}, number of current subscriptions: {}", getSender(), m.getMessageId(), subscriptions.size());
            return;

        } else if (o instanceof Terminated || o instanceof UnsubscribeRequest) {
            log.debug("Received Terminated from {}", getSender());

            if (o instanceof UnsubscribeRequest) {
                UnsubscribeRequest unsubscribeRequest = (UnsubscribeRequest) o;

                log.info("Received UnsubscribeRequest from {} to subscription {}", getSender(), unsubscribeRequest.getSubscriptionId());

                Subscription toRemove = new Subscription(unsubscribeRequest.getSubscriptionId(), getSender());
                boolean removalConfirmed = subscriptions.remove(toRemove);

                if(removalConfirmed){
                    log.info("Unsubscription complete for sender: {} and subscriptionId: {}", toRemove.getSubscriber(), toRemove.getSubscriptionId());
                    onSubscriptionRemoved(unsubscribeRequest);
                    getSender().tell(new UnsubscriptionConfirmed(unsubscribeRequest.getSubscriptionId(), unsubscribeRequest.getDestination()), getSelf());


                }
                else{
                    /*
                        This can occur if the hashCode or equals do not work as expected.
                     */
                    log.error("Can not remove subscription because not found entry for sender: {} and subscriptionId: {}", toRemove.getSubscriber(), unsubscribeRequest.getSubscriptionId());
                }

            } else {
                throw new IllegalStateException("Unimplemented");
            }
        }
        else if(o instanceof PollUnconsumedMessageResponse){
            onPollUnconsumedMessageResponse((PollUnconsumedMessageResponse)o);
        }
        else if(o instanceof StoreUnconsumedMessageResponse){
            onStoreUnconsumedMessageResponse((StoreUnconsumedMessageResponse)o);
            return;
        }
        else if (o instanceof SubscriberRegister) {
            onSubscriberRegister((SubscriberRegister) o);
            return;
        } else {
            log.warning("Received unknown message: {}", o);
            unhandled(o);
        }
    }

    protected void onPollUnconsumedMessageResponse(PollUnconsumedMessageResponse response){
        //  do nothing
    }

    protected void onSubscriberRegister(SubscriberRegister register){

        Subscription subscription = new Subscription(register.getSubscriptionId(), getSender());
        subscriptions.add(subscription);
        log.info("Received SubscriberRegister from {} to destination: {}, number of subscribers: {}", getSender(), destination, subscriptions.size());
    }

    protected void onStoreUnconsumedMessageResponse(StoreUnconsumedMessageResponse response){
        //  do nothing by default
    }
}
