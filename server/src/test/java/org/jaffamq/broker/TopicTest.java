package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.testkit.JavaTestKit;
import org.jaffamq.TCPTestClient;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import java.io.IOException;

/**
 * Integration test for Topic
 */
public class TopicTest {

    private static final Logger LOG = LoggerFactory.getLogger(Topic.class);

    private static ActorSystem system;

    @Rule
    public ExternalResource systemResouce = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            system = ActorSystem.create();
        }

        @Override
        protected void after(){
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    @Test
    public void shouldCorrectlyCommunicateWithTopic(){

        new JavaTestKit(system) {{

            //  given
            final Props props = Props.create(Topic.class);
            final ActorRef topic = system.actorOf(props);

            final JavaTestKit probe = new JavaTestKit(system);

            LOG.debug("Sending unsubscribed message");
            StompMessage tz = new StompMessage("destinationz", null, null);
            expectNoMsg();


            //  warning: this only registers one subscriber
            LOG.debug("Subscribing to topic");
            topic.tell(new SubscriberRegister(), getRef());

            StompMessage t1 = new StompMessage("destination1", null, null);

            LOG.debug("Sending message to subscribed topic");
            topic.tell(t1, getRef());
            expectMsgEquals(t1);

            topic.tell(new Terminated(null, false, false), getRef());
            expectNoMsg();
        }};
    }
}
