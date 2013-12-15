package org.jaffamq.broker.destination.persistence;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageRequest;
import org.jaffamq.persistence.journal.UnconsumedMessageJournalRepository;


/**
 * TODO: Refactor to share most of it with *Service
 */
public class PollUnconsumedMessageService extends UntypedActor {

    private ActorRef router;

    private UnconsumedMessageJournalRepository repo;

    private int poolSize = 4;

    public PollUnconsumedMessageService(UnconsumedMessageJournalRepository repo) {

        this.repo = repo;
        router = getContext().actorOf(Props.create(PollUnconsumedMessageHandler.class, repo).withRouter(new RoundRobinRouter(poolSize)));
    }

    @Override
    public void onReceive(Object msg) {


        if (msg instanceof PollUnconsumedMessageRequest) {
            router.tell(msg, getSelf());

        } else if (msg instanceof Terminated) {
            throw new IllegalStateException("Unimplemented");
        } else {
            unhandled(msg);
        }
    }
}
