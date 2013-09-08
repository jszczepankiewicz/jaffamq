
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.jaffamq.broker.DestinationManager;
import org.jaffamq.broker.ServerListener;
import org.jaffamq.broker.StompServer;
import org.jaffamq.org.jaffamq.test.StompTestBlockingClient;
import org.jaffamq.org.jaffamq.test.StompTestClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

/**
 * Group of tests that are using full TCP/IP stack to communicate between client (test) and server. Client is simple blocking test sending class.
 */
public class BlackBoxServerTest {

    private static Logger LOG = LoggerFactory.getLogger(BlackBoxServerTest.class);

    private ActorSystem system;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private List<StompTestClient> initializedClients;

    @Rule
    public ExternalResource clientsResource = new ExternalResource() {

        @Override
        protected void after() {

            if (initializedClients != null) {
                for (int i = 0; i < initializedClients.size(); i++) {

                    StompTestClient testClient = initializedClients.get(i);

                    try {
                        testClient.close();
                    } catch (IOException e) {
                        LOG.warn("Exception while disposing StompTestClient", e);
                    }
                }
            }

            initializedClients = null;
        }
    };

    /**
     * Creates exactly one client.
     *
     * @return
     */
    private StompTestClient createClient() {
        return createClients(1)[0];
    }

    private StompTestClient[] createClients(int numberOfClients) {

        if (initializedClients != null) {
            throw new IllegalStateException("Clients from previous test not disposed properly. Please make sure you have disposed them before initalizing initializedClients for new test.");
        }

        initializedClients = new ArrayList<>(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            initializedClients.add(new StompTestBlockingClient(9999, "localhost", 3000));
        }

        return initializedClients.toArray(new StompTestClient[]{});
    }

    @Rule
    public ExternalResource serverResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {

            InetSocketAddress remote = new InetSocketAddress("localhost", 9999);
            system = ActorSystem.create("TestServerApp");
            final ActorRef listener = system.actorOf(Props.create(ServerListener.class), "serverlistener");
            final ActorRef destinationManager = system.actorOf(Props.create(DestinationManager.class), DestinationManager.NAME);
            final ActorRef server = system.actorOf(Props.create(StompServer.class, remote, listener, destinationManager));



        }

        @Override
        protected void after() {
            try {
                //  sleep a little before shutdowning actors to fill all logs
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //  do nothing
            }
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    private String readResource(String classpathResource) throws IOException {

        StringWriter writer = new StringWriter();
        InputStream is = this.getClass().getResourceAsStream(classpathResource);

        if (is == null) {
            throw new IllegalArgumentException("Classpath resource: " + classpathResource + " not found");
        }

        IOUtils.copy(is, writer, UTF8);
        return writer.getBuffer().toString().trim();
    }

    private void expectNoResponse(StompTestClient client) throws IOException {

        String response = client.getResponseOrTimeout();

        if (response.length() > 0) {
            fail(String.format("Expected no response from server but got: %s", response));
        }
    }

    private void expectResponse(StompTestClient client, String expectedFrame) throws IOException {

        String response = client.getResponseOrTimeout();
        assertThat("Response from server", response, is(equalTo(readResource(expectedFrame))));
    }

    private void connectClient(StompTestClient client) throws IOException {
        connectClients(new StompTestClient[]{client});
    }

    private void connectClients(StompTestClient[] clients) throws IOException {

        for (int i = 0; i < clients.length; i++) {

            //  given
            assertThat(clients[i], is(notNullValue()));

            //  when
            String response = clients[i].connectSendAndGrabAnswer("/CONNECT/basic.txt");

            //  then
            assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
        }

    }

    @Test
    public void shouldChangeStateToConnectedAfterSuccessfulConnect() throws Exception {

        //  given
        StompTestClient client = createClient();

        //  when
        String response = client.connectSendAndGrabAnswer("/CONNECT/basic.txt");

        //  then
        assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
    }

    @Test
    public void shouldAcceptCorrectSendFrame() throws Exception {

        //  given
        StompTestClient client = createClient();
        connectClient(client);

        //  when
        client.sendFrame("/SEND/send_destination_topic.txt");
        expectNoResponse(client);
    }

    @Test
    public void shouldAcceptCorrectSubscribeFrame() throws Exception {

        //  given
        StompTestClient client = createClient();
        connectClient(client);

        //   when
        client.sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
    }

    @Test
    public void shouldSubscribeToTopicWithSelectedClients() throws Exception {

        //  given
        StompTestClient[] clients = createClients(5);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/SUBSCRIBE/subscribe_topic_x.txt");       //  subscribe to some topic that will not have senders
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");     //  subscribe to /topic/foo
        clients[2].sendFrame("/SUBSCRIBE/subscribe_topic_id_100.txt");   //  subscribe to /topic/foo

        /*
         * Unfortunatelly we need to wait a little to make sure that all buffers are flushed to server and interpreted by it.
         */
        Thread.sleep(1000);

        //  clients[3] does not subscribe to anything
        clients[4].sendFrame("/SEND/send_destination_topic.txt");

        //  then

        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectResponse(clients[2], "/MESSAGE/message_topic_subscription_100_response.txt");
        expectNoResponse(clients[3]);
        expectNoResponse(clients[4]);
    }


}
