import akka.actor.*;
import akka.actor.IO;
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
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static akka.io.PipelineStage.sequence;

public class AkkaTCPServer extends ParserFrameState {

    private static final FiniteDuration DELAYED_MS_BEFORE_DISCONNECT = Duration.create(5000, TimeUnit.MILLISECONDS);

    //final SSLContext sslContext;
    final ActorRef listener;

    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    public AkkaTCPServer(InetSocketAddress remote, ActorRef listener) {

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
        log.info("AkkaTCPServer.onReceive: {}", msg);
        if (msg instanceof CommandFailed) {
            getContext().stop(getSelf());

        } else if (msg instanceof Tcp.Bound) {
            listener.tell(msg, getSelf());

        } else if (msg instanceof Connected) {
            // create a javax.net.ssl.SSLEngine for our peer in server mode
            final InetSocketAddress remote = ((Connected) msg).remoteAddress();
            /*
            final SSLEngine engine = sslContext.createSSLEngine(
                    remote.getHostName(), remote.getPort());
            engine.setUseClientMode(false);
            */
            // build pipeline and set up context for communicating with TcpPipelineHandler
            init = TcpPipelineHandler.withLogger(log, sequence(sequence(sequence(
                    new StringByteStringAdapter("utf-8"),
                    new DelimiterFraming(1024, ByteString.fromString("\n"), true)),
                    //new DelimiterFraming(1024, ByteString.fromString("\000"), true)),
                    new TcpReadWriteAdapter()),
                    //new SslTlsSupport(engine)),
                    new BackpressureBuffer(1000, 10000, 1000000)));

            // create handler for pipeline, setting ourselves as payload recipient
            final ActorRef handler = getContext().actorOf(
                    TcpPipelineHandler.props(init, getSender(), getSelf()));

            // register the SSL handler with the connection
            getSender().tell(TcpMessage.register(handler), getSelf());

        } else if (msg instanceof Init.Event) {
            log.info("onReceive with Init.Event");
            // unwrap TcpPipelineHandler’s event to get a Tcp.Event
            final String recv = init.event(msg);
            // inform someone of the received message
            listener.tell(recv, getSelf());

            parseLine(recv);

            // and reply (sender is the SSL handler created above)
            /*
            if(recv.equals("\000\n")){
                log.info("Found end of message!");
                getSender().tell(init.command("\000\n"), getSelf());
            }
            else{
                getSender().tell(init.command(recv.trim() +"\n"), getSelf());
            }
            */
        }
        else if(msg instanceof WakeUpAndDisconnect){
            System.out.println("received WakeUpAndDisconnect XXX");
            ActorRef tcpHandler = ((WakeUpAndDisconnect)msg).getSenderToClose();
                System.out.println("    handler: " + tcpHandler);
            tcpHandler.tell(TcpMessage.close(), tcpHandler);
        }
    }


    @Override
    protected void transition(Frame.FrameParsingState old, Frame.FrameParsingState next) {
        log.debug("Transition state from {} to {}", old, next);
        System.out.println("Transition from: " + old + " to: " + next);

        if(next == Frame.FrameParsingState.FINISHED_PARSING){
            if(currentFrameCommand == Command.CONNECT){
                System.out.println("    CONNECT...");
                getSender().tell(init.command("CONNECTED\nversion:1.2\n\000\n"), getSelf());
                return;
            }
            if(currentFrameCommand == Command.DISCONNECT){
                System.out.println("    DISCONNECT...");
                String receiptHeader = headers.get("receipt");
                if(receiptHeader != null){
                    //  todo add encoding
                   // getSender().tell(init.command("RECEIPT\nreceipt-id:" + receiptHeader + "\n\000\n"), getSelf());

                }

                System.out.println("XXX: scheduling wakeup");
                System.out.println("    handler before schedule: " + getSender());
                getContext().system().scheduler().scheduleOnce(DELAYED_MS_BEFORE_DISCONNECT, getSelf(), new  WakeUpAndDisconnect(getSender()),getContext().dispatcher(), null);
                // getSender().tell(TcpMessage.close(), getSender());
                return;
            }
            else{
                throw new IllegalStateException("Unimplemented when currentFrameCommand = " + currentFrameCommand);
            }
        }

    }

    /**
     * This probably do not work :(
     */
    class WakeUpAndDisconnect{

        public WakeUpAndDisconnect(ActorRef senderToClose){
            this.senderToClose = senderToClose;
        }

        private final ActorRef senderToClose;

        public ActorRef getSenderToClose(){
            return senderToClose;
        }
    }
}