package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.io.TcpPipelineHandler;
import akka.util.ByteString;
import org.jaffamq.Frame;
import org.jaffamq.Headers;
import org.jaffamq.ParserFrameState;
import org.jaffamq.broker.messages.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor that represents conversation state
 * between client and server. It has mutable state.
 */
public class ClientSessionHandler extends ParserFrameState {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private final ActorRef destinationManager;

    private final ActorRef connection;

    /*
        Unfortunatelly the unsubscribe client frame contains only subscription id (unique among client session), but no destination.
        In order to correctly unsubscribe we need also destinationId. Best place to store the mapping between destination and subscriptionId (per client)
        is the ClientSessionHandler. Question is where should we un
     */
    private Map<String, String> destinationBySubscriptionId = new HashMap<>();

    // this will hold the pipeline handler’s context
    private final TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init;

    public ClientSessionHandler(ActorRef connection, ActorRef destinationManager, TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init) {
        this.destinationManager = destinationManager;
        this.init = init;
        this.connection = connection;
        //this.connection = null;
    }

    @Override
    public void onReceive(Object msg) throws Exception {

        log.info("ClientSessionHandler.onReceive: {}", msg);

        if (msg instanceof Tcp.CommandFailed) {
            getContext().stop(getSelf());

        }
        else if(msg instanceof SubscribedStompMessage){
            SubscribedStompMessage m = (SubscribedStompMessage)msg;
            log.info("Received SubscribedStompMessage with set-message-id: {}", m.getHeaders().get(Headers.SET_MESSAGE_ID));
            onSubscribedMessage(m);

        }
        else if(msg instanceof UnsubscriptionConfirmed){

            UnsubscriptionConfirmed u = (UnsubscriptionConfirmed)msg;
            log.info("Received UnsubsciptionConfirmed with destination: {} and subscriptionId: {}", u.getDestination(), u.getSubscriptionId());
            destinationBySubscriptionId.remove(u.getSubscriptionId());

        }
        else if (msg instanceof TcpPipelineHandler.Init.Event) {

            // unwrap TcpPipelineHandler’s event to get a Tcp.Event
            final String recv = init.event(msg);

            // inform someone of the received message
            //listener.tell(recv, getSelf());

            parseLine(recv);
        }
        else{
            log.warning("unhandled message: {}", msg);
            unhandled(msg);
        }

    }

    private void handleUnimplementedFrame() {
        log.error("ERROR: unimplemented client frame command: {}", currentFrameCommand);
        getSender().tell(init.command(String.format("ERROR\nmessage:unimplemented client command %s\n\000\n", currentFrameCommand)), getSelf());
        //connection.tell(TcpMessage.write(response), )
        //ByteString response = ByteString.fromString()
    }

    private void handleConnectFrame() {
        log.error("XXX: connection: {}, getSender(): {}", connection, getSender());
        final ByteString response = ByteString.fromString("CONNECTED\nversion:1.2\n\000\n");
        connection.tell(TcpMessage.write(response), getSelf());
    }

    private void onSubscribedMessage(SubscribedStompMessage msg){
        log.warning("onSubscribedMessage");
        connection.tell(TcpMessage.write(ByteString.fromString(msg.toTransmit())), getSender());
    }

    private void handleDisconnectFrame() {

        String receiptHeader = headers.get("receipt");

        if (receiptHeader != null) {
            //  todo add encoding
            getSender().tell(init.command("RECEIPT\nreceipt-id:" + Frame.encodeHeaderValue(receiptHeader) + "\n\000\n"), getSelf());
        }

        getSender().tell(TcpMessage.close(), getSender());
    }

    private void handleUnsubscribeFrame(){
        //  TODO: validate headers
        String subscriptionId = headers.get(Headers.SUBSCRIPTION_ID);

        //  need to find out the destination by subscriptionId
        String destination = destinationBySubscriptionId.get(subscriptionId);
        if(destination == null){
            log.error("Can not found destination for subscriptionId: {}, unsubscription can not proceed!", subscriptionId);
        }
        else{
            destinationManager.tell(new Unsubscribe(destination, subscriptionId), getSelf());
        }

    }

    private void handleSubscribeFrame() {

        //  TODO: validate headers
        String destination = headers.get(Headers.DESTINATION);
        String subscriptionId = headers.get(Headers.SUBSCRIPTION_ID);

        //  we need to store destination by subscriptionId to able to quickly serve unsubscribe with only subscriptionId
        destinationBySubscriptionId.put(subscriptionId, destination);
        log.info("Received SUBSCRIBE to destination: {} with id: {}", destination, subscriptionId);

        //  passing self as the subscriber
        destinationManager.tell(new SubscriberRegister(destination, subscriptionId), getSelf());
    }

    private void handleSendFrame(){
        //  TODO: validate headers
        //  TODO: headers + body should be in some struct on stack and not on heap.
        String destination = headers.get(Headers.DESTINATION);
        destinationManager.tell(new StompMessage(destination, getCurrentFrameBody(), headers), getSender());
    }

    private void reactToCommandParsed() {
        log.debug("Finished parsing client frame: {}", currentFrameCommand);

        switch (currentFrameCommand) {

            case CONNECT:
                handleConnectFrame();
                break;
            case DISCONNECT:
                handleDisconnectFrame();
                break;
            case SEND:
                handleSendFrame();
                break;
            case SUBSCRIBE:
                handleSubscribeFrame();
                break;
            case UNSUBSCRIBE:
                handleUnsubscribeFrame();
                break;
            default:
                handleUnimplementedFrame();
                break;
        }
    }

    @Override
    protected void transition(Frame.FrameParsingState old, Frame.FrameParsingState next) {
        log.debug("Transition state from {} to {}", old, next);

        if (next == Frame.FrameParsingState.FINISHED_PARSING) {
            reactToCommandParsed();
        }



    }

}
