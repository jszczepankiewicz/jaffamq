package org.jaffamq.broker.destination.persistence;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageRequest;
import org.jaffamq.persistence.journal.UnconsumedMessageJournalRepository;

/**
 * Actor responsible for managing StoreUnconsumedMessageReqest in na blocking safe way
 * by using internal bounded router.
 */
public class StoreUnconsumedMessageService extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private ActorRef router;

    private UnconsumedMessageJournalRepository repo;

    private int poolSize = 1;

    public StoreUnconsumedMessageService(UnconsumedMessageJournalRepository repo) {

        this.repo = repo;
        router = getContext().actorOf(Props.create(StoreUnconsumedMessageHandler.class, /*destinationManager,*/ repo).withRouter(new RoundRobinRouter(poolSize)));
    }

    @Override
    public void onReceive(Object msg) {

        if (msg instanceof StoreUnconsumedMessageRequest) {
            log.debug("Received StoreUnconsumedMessageRequest");

            router.tell(msg, getSelf());

        } else if (msg instanceof Terminated) {
            throw new IllegalStateException("Unimplemented");
        } else {
            unhandled(msg);
        }
    }
}
