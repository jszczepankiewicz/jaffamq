package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.destination.Topic;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor responsible for managing topic destinations.
 */
public class TopicDestinationManager extends DestinationManager{

    public static final String NAME="topicDestinationManager";

    @Override
    protected ActorRef createDestinationForName(String destination) {

        final Props props = Props.create(Topic.class, destination);
        return getContext().system().actorOf(props);

    }

}
