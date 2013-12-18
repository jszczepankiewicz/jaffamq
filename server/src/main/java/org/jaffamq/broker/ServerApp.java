package org.jaffamq.broker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Main class for broker.
 */
public class ServerApp {

    private static BrokerInstance instance;

    public static void main(String... args) {

        Config conf = ConfigFactory.load();

        instance = new BrokerInstance(conf.getString("torpedo.net.host"), conf.getInt("torpedo.net.port"), conf.getString("torpedo.repo.datadir"));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                instance.shutdown();
            }
        });

    }

}
