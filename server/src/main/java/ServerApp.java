import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.jaffamq.broker.DestinationManager;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 16.08.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class ServerApp{

    public static void main(String...args) throws NoSuchAlgorithmException {
        //SSLContext ctx = SSLContext.getDefault();
        InetSocketAddress remote = new InetSocketAddress("localhost", 9907);
        ActorSystem system = ActorSystem.create("ServerApp");
        final ActorRef listener = system.actorOf(Props.create(ServerListener.class), "serverlistener");
        final ActorRef destinationManager = system.actorOf(Props.create(DestinationManager.class), DestinationManager.NAME);
        final ActorRef server = system.actorOf(Props.create(StompServer.class, remote, listener, destinationManager));

    }

}
