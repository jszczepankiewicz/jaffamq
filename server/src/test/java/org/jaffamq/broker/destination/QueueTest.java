package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.testkit.JavaTestKit;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for Queue.
 */
public class QueueTest {

    private static final Logger LOG = LoggerFactory.getLogger(TopicTest.class);

    private static ActorSystem system;

    @Rule
    public ExternalResource systemResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            system = ActorSystem.create();
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    @Test
    public void shouldCorrectlyCommunicateWithQueueWithOneSubscriber() {

        new JavaTestKit(system) {{

            //  given
            final Props props = Props.create(Queue.class, "destination1");
            final ActorRef queue = system.actorOf(props);

            //final JavaTestKit probe = new JavaTestKit(system);

            LOG.debug("Sending unsubscribed message");
            StompMessage tz = new StompMessage("destinationz", null, null, "3");
            expectNoMsg();

            //  warning: this only registers one subscriber
            LOG.debug("Subscribing to topic");
            queue.tell(new SubscriberRegister("destination1", "1"), getRef());

            StompMessage t1 = new StompMessage("destination1", null, null, "3");

            LOG.debug("Sending message to subscribed topic");
            queue.tell(t1, getRef());
            SubscribedStompMessage expected = new SubscribedStompMessage(t1, "1");
            expectMsgEquals(expected);

            queue.tell(new Terminated(null, false, false), getRef());
            expectNoMsg();
        }};
    }

}
