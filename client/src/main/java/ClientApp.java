import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 16.08.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientApp{


    public static void main(String...arg) throws NoSuchAlgorithmException {
        InetSocketAddress remote = new InetSocketAddress("localhost", 9907);
        //SSLContext ctx = SSLContext.getDefault();
        ActorSystem system = ActorSystem.create("ClientApp");
        final ActorRef listener = system.actorOf(Props.create(ClientListener.class), "clientlistener");
        final ActorRef client = system.actorOf(Props.create(AkkaTCPClient.class, remote,  listener));

    }


    //final SSLContext ctx = SslTlsSupportSpec.createSslContext("/keystore", "/truststore", "changeme");
}
