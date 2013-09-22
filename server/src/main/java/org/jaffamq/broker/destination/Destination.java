package org.jaffamq.broker.destination;

import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.Subscription;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;
import org.jaffamq.broker.messages.UnsubscriptionConfirmed;

import java.util.Iterator;
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

    @Override
    public void onReceive(Object o) throws Exception {

        log.info("Destination actor: [{}] onReceive: {}", destination, o);

        if (o instanceof StompMessage) {
            StompMessage m = (StompMessage) o;

            onStompMessage(m);

           log.info("Received StompMessage from {} with message-id: {}, number of current subscriptions: {}", getSender(), m.getMessageId(), subscriptions.size());
            return;

        } else if (o instanceof Terminated || o instanceof Unsubscribe) {
            log.debug("Received Terminated from {}", getSender());
            //  TODO: optimize that
            //subscribers.remove(getSender());

            if (o instanceof Unsubscribe) {
                Unsubscribe unsubscribe = (Unsubscribe) o;
                Iterator<Subscription> iterator = subscriptions.iterator();

                log.info("Received Unsubscribe from {} to subscription {}", getSender(), unsubscribe.getSubscriptionId());

                while (iterator.hasNext()) {

                    Subscription subscription = iterator.next();

                    if (subscription.getSubscriptionId().equals(unsubscribe.getSubscriptionId()) && subscription.getSubscriber().equals(getSender())) {
                        log.info("Found subscription to unsubscribe");
                        iterator.remove();

                        //  we need to inform back the session that the unsubscription was successfull
                        getSender().tell(new UnsubscriptionConfirmed(unsubscribe.getSubscriptionId(), unsubscribe.getDestination()), getSelf());
                        break;
                    }
                }
            } else {
                throw new IllegalStateException("Unimplemented");
            }
        } else if (o instanceof SubscriberRegister) {
            SubscriberRegister register = (SubscriberRegister) o;
            Subscription subscription = new Subscription(register.getSubscriptionId(), getSender());
            subscriptions.add(subscription);
            log.info("Received SubscriberRegister from {} to destination: {}, number of subscribers: {}", getSender(), destination, subscriptions.size());
        } else {
            log.warning("Received unknow message: {}", o);
            unhandled(o);
        }
    }
}
