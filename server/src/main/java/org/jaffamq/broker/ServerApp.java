package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 16.08.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class ServerApp {

    public static void main(String... args) throws NoSuchAlgorithmException {

        InetSocketAddress remote = new InetSocketAddress("localhost", 9907);
        ActorSystem system = ActorSystem.create("ServerApp");

        final ActorRef topicDestinationManager = system.actorOf(Props.create(TopicDestinationManager.class), TopicDestinationManager.NAME);
        final ActorRef queueDestinationManager = system.actorOf(Props.create(QueueDestinationManager.class), QueueDestinationManager.NAME);
        final ActorRef server = system.actorOf(Props.create(StompServer.class, remote, topicDestinationManager, queueDestinationManager));

    }

}
