package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.testkit.JavaTestKit;
import org.jaffamq.broker.messages.SubscribedStompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.messages.StompMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for Topic
 */
public class TopicTest {

    private static final Logger LOG = LoggerFactory.getLogger(TopicTest.class);

    private static ActorSystem system;

    @Rule
    public ExternalResource systemResource = new ExternalResource() {

        @Override
        protected void before() {
            system = ActorSystem.create();
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };


    /*
        TODO:   add multiple subscribers
     */
    @Test
    public void shouldCorrectlyCommunicateWithTopic() {

        new JavaTestKit(system) {{

            //  given
            final Props props = Props.create(Topic.class, "destination1");
            final ActorRef topic = system.actorOf(props);

            LOG.debug("Sending unsubscribed message");
            StompMessage tz = new StompMessage("destinationz", null, null, "3");
            expectNoMsg();

            //  warning: this only registers one subscriber
            LOG.debug("Subscribing to topic");
            topic.tell(new SubscriberRegister("destination1", "1"), getRef());

            StompMessage t1 = new StompMessage("destination1", null, null, "3");

            LOG.debug("Sending message to subscribed topic");
            topic.tell(t1, getRef());
            SubscribedStompMessage expected = new SubscribedStompMessage(t1, "1");
            expectMsgEquals(expected);

            topic.tell(new Terminated(null, false, false), getRef());
            expectNoMsg();
        }};
    }
}
