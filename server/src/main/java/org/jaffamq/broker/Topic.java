package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;

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

    //private Set<ActorRef> subscribers = new LinkedHashSet<ActorRef>();
    private Set<Subscription> subscriptions = new LinkedHashSet<Subscription>();

    Topic(String destination){
        this.destination = destination;
    }

    @Override
    public void onReceive(Object o) throws Exception {

        if(o instanceof StompMessage){

            log.debug("Received StompMessage from {}", getSender());
            StompMessage m = (StompMessage)o;
            /*
            for(ActorRef subscriber:subscribers){
                subscriber.tell(o, getSelf());
            }
            */
            for(Subscription subscription:subscriptions){
                subscription.getSubscriber().tell(new SubscribedStompMessage(m, subscription.getSubscriptionId()), getSelf());
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
                    }
                }
            }
            else{
                throw new IllegalStateException("Unimplemented");
            }
        }
        else if(o instanceof SubscriberRegister){
            log.debug("Received SubscriberRegister from {}", getSender());
            //subscribers.add(getSender());
            SubscriberRegister register = (SubscriberRegister)o;
            Subscription subscription = new Subscription(register.getSubscriptionId(), getSender());
            subscriptions.add(subscription);
        }
        else{
            unhandled(o);
        }
    }
}
