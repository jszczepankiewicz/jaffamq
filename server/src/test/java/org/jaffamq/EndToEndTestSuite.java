package org.jaffamq;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.jaffamq.broker.StompServer;
import org.jaffamq.broker.destination.QueueDestinationManager;
import org.jaffamq.broker.destination.TopicDestinationManager;
import org.jaffamq.broker.destination.persistence.PollUnconsumedMessageService;
import org.jaffamq.broker.destination.persistence.StoreUnconsumedMessageService;
import org.jaffamq.org.jaffamq.test.StompTestBlockingClient;
import org.jaffamq.org.jaffamq.test.StompTestClient;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.journal.UnconsumedMessageJournalRepository;
import org.junit.Rule;
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
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

/**
 * Created by urwisy on 08.12.13.
 */
public class EndToEndTestSuite {

    private static Logger LOG = LoggerFactory.getLogger(EndToEndTestSuite.class);

    protected ActorSystem system;

    private String unconsumedRepositoryDataDir;

    protected void disconnectAllClients(){

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

    protected void createBroker(String unconsumedRepositoryDataDir) throws InterruptedException {

        this.unconsumedRepositoryDataDir = unconsumedRepositoryDataDir;

        InetSocketAddress remote = new InetSocketAddress("127.0.0.1", 9999);
        system = ActorSystem.create("TestServerApp");

        repo = new UnconsumedMessageJournalRepository(unconsumedRepositoryDataDir);
        repo.init();

        Map<String, List<PersistedMessageId>> persistedMessages = repo.getPersistedMessagesByLocation();

        final ActorRef pollUnconsumedMessageService = system.actorOf(Props.create(PollUnconsumedMessageService.class, repo));
        final ActorRef storeUnconsumedMessageService = system.actorOf(Props.create(StoreUnconsumedMessageService.class, repo));

        final ActorRef topicDestinationManager = system.actorOf(Props.create(TopicDestinationManager.class), TopicDestinationManager.NAME);
        final ActorRef queueDestinationManager = system.actorOf(Props.create(QueueDestinationManager.class, storeUnconsumedMessageService, pollUnconsumedMessageService, persistedMessages), QueueDestinationManager.NAME);

        system.actorOf(Props.create(StompServer.class, remote, topicDestinationManager, queueDestinationManager));
        Thread.sleep(500);
    }

    protected String getUnconsumedRepositoryDataDir(){
        return unconsumedRepositoryDataDir;
    }

    protected void createBroker() throws InterruptedException {
        createBroker(org.jaffamq.test.IOTestHelper.getTempDataDir());
    }

    protected void closeBroker(){
        try {
            //  sleep a little before shutdowning actors to fill all logs
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOG.warn("Unexpected InterrruptedException while waiting");
        }
        JavaTestKit.shutdownActorSystem(system);
        system = null;
        repo.shutdown();
    }

    @Rule
    public ExternalResource clientsResource = new ExternalResource() {

        @Override
        protected void after() {
            disconnectAllClients();
        }
    };

    @Rule
    public ExternalResource serverResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            createBroker();
        }

        @Override
        protected void after() {
            closeBroker();
        }
    };

    private static final Charset UTF8 = Charset.forName("UTF-8");

    protected List<StompTestClient> initializedClients;

    protected UnconsumedMessageJournalRepository repo;

    protected StompTestClient createClient() {
        return createClients(1)[0];
    }

    protected StompTestClient createConnectedClient() throws IOException {
        StompTestClient client = createClient();
        connectClient(client);
        return client;
    }

    protected StompTestClient[] createClients(int numberOfClients) {

        if (initializedClients != null) {
            throw new IllegalStateException("Clients from previous test not disposed properly. Please make sure you have disposed them before initalizing initializedClients for new test.");
        }

        initializedClients = new ArrayList<>(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            initializedClients.add(new StompTestBlockingClient(9999, "127.0.0.1", 3000));
        }

        return initializedClients.toArray(new StompTestClient[]{});
    }

    protected void connectClient(StompTestClient client) throws IOException {
        connectClients(new StompTestClient[]{client});
    }

    protected void connectClients(StompTestClient[] clients) throws IOException {

        for (int i = 0; i < clients.length; i++) {

            //  given
            assertThat(clients[i], is(notNullValue()));

            //  when
            String response = clients[i].connectSendAndGrabAnswer("/CONNECT/basic.txt");

            //  then
            assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
        }

    }

    protected static void waitToPropagateTCP() throws InterruptedException {
        Thread.sleep(1000);
    }

    protected String readResource(String classpathResource) throws IOException {

        StringWriter writer = new StringWriter();
        InputStream is = this.getClass().getResourceAsStream(classpathResource);

        if (is == null) {
            throw new IllegalArgumentException("Classpath resource: " + classpathResource + " not found");
        }

        IOUtils.copy(is, writer, UTF8);
        return writer.getBuffer().toString().trim();
    }

    protected void expectNoResponse(StompTestClient client) throws IOException {

        String response = client.getResponseOrTimeout();

        if (response.length() > 0) {
            fail(String.format("Expected no response from server but got: %s", response));
        }
    }

    protected void expectResponse(StompTestClient client, String expectedFrame) throws IOException {

        String response = client.getResponseOrTimeout();
        assertThat("Response from server", response, is(equalTo(readResource(expectedFrame))));
    }

}
