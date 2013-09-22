package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.io.TcpPipelineHandler;
import akka.util.ByteString;
import org.jaffamq.*;
import org.jaffamq.broker.messages.*;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Actor that represents conversation state
 * between client and server. It has mutable state.
 */
public class ClientSessionHandler extends ParserFrameState {

    public static final int MILISECONDS_BEFORE_CLOSE = 100;

    private long nextMessageIdPart;

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    private final ActorRef topicDestinationManager;

    private final ActorRef queueDestinationManager;

    private final ActorRef connection;

    /*
        Unfortunatelly the unsubscribe client frame contains only subscription id (unique among client session), but no destination.
        In order to correctly unsubscribe we need also destinationId. Best place to store the mapping between destination and subscriptionId (per client)
        is the ClientSessionHandler. Question is where should we un
     */
    private Map<String, String> destinationBySubscriptionId = new HashMap<>();

    // this will hold the pipeline handler’s context
    private final TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init;

    public ClientSessionHandler(ActorRef connection, ActorRef topicDestinationManager, ActorRef queueDestinationManager, TcpPipelineHandler.Init<TcpPipelineHandler.WithinActorContext, String, String> init) {
        this.topicDestinationManager = topicDestinationManager;
        this.queueDestinationManager = queueDestinationManager;
        this.init = init;
        this.connection = connection;
    }

    @Override
    public void onReceive(Object msg) throws Exception {

        //  uncomment below to grab low level tcp events
        //log.info("ClientSessionHandler.onReceive: {}", msg);

        if (msg instanceof Tcp.CommandFailed) {
            getContext().stop(getSelf());

        } else if (msg instanceof SubscribedStompMessage) {
            SubscribedStompMessage m = (SubscribedStompMessage) msg;
            log.info("Received SubscribedStompMessage with set-message-id: {}", m.getHeaders().get(Headers.SET_MESSAGE_ID));
            onSubscribedMessage(m);

        } else if (msg instanceof UnsubscriptionConfirmed) {

            UnsubscriptionConfirmed u = (UnsubscriptionConfirmed) msg;
            log.info("Received UnsubsciptionConfirmed with destination: {} and subscriptionId: {}", u.getDestination(), u.getSubscriptionId());
            destinationBySubscriptionId.remove(u.getSubscriptionId());

        } else if (msg instanceof TcpPipelineHandler.Init.Event) {

            // unwrap TcpPipelineHandler’s event to get a Tcp.Event
            final String recv = init.event(msg);

            // inform someone of the received message
            //listener.tell(recv, getSelf());

            parseLine(recv);
        } else {
            log.warning("unhandled message: {}", msg);
            unhandled(msg);
        }

    }

    private void handleUnimplementedFrame() {
        log.error("ERROR: unimplemented client frame command: {}", currentFrameCommand);
        getSender().tell(init.command(String.format("ERROR\nmessage:unimplemented client command %s\n\000\n", currentFrameCommand)), getSelf());

        scheduleClosingConnection();
    }

    private void handleConnectFrame() throws RequestValidationFailedException {

        getRequiredHeaderValue(Headers.ACCEPT_VERSION, Errors.HEADERS_MISSING_ACCEPT_VERSION);
        getRequiredHeaderValue(Headers.HOST, Errors.HEADERS_MISSING_HOST);

        final ByteString response = ByteString.fromString("CONNECTED\nversion:1.2\n\000\n");
        connection.tell(TcpMessage.write(response), getSelf());
    }

    private void onSubscribedMessage(SubscribedStompMessage msg) {
        log.warning("onSubscribedMessage");
        connection.tell(TcpMessage.write(ByteString.fromString(msg.toTransmit())), getSender());
    }

    private void handleDisconnectFrame() {

        String receiptHeader = headers.get("receipt");

        if (receiptHeader != null) {
            //  todo add encoding
            getSender().tell(init.command("RECEIPT\nreceipt-id:" + Frame.encodeHeaderValue(receiptHeader) + "\n\000\n"), getSelf());
        }

        scheduleClosingConnection();
    }

