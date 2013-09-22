package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract destination manager that manages different types of destinations and routes
 * messages to and from the to the clients.
 */

public abstract class DestinationManager extends UntypedActor{


    //  TODO: weak references?
    private Map<String, ActorRef> destinations = new HashMap<String, ActorRef>();

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    protected abstract ActorRef createDestinationForName(String destination);

    private ActorRef getOrCreateDestinationForName(String destination){

        ActorRef destinationActor = destinations.get(destination);

        if(destinationActor == null){
            log.info("Creating destinationActor for destination: {}", destination);
            destinationActor = createDestinationForName(destination);
            destinations.put(destination, destinationActor);
        }

        return destinationActor;
    }

    @Override
    public void onReceive(Object o) throws Exception {

        log.info("DestinationHandler.onReceive: {}", o);

        if(o instanceof StompMessage){

            StompMessage m = (StompMessage)o;
            log.info("Received stomp message wit message-id: {}", m.getMessageId());
            ActorRef destination = getOrCreateDestinationForName(m.getDestination());
            destination.tell(o, getSender());
            return;
        }
        else if(o instanceof SubscriberRegister){
            String destination =((SubscriberRegister)o).getDestination();
            ActorRef destinationActor = getOrCreateDestinationForName(destination);
            destinationActor.tell(o, getSender());
            return;
        }
        else if(o instanceof Terminated){
            log.warning("Implement me");
        }
        else if(o instanceof Unsubscribe){

            Unsubscribe message = (Unsubscribe)o;
            log.info("received Unsubscribe to destination {} from {}", message.getDestination(), getSender());

            //  may be received only from socket
            //  check if we have topics that can be unsubscribed
            ActorRef destinationActor = destinations.get(message.getDestination());

            if(destinationActor != null){
                log.info("Found destination actor for name {}", message.getDestination());
                destinationActor.tell(o, getSender());
            }
            else{
                log.info("Can not found destination actor for name {}", message.getDestination());
            }

            return;
        }
        else{
            log.warning("Received unexpected message: {}", o);
            unhandled(o);
        }
    }
}
