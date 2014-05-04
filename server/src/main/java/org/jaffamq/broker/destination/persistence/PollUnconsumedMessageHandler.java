package org.jaffamq.broker.destination.persistence;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.Errors;
import org.jaffamq.broker.destination.QueueDestinationManager;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageResponse;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Handler for PollUnconsumedMessageRequest.
 * WARNING:this class is using blocking access to UnconsumedMessageRepository and
 * have to be run from router, not directly!.
 */
public class PollUnconsumedMessageHandler extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private final UnconsumedMessageRepository repo;
    private ActorRef destinationManager;

    public PollUnconsumedMessageHandler(UnconsumedMessageRepository repo) {
        this.repo = repo;
    }

    private StompMessage pollMessageFromUnconsumedMessageRepository(PersistedMessageId pid) {
        //  TODO: refactor to use thread pool
        RunnableFuture<StompMessage> future = new FutureTask<>(new PollMessageCall(pid, repo));
        future.run();

        StompMessage retval = null;

        try {
            retval = future.get(UnconsumedMessageRepository.MAXIMUM_POLL_DURATION_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            future.cancel(true);
            throw new PersistenceException(Errors.UNCONSUMED_MESSAGE_POLL_UNEXPECTED_ERROR, e, "For persisted message id: " + pid);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new PersistenceException(Errors.UNCONSUMED_MESSAGE_POLL_TIMEOUT, "For persisted message id: " + pid);
        }

        return retval;
    }

    /**
     * Qeurying for QueueDestinationManager using Identify
     *
     * @throws Exception
     */
    @Override
    public void preStart() {
        ActorSelection queueDestinationManager = getContext().actorSelection(QueueDestinationManager.LOCAL_PATH);
        queueDestinationManager.tell(new Identify(null), getSelf());
    }

    @Override
    public void onReceive(Object o) {

        if (o instanceof PollUnconsumedMessageRequest) {
            PollUnconsumedMessageRequest request = (PollUnconsumedMessageRequest) o;

            //  WARNING: blocking operation
            StompMessage message = pollMessageFromUnconsumedMessageRepository(request.getPid());

            //  TODO: in order to increase perfomance we should in the future send directly to certain destination
            PollUnconsumedMessageResponse response = new PollUnconsumedMessageResponse(message, request.getPid());

            destinationManager.tell(response, getSelf());

        } else if (o instanceof ActorIdentity) {

            ActorIdentity identity = (ActorIdentity) o;
            if (QueueDestinationManager.LOCAL_PATH.equals(identity.getRef().path().toString())) {
                log.info("Successfully discovered QueueDestinationManager");
                destinationManager = identity.getRef();
            } else {
                //  TODO: add error code
                throw new IllegalStateException("ActorIdentity retrieved. Expected path: " + QueueDestinationManager.LOCAL_PATH + ", but was: " + identity.getRef().path());
            }
            return;
        } else {
            unhandled(o);
        }
    }


}
