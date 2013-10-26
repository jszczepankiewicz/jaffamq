package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.Props;

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
