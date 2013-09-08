package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.*;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpPipelineHandler.Init;
import akka.io.TcpPipelineHandler.WithinActorContext;
import akka.util.ByteString;
import org.jaffamq.Frame;
import org.jaffamq.Headers;
import org.jaffamq.ParserFrameState;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;

import java.net.InetSocketAddress;

import static akka.io.PipelineStage.sequence;

public class StompServer extends UntypedActor {
    /**
     * If you change this change also ParserFrameState.PAYLOAD_LINE_SEPARATOR_LENGTH
     */
    public static final String PAYLOAD_LINE_SEPARATOR="\n";
    final ActorRef destinationManager;

    final ActorRef listener;

    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public StompServer(InetSocketAddress remote, ActorRef listener, ActorRef destinationManager) {

        this.listener = listener;
        this.destinationManager = destinationManager;

        // bind to a socket, registering ourselves as incoming connection handler
        Tcp.get(getContext().system()).getManager().tell(
                TcpMessage.bind(getSelf(), remote, 100),
                getSelf());
    }

    @Override
    public void onReceive(Object msg) {

        log.info("StompServer.onReceive: {}", msg);

        if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());

        } else if (msg instanceof Tcp.Bound) {
            listener.tell(msg, getSelf());

        } else if (msg instanceof Connected) {

            // build pipeline and set up context for communicating with TcpPipelineHandler
            Init<WithinActorContext, String, String> init  = TcpPipelineHandler.withLogger(log, sequence(sequence(sequence(
                    new StringByteStringAdapter("utf-8"),
                    new DelimiterFraming(1024, ByteString.fromString(PAYLOAD_LINE_SEPARATOR), true)),
                    new TcpReadWriteAdapter()),
                    new BackpressureBuffer(1000, 10000, 1000000)));

            ActorRef connection = getSender();
            //  create session handler which will handle conversation state
            ActorRef sessionHandler = getContext().actorOf(Props.create
                    (ClientSessionHandler.class, connection, destinationManager, init));


            final ActorRef handler = getContext().actorOf(
                    TcpPipelineHandler.props(init, connection, sessionHandler));

            // register the session handler with the connection
            getSender().tell(TcpMessage.register(handler), getSelf());

        }

        else{
            unhandled(msg);
        }
    }

    /*private void onSubscribedMessage(SubscribedStompMessage msg){

    }*/

    /*private void handleUnimplementedFrame() {
        log.warning("WARNING: unimplemented client frame command: {}", currentFrameCommand);
        getSender().tell(init.command(String.format("ERROR\nmessage:unimplemented client command %s\n\000\n", currentFrameCommand)), getSelf());
    }

    private void handleConnectFrame() {
        getSender().tell(init.command("CONNECTED\nversion:1.2\n\000\n"), getSelf());
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
    }*/

    /*private void reactToCommandParsed() {
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
*/

}