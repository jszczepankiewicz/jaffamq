package org.jaffamq.broker.destination.persistence;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.Errors;
import org.jaffamq.broker.destination.QueueDestinationManager;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageResponse;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;

import java.util.concurrent.*;

/**
 * Actor responsible for storing unconsumed message. WARNING:
 * this class is using blocking access to UnconsumedMessageRepository and
 * have to be run from router, not directly!.
 */
public class StoreUnconsumedMessageHandler extends UntypedActor{

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private final UnconsumedMessageRepository repo;
    private ActorRef destinationManager;

    public StoreUnconsumedMessageHandler(UnconsumedMessageRepository repo) {
        this.repo = repo;
    }

    private PersistedMessageId storeUnconsumedMessage(StompMessage message){
        //  TODO: refactor to use thread pool
        RunnableFuture<PersistedMessageId> future = new FutureTask<>(new StoreUnconsumedMessageCall(message, repo));
        future.run();

        PersistedMessageId retval = null;

        try {
            retval = future.get(UnconsumedMessageRepository.MAXIMUM_PERSIST_DURATION_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            future.cancel(true);
            throw new PersistenceException(Errors.UNCOSUMEND_MESSAGE_PERSIST_UNEXPECTED_ERROR, e, "For message id: " + message.getMessageId());
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new PersistenceException(Errors.UNCONSUMED_MESSAGE_PERSIST_TIMEOUT, "For message id: " + message.getMessageId());
        }

        return retval;

    }

    /**
     * Qeurying for QueueDestinationManager using Identify
     * @throws Exception
     */
    @Override
    public void preStart() {
        ActorSelection queueDestinationManager = getContext().actorSelection(QueueDestinationManager.LOCAL_PATH);
        queueDestinationManager.tell(new Identify(null), getSelf());
    }

    @Override
    public void onReceive(Object o) {

        if(o instanceof StoreUnconsumedMessageRequest){
            StoreUnconsumedMessageRequest request = (StoreUnconsumedMessageRequest)o;

            //  WARNING: blocking operation
            PersistedMessageId pid = storeUnconsumedMessage(request.getMessage());

            StoreUnconsumedMessageResponse response = new StoreUnconsumedMessageResponse(pid, request.getMessage().getDestination());

            log.info("Before sending StoreUnconsumedMessageResponse to {}", destinationManager);

            if(destinationManager == null){
                //  TODO: change to some code
                throw new IllegalStateException("Destination Manager not yet discovered");
            }

            destinationManager.tell(response, getSelf());
            return;
        }
        else if(o instanceof ActorIdentity){

            ActorIdentity identity = (ActorIdentity)o;
            if(QueueDestinationManager.LOCAL_PATH.equals(identity.getRef().path().toString())){
                log.info("Successfully discovered QueueDestinationManager");
                destinationManager = identity.getRef();
            }
            else{
                //  TODO: add error code
                throw new IllegalStateException("ActorIdentity retrieved. Expected path: " + QueueDestinationManager.LOCAL_PATH + ", but was: " + identity.getRef().path());
            }
            return;
        }
        else{
            unhandled(o);
        }
    }
}
