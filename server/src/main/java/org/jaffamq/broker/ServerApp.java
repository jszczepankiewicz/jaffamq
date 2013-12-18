package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jaffamq.broker.destination.QueueDestinationManager;
import org.jaffamq.broker.destination.TopicDestinationManager;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

/**
 * Main class for broker.
 */
public class ServerApp {

    private static BrokerInstance instance;

    public static void main(String... args){

        Config conf = ConfigFactory.load();

        instance = new BrokerInstance(conf.getString("torpedo.net.host"), conf.getInt("torpedo.net.port"), conf.getString("torpedo.repo.datadir"));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                instance.shutdown();
            }
        });

    }

}
