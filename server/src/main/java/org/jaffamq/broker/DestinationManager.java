package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.jaffamq.Headers;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor responsible for managing destinations. Currently limited to only topics.
 */
public class DestinationManager extends UntypedActor{

    public static final String NAME="destinationManager";

    //  TODO: weak references?
    private Map<String, ActorRef> topics = new HashMap<String, ActorRef>();

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private ActorRef getOrCreateTopicForDestination(String destination){

        ActorRef topic = topics.get(destination);

        if(topic == null){
            log.info("Creating topic for destination: {}", destination);
            final Props props = Props.create(Topic.class, destination);
            topic = getContext().system().actorOf(props);
            topics.put(destination, topic);
        }

        return topic;
    }

    @Override
    public void onReceive(Object o) throws Exception {

        log.info("DestinationHandler.onReceive: {}", o);

        if(o instanceof StompMessage){

            StompMessage m = (StompMessage)o;
            log.info("Received stomp message wit message-id: {}", m.getMessageId());
            ActorRef topic = getOrCreateTopicForDestination(m.getDestination());
            topic.tell(o, getSender());
            return;
        }
        else if(o instanceof SubscriberRegister){
            String destination =((SubscriberRegister)o).getDestination();
            ActorRef topic = getOrCreateTopicForDestination(destination);
            topic.tell(o, getSender());
            return;
        }
        else if(o instanceof Terminated){
            //  I assume this is from topic
            //Topic topic = ((Topic)getSender())
            //  TODO: remove me
        }
        else if(o instanceof Unsubscribe){

            Unsubscribe message = (Unsubscribe)o;
            log.info("received Unsubscribe to destination {} from {}", message.getDestination(), getSender());

            //  may be received only from socket
            //  check if we have topics that can be unsubscribed
            ActorRef topic = topics.get(message.getDestination());

            if(topic != null){
                log.info("Found topic for destination {}", message.getDestination());
                topic.tell(o, getSender());
            }
            else{
                log.info("Can not found topic for destination {}", message.getDestination());
            }

            return;
        }
        else{
            log.warning("Received unexpected message: {}", o);
            unhandled(o);
        }
    }
}
