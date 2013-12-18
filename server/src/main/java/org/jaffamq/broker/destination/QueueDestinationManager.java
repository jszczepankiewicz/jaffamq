package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.jaffamq.persistence.PersistedMessageId;

import java.util.Map;

/**
 * Actor responsible for managing queue destinations.
 */
public class QueueDestinationManager extends DestinationManager {

    public static final String NAME = "queueDestinationManager";

    /**
     * This is not very cool. This is temporarily path used by actorSelection in order to avoid cycled dependencies.
     */
    public static final String LOCAL_PATH = "akka://TestServerApp/user/queueDestinationManager";

    private ActorRef storeUnconsumedMessageService;
    private ActorRef pollUnconsumedMessageService;
    private Map<String, java.util.Queue<PersistedMessageId>> unconsumedMessagesByDestinationFromPreviousSession;


    public QueueDestinationManager(ActorRef storeUnconsumedMessageService, ActorRef pollUnconsumedMessageService, Map<String, java.util.Queue<PersistedMessageId>> unconsumedMessages) {
        this.storeUnconsumedMessageService = storeUnconsumedMessageService;
        this.pollUnconsumedMessageService = pollUnconsumedMessageService;
        this.unconsumedMessagesByDestinationFromPreviousSession = unconsumedMessages;
    }

    @Override
    protected ActorRef createDestinationForName(String destination) {

        java.util.Queue<PersistedMessageId> unconsumedMessages = unconsumedMessagesByDestinationFromPreviousSession.get(destination);
        final Props props = Props.create(Queue.class, destination, storeUnconsumedMessageService, pollUnconsumedMessageService, unconsumedMessages);
        ActorRef queue = getContext().system().actorOf(props);
        unconsumedMessagesByDestinationFromPreviousSession.remove(destination);
        return queue;

    }

}

