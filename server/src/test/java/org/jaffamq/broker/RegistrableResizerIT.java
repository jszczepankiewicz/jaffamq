package org.jaffamq.broker;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.testkit.JavaTestKit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrableResizerIT {

    private static final Logger LOG = LoggerFactory.getLogger(TopicDestinationManagerTest.class);

    private static ActorSystem system;

    public static class EchoActor extends UntypedActor{

        private String id;

        public EchoActor(String id){
            this.id = id;
        }

        @Override
        public void onReceive(Object o) throws Exception {
            if(o instanceof String){
                String response = (String)o + " from " + id;
                getSender().tell(response, getSelf());
            }
            else{
                unhandled(o);
            }
        }
    }

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
    public void shouldInitializeRouterWithResizerCorrectly(){
        new JavaTestKit(system) {{

            //  given
            RegistrableResizer resizer = new RegistrableResizer();

            //  when
            ActorRef router2 = system.actorOf(
                    Props.empty().withRouter(new RoundRobinRouter(resizer)));


        }};
    }

    @Test
    public void shouldSendMessagesToNewRoutees(){
        new JavaTestKit(system) {{

            //  given
            ActorRef actor1 = system.actorOf(Props.create(EchoActor.class, "actor1"));
            ActorRef actor2 = system.actorOf(Props.create(EchoActor.class, "actor2"));
            ActorRef actor3 = system.actorOf(Props.create(EchoActor.class, "actor3"));

            RegistrableResizer resizer = new RegistrableResizer();

            //  when
            ActorRef router2 = system.actorOf(
                    Props.empty().withRouter(new RoundRobinRouter(resizer)));

            resizer.registerRoutee(actor1);
            resizer.registerRoutee(actor2);
            router2.tell("message1", getRef());
            expectMsgEquals("message1 from actor1");
            expectNoMsg();

            router2.tell("message2", getRef());
            expectMsgEquals("message2 from actor2");
            expectNoMsg();


        }};
    }
}
