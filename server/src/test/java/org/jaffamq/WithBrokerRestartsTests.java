package org.jaffamq;

import org.jaffamq.org.jaffamq.test.StompTestClient;
import org.junit.Test;

/**
 * Integration tests which engage restart of the broker.
 */
public class WithBrokerRestartsTests extends EndToEndTestSuite {

    @Test
    public void shouldRestoreUnconsumedQueueMessagesFromPreviousServerRun() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");

        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");

        //  then
        expectNoResponse(clients[1]);
        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");
        expectNoResponse(clients[0]);

        waitToPropagateTCP();
        String dataDir = getUnconsumedRepositoryDataDir();

        //  shutdowning broker
        closeBroker();

        //  shutdowning the clients
        disconnectAllClients();

        //  creating new broker with previous data storage location
        createBroker(dataDir);

        //  creating clients and connecting with subscriber

        //  given
        StompTestClient[] clientsForNewBroker = createClients(2);
        connectClients(clientsForNewBroker);

        //  then
        clientsForNewBroker[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");     //  subscribe to /topic/foo
        expectResponse(clientsForNewBroker[1], "/MESSAGE/message_queue_subscription_3_m1.txt");
        expectNoResponse(clientsForNewBroker[0]);
    }


}
