import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.*;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpPipelineHandler.Init;
import akka.io.TcpPipelineHandler.WithinActorContext;
import akka.util.ByteString;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

import static akka.io.PipelineStage.sequence;

public class AkkaTCPClient extends UntypedActor {

    final InetSocketAddress remote;
    //final SSLContext sslContext;
    final ActorRef listener;
    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());
    // this will hold the pipeline handler’s context
    Init<WithinActorContext, String, String> init = null;

    public AkkaTCPClient(InetSocketAddress remote,
                         ActorRef listener) {
        this.remote = remote;

        this.listener = listener;

        // open a connection to the remote TCP port
        Tcp.get(getContext().system()).getManager()
                .tell(TcpMessage.connect(remote), getSelf());
    }

    @Override
    public void onReceive(Object msg) {
        //log.info("onReceive: {}", msg);

        if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());

        } else if (msg instanceof Connected) {
            // create a javax.net.ssl.SSLEngine for our peer in client mode


            // build pipeline and set up context for communicating with TcpPipelineHandler
            init = TcpPipelineHandler.withLogger(log, sequence(sequence(sequence(
                    new StringByteStringAdapter("utf-8"),
                    new DelimiterFraming(1024, ByteString.fromString("\n"), true)),
                    new TcpReadWriteAdapter()),
                    //new SslTlsSupport(engine)),
                    new BackpressureBuffer(1000, 10000, 1000000)));

            // create handler for pipeline, setting ourselves as payload recipient
            final ActorRef handler = getContext().actorOf(
                    TcpPipelineHandler.props(init, getSender(), getSelf()));

            // register the SSL handler with the connection
            getSender().tell(TcpMessage.register(handler), getSelf());

            // and send a message across the SSL channel
            handler.tell(init.command("CONNECT\naccept-version:1.2\nhost:localhost\n\nsomebodysomebody"), getSelf());

        } else if (msg instanceof Init.Event) {
            // unwrap TcpPipelineHandler’s event into a Tcp.Event
            final String recv = init.event(msg);
            // and inform someone of the received payload
            listener.tell(recv, getSelf());
        }
    }
}