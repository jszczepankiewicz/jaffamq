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
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;

/**
 * Actor that represents conversation state
 * between client and server. It has mutable state.
 */
public class ClientSessionHandler extends ParserFrameState {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private final ActorRef destinationManager;

    private final ActorRef connection;

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

    private void handleSubscribeFrame() {

        //  TODO: validate headers
        String destination = headers.get(Headers.DESTINATION);
        String subscriptionId = headers.get(Headers.SUBSCRIPTION_ID);
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
