package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.io.TcpPipelineHandler;
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

    // this will hold the pipeline handler’s context
    private final TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init;

    public ClientSessionHandler(ActorRef destinationManager, TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init) {
        this.destinationManager = destinationManager;
        this.init = init;
    }

    @Override
    public void onReceive(Object msg) throws Exception {

        log.info("ClientSessionHandler.onReceive: {}", msg);

        if (msg instanceof Tcp.CommandFailed) {
            getContext().stop(getSelf());

        }
        else if(msg instanceof SubscribedStompMessage){
            onSubscribedMessage((SubscribedStompMessage)msg);

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
        log.warning("WARNING: unimplemented client frame command: {}", currentFrameCommand);
        getSender().tell(init.command(String.format("ERROR\nmessage:unimplemented client command %s\n\000\n", currentFrameCommand)), getSelf());
    }

    private void handleConnectFrame() {
        getSender().tell(init.command("CONNECTED\nversion:1.2\n\000\n"), getSelf());
    }

    private void onSubscribedMessage(SubscribedStompMessage msg){
        log.warning("onSubscribedMessage");
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

        destinationManager.tell(new SubscriberRegister(destination, subscriptionId), getSender());
    }

    private void handleSendFrame(){
        //  TODO: validate headers
        String destination = headers.get(Headers.DESTINATION);
        destinationManager.tell(new StompMessage(destination, null, headers), getSender());
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
