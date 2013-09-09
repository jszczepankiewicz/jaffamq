package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.Headers;
import org.jaffamq.broker.messages.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 26.08.13
 * Time: 20:41
 * To change this template use File | Settings | File Templates.
 */
public class Topic extends UntypedActor{

    private final String destination;

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private Set<Subscription> subscriptions = new LinkedHashSet<Subscription>();

    Topic(String destination){
        this.destination = destination;
    }

    @Override
    public void onReceive(Object o) throws Exception {

        log.info("Topic: [{}] onReceive: {}", destination, o);

        if(o instanceof StompMessage){
            StompMessage m = (StompMessage)o;

            log.info("Received StompMessage from {} with message-id: {}, number of current subscriptions: {}", getSender(), m.getMessageId(), subscriptions.size());

            for(Subscription subscription:subscriptions){
                subscription.getSubscriber().tell(new SubscribedStompMessage(m, subscription.getSubscriptionId()), getSelf());
                log.info("Sent message to subscriber: {}", subscription.getSubscriber());
            }
        }
        else if(o instanceof Terminated || o instanceof Unsubscribe){
            log.debug("Received Terminated from {}", getSender());
            //  TODO: optimize that
            //subscribers.remove(getSender());

            if(o instanceof Unsubscribe){
                Unsubscribe unsubscribe = (Unsubscribe)o;
                Iterator<Subscription> iterator = subscriptions.iterator();

                log.info("Received Unsubscribe from {} to subscription {}", getSender(), unsubscribe.getSubscriptionId());

                while(iterator.hasNext()){

                    Subscription subscription = iterator.next();

                    if(subscription.getSubscriptionId().equals(unsubscribe.getSubscriptionId()) && subscription.getSubscriber().equals(getSender())){
                        log.info("Found subscription to unsubscribe");
                        iterator.remove();

                        //  we need to inform back the session that the unsubscription was successfull
                        getSender().tell(new UnsubscriptionConfirmed(unsubscribe.getSubscriptionId(), unsubscribe.getDestination()), getSelf());
                        break;
                    }
                }
            }
            else{
                throw new IllegalStateException("Unimplemented");
            }
        }
        else if(o instanceof SubscriberRegister){
            log.info("Received SubscriberRegister from {} to destination: {}", getSender(), destination);
            //subscribers.add(getSender());
            SubscriberRegister register = (SubscriberRegister)o;
            Subscription subscription = new Subscription(register.getSubscriptionId(), getSender());
            subscriptions.add(subscription);
        }
        else{
            log.warning("Received unknow message: {}", o);
            unhandled(o);
        }
    }
}
