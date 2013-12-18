package org.jaffamq.broker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.jaffamq.broker.destination.QueueDestinationManager;
import org.jaffamq.broker.destination.TopicDestinationManager;
import org.jaffamq.broker.destination.persistence.PollUnconsumedMessageService;
import org.jaffamq.broker.destination.persistence.StoreUnconsumedMessageService;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.UnconsumedMessageRepository;
import org.jaffamq.persistence.journal.UnconsumedMessageJournalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Broker instance creation / destruction encapsulation. Created to share common source between end-to-end tests and real instance.
 *
 */
public class BrokerInstance {

    private static Logger LOG = LoggerFactory.getLogger(BrokerInstance.class);

    private ActorSystem system;

    private UnconsumedMessageRepository repo;

    /**
     * Destroy system (cleanly) and then close the unconsumed message repository.
     */
    public void shutdown(){

        LOG.info("Proceeding with broker instance shutdown...");

        long nanoStart = System.nanoTime();
        system.shutdown();
        repo.shutdown();

        long durationNano = System.nanoTime() - nanoStart;
        LOG.info("Broker instance shutdown complete in {} ms", TimeUnit.MILLISECONDS.convert(durationNano, TimeUnit.NANOSECONDS));
    }

    /**
     * Create broker isntance and start it
     * @param host on which listen to
     * @param port on which listen to
     * @param unconsumedRepositoryDataDir directory name with journal data location. Data will be created if not found.
     */
    public BrokerInstance(String host, int port, String unconsumedRepositoryDataDir){

        LOG.info("Broker instance start initialized...");
        long nanoStart = System.nanoTime();

        InetSocketAddress remote = new InetSocketAddress(host, port);
        system = ActorSystem.create("TestServerApp");

        repo = new UnconsumedMessageJournalRepository(unconsumedRepositoryDataDir);
        repo.init();

        Map<String, Queue<PersistedMessageId>> persistedMessages = repo.getPersistedMessagesByLocation();

        final ActorRef pollUnconsumedMessageService = system.actorOf(Props.create(PollUnconsumedMessageService.class, repo));
        final ActorRef storeUnconsumedMessageService = system.actorOf(Props.create(StoreUnconsumedMessageService.class, repo));

        final ActorRef topicDestinationManager = system.actorOf(Props.create(TopicDestinationManager.class), TopicDestinationManager.NAME);
        final ActorRef queueDestinationManager = system.actorOf(Props.create(QueueDestinationManager.class, storeUnconsumedMessageService, pollUnconsumedMessageService, persistedMessages), QueueDestinationManager.NAME);

        system.actorOf(Props.create(StompServer.class, remote, topicDestinationManager, queueDestinationManager));
        long durationNano = System.nanoTime() - nanoStart;
        LOG.info("Broker instance started in {} ms", TimeUnit.MILLISECONDS.convert(durationNano, TimeUnit.NANOSECONDS));
    }

    public ActorSystem getSystem(){
        return system;
    }

}
