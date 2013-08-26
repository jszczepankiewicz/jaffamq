package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;

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

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private Set<ActorRef> subscribers = new LinkedHashSet<ActorRef>();

    @Override
    public void onReceive(Object o) throws Exception {

        if(o instanceof StompMessage){

            log.debug("Received StompMessage from {}", getSender());

            for(ActorRef subscriber:subscribers){
                subscriber.tell(o, getSelf());
            }
        }
        else if(o instanceof Terminated){
            log.debug("Received Terminated from {}", getSender());
            subscribers.remove(getSender());
        }
        else if(o instanceof SubscriberRegister){
            log.debug("Received SubscriberRegister from {}", getSender());
            subscribers.add(getSender());
        }
        else{
            unhandled(o);
        }
    }
}
