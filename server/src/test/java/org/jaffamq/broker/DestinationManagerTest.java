package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.messages.SubscriberRegister;
import org.jaffamq.broker.messages.Unsubscribe;
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
public class DestinationManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DestinationManagerTest.class);

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
            final Props props = Props.create(DestinationManager.class);
            final ActorRef destinationManager = system.actorOf(props);



            StompMessage tz = new StompMessage("destinationz", null, null);

            destinationManager.tell(tz, getRef());
            LOG.debug("Message to destinationz sent");
            //  we were no subscribing, nothing should be retrieved
            expectNoMsg();

            StompMessage ta = new StompMessage("destinationa", null, null);
            StompMessage tb = new StompMessage("destinationb", null, null);

            destinationManager.tell(new SubscriberRegister("destinationa"), getRef());
            LOG.debug("Subscribed to destinationa");
            expectNoMsg();
            destinationManager.tell(new SubscriberRegister("destinationb"), getRef());
            LOG.debug("Subscribed to destinationb");
            expectNoMsg();

            destinationManager.tell(ta, getRef());
            LOG.debug("Message to destinationa sent");
            expectMsgEquals(ta);

            destinationManager.tell(tb, getRef());
            LOG.debug("Message to destinationb sent");
            expectMsgEquals(tb);

            //  TODO: add more subscribers, test lifecycle
            destinationManager.tell(new Unsubscribe("destinationb"), getRef());
            LOG.debug("Unsubscribed from destinationb");
            expectNoMsg();

            destinationManager.tell(ta, getRef());
            LOG.debug("Message to destinationa sent");
            expectMsgEquals(ta);

            destinationManager.tell(tb, getRef());
            LOG.debug("Message to destinationb sent");
            expectNoMsg();



        }};
    }


}
