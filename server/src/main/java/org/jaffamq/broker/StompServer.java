package org.jaffamq.broker;

import akka.actor.ActorRef;
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
import org.jaffamq.broker.messages.SubscriberRegister;

import java.net.InetSocketAddress;

import static akka.io.PipelineStage.sequence;

public class StompServer extends ParserFrameState {

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

    // this will hold the pipeline handler’s context
    Init<WithinActorContext, String, String> init = null;

    @Override
    public void onReceive(Object msg) {

        log.info("StompServer.onReceive: {}", msg);

        if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());

        } else if (msg instanceof Tcp.Bound) {
            listener.tell(msg, getSelf());

        } else if (msg instanceof Connected) {
            // create a javax.net.ssl.SSLEngine for our peer in broker mode
            final InetSocketAddress remote = ((Connected) msg).remoteAddress();

            // build pipeline and set up context for communicating with TcpPipelineHandler
            init = TcpPipelineHandler.withLogger(log, sequence(sequence(sequence(
                    new StringByteStringAdapter("utf-8"),
                    new DelimiterFraming(1024, ByteString.fromString("\n"), true)),
                    new TcpReadWriteAdapter()),
                    new BackpressureBuffer(1000, 10000, 1000000)));

            // create handler for pipeline, setting ourselves as payload recipient
            final ActorRef handler = getContext().actorOf(
                    TcpPipelineHandler.props(init, getSender(), getSelf()));

            // register the SSL handler with the connection
            getSender().tell(TcpMessage.register(handler), getSelf());

        } else if (msg instanceof Init.Event) {

            // unwrap TcpPipelineHandler’s event to get a Tcp.Event
            final String recv = init.event(msg);

            // inform someone of the received message
            listener.tell(recv, getSelf());

            parseLine(recv);
        }
    }

    private void handleUnimplementedFrame() {
        System.out.println("WARNING: unimplemented client frame command: " + currentFrameCommand);
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