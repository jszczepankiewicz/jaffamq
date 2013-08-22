import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.*;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpPipelineHandler.Init;
import akka.io.TcpPipelineHandler.WithinActorContext;
import akka.util.ByteString;
import org.jaffamq.Command;
import org.jaffamq.Frame;
import org.jaffamq.ParserFrameState;

import java.net.InetSocketAddress;

import static akka.io.PipelineStage.sequence;

public class StompServer extends ParserFrameState {

    final ActorRef listener;

    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public StompServer(InetSocketAddress remote, ActorRef listener) {

        this.listener = listener;

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
            // create a javax.net.ssl.SSLEngine for our peer in server mode
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


    @Override
    protected void transition(Frame.FrameParsingState old, Frame.FrameParsingState next) {
        log.debug("Transition state from {} to {}", old, next);
        System.out.println("Transition from: " + old + " to: " + next);

        if (next == Frame.FrameParsingState.FINISHED_PARSING) {

            log.debug("Finished parsing client frame: {}", currentFrameCommand);

            if (currentFrameCommand == Command.CONNECT) {

                getSender().tell(init.command("CONNECTED\nversion:1.2\n\000\n"), getSelf());
                return;
            }
            if (currentFrameCommand == Command.DISCONNECT) {

                String receiptHeader = headers.get("receipt");

                if (receiptHeader != null) {
                    //  todo add encoding
                    getSender().tell(init.command("RECEIPT\nreceipt-id:" + receiptHeader + "\n\000\n"), getSelf());
                }

                getSender().tell(TcpMessage.close(), getSender());
                return;
            } else {
                throw new IllegalStateException("Unimplemented when currentFrameCommand = " + currentFrameCommand);
            }
        }

    }


}