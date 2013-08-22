
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.jaffamq.TCPTestClient;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 18.08.13
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class StompServerProtocolFrameTest {

    private static final int TEST_TIMEOUT_MS=900;

    private static Logger LOG = LoggerFactory.getLogger(StompServerProtocolFrameTest.class);

    private  TCPTestClient testClient;
    private ActorSystem system;
    private static final Charset ENC=Charset.forName("UTF-8");

    @Rule
    public ExternalResource clientResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
            LOG.debug("clientResource.before()");
            testClient = new TCPTestClient(9999, "localhost", 3000);
        }

        @Override
        protected void after(){
            LOG.debug("clientResource.after()");
            try {
                testClient.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            testClient = null;
        }
    };

    @Rule
    public ExternalResource serverResource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {

           // testClient = new TCPTestClient(9999, "localhost", 3000);
            InetSocketAddress remote = new InetSocketAddress("localhost", 9999);
            system = ActorSystem.create("TestServerApp");
            final ActorRef listener = system.actorOf(Props.create(ServerListener.class), "serverlistener");
            final ActorRef server = system.actorOf(Props.create(StompServer.class, remote, listener));
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };

    private String readResource(String classpathResource) throws IOException {

        StringWriter writer = new StringWriter();
        InputStream is = this.getClass().getResourceAsStream(classpathResource);

        if(is == null){
            throw new IllegalArgumentException("Classpath resource: " + classpathResource + " not found");
        }

        IOUtils.copy(is, writer, ENC);
        return writer.getBuffer().toString().trim();
    }

    private void expectResponseForRequest(String requestResource, String expectedResponseResource) throws IOException{

        //  when
        String response = testClient.sendFrameAndWaitForResponseFrame(requestResource);

        //  then
        assertThat(response, is(equalTo(readResource(expectedResponseResource))));
    }

    /**
     * Goes to CONNECTED state.
     * @throws Exception
     */
    private void doConnectedClient() throws Exception{
        //  given
        assertThat(testClient, is(notNullValue()));

        //  when
        String response = testClient.connectSendAndGrabAnswer("/CONNECT/basic.txt");


        //  then
        assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
    }

    @Test(timeout = TEST_TIMEOUT_MS)
    public void shouldChangeStateToConnectedAfterSuccessfulConnect() throws Exception {

        //  given
        assertThat(testClient, is(notNullValue()));

        //  when
        String response = testClient.connectSendAndGrabAnswer("/CONNECT/basic.txt");

        //  then
        assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
    }

    @Test(timeout = TEST_TIMEOUT_MS)
    public void shouldAcceptCorrectSendFrame() throws Exception{

        //  given
        doConnectedClient();

        //  when
        testClient.sendFrameAndWaitForResponseFrame("/SEND/send_destination_topic.txt");
    }
}