    private void handleUnsubscribeFrame() throws RequestValidationFailedException {

        String subscriptionId = getRequiredHeaderValue(Headers.SUBSCRIPTION_ID, Errors.HEADERS_MISSING_SUBSCRIPTION_ID);

        //  need to find out the destination by subscriptionId
        String destination = destinationBySubscriptionId.get(subscriptionId);

        if (destination == null) {
            log.error("Can not found destination for subscriptionId: {}, unsubscription can not proceed!", subscriptionId);
        } else {

            getDestinationManager(destination).tell(new Unsubscribe(destination, subscriptionId), getSelf());
        }

    }

    private void handleSubscribeFrame() throws RequestValidationFailedException {

        String destination = getRequiredHeaderValue(Headers.DESTINATION, Errors.HEADERS_MISSING_DESTINATION);
        String subscriptionId = getRequiredHeaderValue(Headers.SUBSCRIPTION_ID, Errors.HEADERS_MISSING_SUBSCRIPTION_ID);

        //  we need to store destination by subscriptionId to able to quickly serve unsubscribe with only subscriptionId
        destinationBySubscriptionId.put(subscriptionId, destination);
        log.info("Received SUBSCRIBE to destination: {} with id: {}", destination, subscriptionId);

        //  passing self as the subscriber

        getDestinationManager(destination).tell(new SubscriberRegister(destination, subscriptionId), getSelf());
    }

    private String getNextMessageId() {
        /*  Is this unique ? */
        return getSelf().path().name() + "_" + System.currentTimeMillis() + "_" + nextMessageIdPart++;
    }

    private void scheduleClosingConnection() {
        /*
            TODO: we should probably change the state of the session to something WAIRING_FOR_CLOSE.
            so no other frames are consumed from client.
         */
        getContext().system().scheduler().scheduleOnce(Duration.create(MILISECONDS_BEFORE_CLOSE, TimeUnit.MILLISECONDS),
                connection, TcpMessage.close(), getContext().system().dispatcher(), null);

    }

    private void handleErrorFrame(RequestValidationFailedException ex) {

        Errors.Code e = ex.getErrorCode();
        ByteString response = ByteString.fromString(String.format("ERROR\nmessage:%s %s\n\n%s\n\000\n", e.getId(), e.getDescription(), e.getCause()));
        connection.tell(TcpMessage.write(response), getSender());

        scheduleClosingConnection();
    }

    private void handleSendFrame() throws RequestValidationFailedException {

        String destination = getRequiredHeaderValue(Headers.DESTINATION, Errors.HEADERS_MISSING_DESTINATION);

        String messageId = headers.get(Headers.SET_MESSAGE_ID);

        if (messageId == null) {
            messageId = getNextMessageId();
        }

        getDestinationManager(destination).tell(new StompMessage(destination, getCurrentFrameBody(), headers, messageId), getSender());
    }

    private void reactToCommandParsed() {
        log.debug("Finished parsing client frame: {}", currentFrameCommand);
        try {
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
        } catch (RequestValidationFailedException ex) {
            log.warning("Validation exception: {} for client: {}", ex.getErrorCode());
            handleErrorFrame(ex);
        }
    }

    @Override
    protected void transition(Frame.FrameParsingState old, Frame.FrameParsingState next) {
        log.debug("Transition state from {} to {}", old, next);

        if (next == Frame.FrameParsingState.FINISHED_PARSING) {
            reactToCommandParsed();
        }

    }

    private ActorRef getDestinationManager(String destination) throws RequestValidationFailedException{

        //  checking if there is at least one character in destination name
        if(destination.length() == 7){
            throw new RequestValidationFailedException(Errors.INVALID_DESTINATION_NAME);
        }

        if(destination.startsWith("/topic/")){
            return topicDestinationManager;
        }

        if(destination.startsWith("/queue/")){
            return queueDestinationManager;
        }

        throw new RequestValidationFailedException(Errors.UNSUPPORTED_DESTINATION_TYPE);
    }

}
