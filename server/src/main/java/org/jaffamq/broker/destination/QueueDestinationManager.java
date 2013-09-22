package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Actor responsible for managing queue destinations.
 */
public class QueueDestinationManager extends DestinationManager {

    public static final String NAME="queueDestinationManager";

    @Override
    protected ActorRef createDestinationForName(String destination) {

        final Props props = Props.create(Queue.class, destination);
        return getContext().system().actorOf(props);

    }

}

