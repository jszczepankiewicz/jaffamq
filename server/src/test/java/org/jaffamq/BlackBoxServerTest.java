package org.jaffamq;

import org.jaffamq.org.jaffamq.test.StompTestClient;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

/**
 * Group of tests that are using full TCP/IP stack to communicate between client (test) and server.
 * Client is simple blocking test sending class.
 */
public class BlackBoxServerTest extends EndToEndTestSuite {

    private static Logger LOG = LoggerFactory.getLogger(BlackBoxServerTest.class);

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
        waitToPropagateTCP();

        //  clients[3] does not subscribe to anything
        clients[4].sendFrame("/SEND/send_destination_topic.txt");

        //  then

        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectResponse(clients[2], "/MESSAGE/message_topic_subscription_100_response.txt");
        expectNoResponse(clients[3]);
        expectNoResponse(clients[4]);
    }

    @Test
    public void shouldRespondToConnectWithErrorForMissingHost() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/CONNECT/invalid_frame_connect_headers_missing_host.txt");
        expectResponse(client, "/ERROR/error_headers_missing_host.txt");
    }

    @Test
    public void shouldRespondToConnectWithErrorForMissingAcceptVersion() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/CONNECT/invalid_frame_connect_headers_missing_accept_version.txt");
        expectResponse(client, "/ERROR/error_headers_missing_accept_version.txt");
    }

    @Test
    public void shouldRespondToSendToTopicWithErrorForMissingDestination() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_headers_missing_destination.txt");
        expectResponse(client, "/ERROR/error_headers_missing_destination.txt");

    }

    @Test
    public void shouldRespondToSubscribeToTopicWithErrorForMissingId() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SUBSCRIBE/invalid_frame_subscribe_headers_missing_id.txt");
        expectResponse(client, "/ERROR/error_headers_missing_id.txt");

    }

    @Test
    public void shouldRespondToUnknownDestinationTypeWithError() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_unsupported_destination_type.txt");
        expectResponse(client, "/ERROR/error_unsupported_destination_type.txt");
    }

    @Test
    public void shouldRespondToInvalidQueueDestinationNameWithError() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_invalid_queue_destination_name.txt");
        expectResponse(client, "/ERROR/error_invalid_destination_name.txt");
    }

    @Test
    public void shouldRespondToBeginWithErrorForMissingTransaction() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/BEGIN/invalid_frame_begin_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");
    }

    @Test
    public void shouldRespondToCommitWithErrorForMissingTransaction() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/COMMIT/invalid_frame_commit_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");

    }

    @Test
    public void shouldRespondToAbortWithErrorForMissingTransaction() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/ABORT/invalid_frame_abort_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");

    }

    @Test
    public void shouldRespondToInvalidTopicDestinationNameWithError() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_invalid_topic_destination_name.txt");
        expectResponse(client, "/ERROR/error_invalid_destination_name.txt");
    }

    @Test
    public void shouldRespondToSubscribeToTopicWithErrorForMissingDestination() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SUBSCRIBE/invalid_frame_subscribe_headers_missing_destination.txt");
        expectResponse(client, "/ERROR/error_headers_missing_destination.txt");

    }

    @Test
    public void shouldRespondToUnsubscribeToTopicWithErrorForMissingId() throws Exception {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/UNSUBSCRIBE/invalid_frame_unsubscribe_headers_missing_id.txt");
        expectResponse(client, "/ERROR/error_headers_missing_id.txt");

    }

    //  is using persistence
    @Test
    public void shouldKeepMessagesFromQueueForNextSubscriberIfSentWithoutActiveSubscribers() throws Exception {

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
        /*
            Testing if message will be stored in persisted storage for next subscriber
         */
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");     //  subscribe to /topic/foo
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m1.txt");
        expectNoResponse(clients[0]);
    }

    @Test
    public void shouldRecycleQueueSubscribersOnUnsubscribe() throws Exception {

        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");
        waitToPropagateTCP();
        clients[2].sendFrame("/SUBSCRIBE/subscribe_queue_id_6.txt");

        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m1.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m1.txt");
        expectNoResponse(clients[2]);

        //  unsubscribe
        clients[2].sendFrame("/UNSUBSCRIBE/unsubscribe_id_6.txt");
        waitToPropagateTCP();

        //  when
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m2.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m2.txt");
        expectNoResponse(clients[2]);   //  clients[2] unsubscribed from this id

    }


    @Test
    public void shouldRecycleQueueSubscribersOnMoreMessages() throws Exception {

        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");
        waitToPropagateTCP();
        clients[2].sendFrame("/SUBSCRIBE/subscribe_queue_id_6.txt");

        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m1.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m1.txt");
        expectNoResponse(clients[2]);

        //  when
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m2.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[2], "/MESSAGE/message_queue_subscription_6_m2.txt");
        expectNoResponse(clients[1]);

        //  when
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m3.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m3.txt");
        expectNoResponse(clients[2]);

        //  when
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m4.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[2], "/MESSAGE/message_queue_subscription_6_m4.txt");
        expectNoResponse(clients[1]);

        //  when
        clients[0].sendFrame("/SEND/send_destination_queue_foo_m5.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m5.txt");
        expectNoResponse(clients[2]);

    }

    @Test
    public void shouldReturnErrorOnBeginTwiceSameTx() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        waitToPropagateTCP();

        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        expectResponse(clients[0], "/ERROR/error_tx_already_begun.txt");


    }

    @Test
    public void shouldReturnErrorOnAbortNotKnownTx() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/ABORT/abort_tx_y.txt");
        expectResponse(clients[0], "/ERROR/error_unknown_tx_to_abort.txt");
    }

    @Test
    public void shouldReturnErrorOnCommitNotKnownTx() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/COMMIT/commit_tx_y.txt");
        expectResponse(clients[0], "/ERROR/error_unknown_tx_to_commit.txt");
    }

    @Test
    public void shouldReturnErrorOnCommitTwiceSameTx() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");
        expectNoResponse(clients[0]);
        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");
        expectResponse(clients[0], "/ERROR/error_tx_already_commited.txt");
    }

    @Test
    public void shouldReturnErrorOnAbortTwiceSameTx() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        expectNoResponse(clients[0]);
        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        expectResponse(clients[0], "/ERROR/error_tx_already_aborted.txt");
    }

    @Test
    public void shouldSendCommitedMessagesFromOneTransactionToMultipleDestinations() throws Exception {

        // given
        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
        clients[2].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");
        expectNoResponse(clients[1]);
        expectNoResponse(clients[2]);

        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");
        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectResponse(clients[2], "/MESSAGE/message_queue_subscription_3_m1.txt");

    }

    @Test
    public void shouldNotSendRollbackedMessagesToTopic() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        expectNoResponse(clients[1]);

    }

    @Test
    public void shouldNotSendRollbackedMessagesToQueue() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");     //  subscribe to /topic/foo
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        expectNoResponse(clients[1]);
    }

    @Test
    public void shouldNotSendRollbackedMessagesFromOneTransactionToMulitpleDestinations() throws Exception {

        // given
        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
        clients[2].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");
        expectNoResponse(clients[1]);
        expectNoResponse(clients[2]);

        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        expectNoResponse(clients[1]);
        expectNoResponse(clients[2]);
    }

    @Test
    public void shouldSendOnlyMessagesFromCommitedTxWithSecondTxFromSameClientUncommited() throws Exception {

        // given
        StompTestClient[] clients = createClients(4);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[0].sendFrame("/BEGIN/begin_tx_b.txt");

        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");    //  subscription id: 0, /topic/foo
        clients[2].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");    //  subscription id: 3, /queue/foo
        clients[3].sendFrame("/SUBSCRIBE/subscribe_queue_id_6.txt");    //  subscription id: 6, /queue/foo

        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");  //  /topic/foo
        expectNoResponse(clients[0]);
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");  //  /queue/foo
        expectNoResponse(clients[0]);
        clients[0].sendFrame("/SEND/send_destination_queue_tx_b.txt");  //  /queue/foo
        expectNoResponse(clients[0]);

        expectNoResponse(clients[1]);
        expectNoResponse(clients[2]);
        expectNoResponse(clients[3]);

        clients[0].sendFrame("/ABORT/abort_tx_a.txt");
        clients[0].sendFrame("/COMMIT/commit_tx_b.txt");
        expectNoResponse(clients[1]);

        String client1response = clients[2].getResponseOrTimeout();
        String client2response = clients[3].getResponseOrTimeout();
        String possibleAnswer1 = readResource("/MESSAGE/message_queue_subscription_6_m6.txt");
        String possibleAnswer2 = readResource("/MESSAGE/message_queue_subscription_3_m6.txt");

        boolean found = false;

        if(client1response.trim().length()>0){

            if(client1response.equals(possibleAnswer1)){
                found = true;
            }
            else{
                fail("Expected message different than received");
            }
        }

        if(client2response.trim().length()>0){
            if(found){
                fail("Message received by more than one client");
            }

            if(client2response.equals(possibleAnswer2)){
                found = true;
            }
            else{
                fail("Expected message different than received");
            }
        }

        if(!found){
            fail("Message was not sent to any client");
        }

        //expectResponseInOneOfClient("/MESSAGE/message_queue_subscription_6_m6.txt", clients[2], clients[3]);


    }


    @Test
    public void shouldNotSendUncommitedMessagesToQueue() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");     //  subscribe to /topic/foo
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");
        waitToPropagateTCP();
        clients[0].close();
        expectNoResponse(clients[1]);
    }

    @Test
    public void shouldSendCommitedMessagesToQueue() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_queue_id_3.txt");     //  subscribe to /topic/foo
        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_queue_tx_a.txt");

        //  then
        expectNoResponse(clients[1]);
        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_queue_subscription_3_m1.txt");

    }

    @Test
    public void shouldSendCommitedMessagesToTopic() throws Exception {

        //  given
        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
        clients[2].sendFrame("/SUBSCRIBE/subscribe_topic_id_100.txt");
        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");

        //  then
        expectNoResponse(clients[1]);
        expectNoResponse(clients[2]);

        clients[0].sendFrame("/COMMIT/commit_tx_a.txt");

        expectNoResponse(clients[0]);

        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectResponse(clients[2], "/MESSAGE/message_topic_subscription_100_response.txt");
    }

    @Ignore("Should be investigated in the future")
    @Test
    public void shouldReturnErrorOnEmptyFrame() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/OTHER/invalid_frame_empty.txt");

        //  then
        expectResponse(clients[0], "/ERROR/error_empty_frame.txt");
    }

    @Test
    public void shouldReturnErrorOnEmptyTransactionNameWithSend() throws Exception {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/SEND/invalid_frame_send_empty_tx.txt");

        //  then
        expectResponse(clients[0], "/ERROR/error_tx_empty_name.txt");
    }

    @Test
    public void shouldSendErrorOnProtocolNotEqualToStomp12() throws Exception {

        //  given
        StompTestClient client = createClient();

        //  when
        String response = client.connectSendAndGrabAnswer("/CONNECT/basic_unsupported_protocol.txt");

        //  then
        assertThat(response, is(equalTo(readResource("/ERROR/error_unsupported_protocol.txt"))));
    }


    @Test
    public void shouldNotSendUncommitedMessagesToTopic() throws Exception {

        //  given
        StompTestClient[] clients = createClients(2);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/BEGIN/begin_tx_a.txt");
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");     //  subscribe to /topic/foo
        waitToPropagateTCP();

        //  then
        clients[0].sendFrame("/SEND/send_destination_topic_tx_a.txt");
        waitToPropagateTCP();
        clients[0].close();
        expectNoResponse(clients[1]);
    }


    @Test
    public void shouldUnsubscribeFromTopic() throws Exception {

        //  given
        StompTestClient[] clients = createClients(3);
        connectClients(clients);

        //  when
        clients[1].sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");     //  subscribe to /topic/foo
        clients[2].sendFrame("/SUBSCRIBE/subscribe_topic_id_100.txt");   //  subscribe to /topic/foo

        /*
         * Unfortunatelly we need to wait a little to make sure that all buffers are flushed to server and interpreted by it.
         */
        waitToPropagateTCP();
        clients[0].sendFrame("/SEND/send_destination_topic.txt");

        //  then
        expectNoResponse(clients[0]);
        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectResponse(clients[2], "/MESSAGE/message_topic_subscription_100_response.txt");

        clients[2].sendFrame("/UNSUBSCRIBE/unsubscribe_id_100.txt");

        waitToPropagateTCP();


        clients[0].sendFrame("/SEND/send_destination_topic.txt");
        expectResponse(clients[1], "/MESSAGE/message_topic_subscription_0_response.txt");
        expectNoResponse(clients[2]);
    }


}