package org.jaffamq.broker.destination;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.jaffamq.broker.messages.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 28.08.13
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class TopicDestinationManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger(TopicDestinationManagerTest.class);

    private static ActorSystem system;

    @Rule
    public ExternalResource systemResouce = new ExternalResource() {

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
    public void shouldCorrectlyServeTopics(){
        new JavaTestKit(system) {{
            final Props props = Props.create(TopicDestinationManager.class);
            final ActorRef destinationManager = system.actorOf(props);



            StompMessage tz = new StompMessage("destinationz", null, null, "1");

            destinationManager.tell(tz, getRef());
            LOG.debug("Message to destinationz sent");
            //  we were no subscribing, nothing should be retrieved
            expectNoMsg();

            StompMessage ta = new StompMessage("destinationa", null, null, "2");
            StompMessage tb = new StompMessage("destinationb", null, null, "3");

            destinationManager.tell(new SubscriberRegister("destinationa", "1"), getRef());
            LOG.debug("Subscribed to destinationa");
            expectNoMsg();
            destinationManager.tell(new SubscriberRegister("destinationb", "2"), getRef());
            LOG.debug("Subscribed to destinationb");
            expectNoMsg();

            destinationManager.tell(ta, getRef());
            LOG.debug("Message to destinationa sent");
            SubscribedStompMessage expected1 = new SubscribedStompMessage(ta, "1");
            expectMsgEquals(expected1);

            destinationManager.tell(tb, getRef());
            LOG.debug("Message to destinationb sent");
            SubscribedStompMessage expected2 = new SubscribedStompMessage(tb, "2");
            expectMsgEquals(expected2);

            //  TODO: add more subscribers, test lifecycle
            destinationManager.tell(new Unsubscribe("destinationb", "2"), getRef());
            LOG.debug("Unsubscribed from destinationb");
            UnsubscriptionConfirmed expectedC = new UnsubscriptionConfirmed("2", "destinationb");
            expectMsgEquals(expectedC);

            destinationManager.tell(ta, getRef());
            LOG.debug("Message to destinationa sent");
            SubscribedStompMessage expected3 = new SubscribedStompMessage(ta, "1");
            expectMsgEquals(expected3);

            destinationManager.tell(tb, getRef());
            LOG.debug("Message to destinationb sent");
            expectNoMsg();



        }};
    }


}
