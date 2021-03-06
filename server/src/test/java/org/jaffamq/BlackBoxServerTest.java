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
    public void shouldChangeStateToConnectedAfterSuccessfulConnect() {

        //  given
        StompTestClient client = createClient();

        //  when
        String response = client.connectSendAndGrabAnswer("/CONNECT/basic.txt");

        //  then
        assertThat(response, is(equalTo(readResource("/CONNECT/basic_response.txt"))));
    }

    @Test
    public void shouldAcceptCorrectSendFrame() {

        //  given
        StompTestClient client = createClient();
        connectClient(client);

        //  when
        client.sendFrame("/SEND/send_destination_topic.txt");
        expectNoResponse(client);
    }

    @Test
    public void shouldAcceptCorrectSubscribeFrame() {

        //  given
        StompTestClient client = createClient();
        connectClient(client);

        //   when
        client.sendFrame("/SUBSCRIBE/subscribe_topic_id_0.txt");
    }

    @Test
    public void shouldSubscribeToTopicWithSelectedClients() {

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
    public void shouldRespondToConnectWithErrorForMissingHost() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/CONNECT/invalid_frame_connect_headers_missing_host.txt");
        expectResponse(client, "/ERROR/error_headers_missing_host.txt");
    }

    @Test
    public void shouldRespondToConnectWithErrorForMissingAcceptVersion() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/CONNECT/invalid_frame_connect_headers_missing_accept_version.txt");
        expectResponse(client, "/ERROR/error_headers_missing_accept_version.txt");
    }

    @Test
    public void shouldRespondToSendToTopicWithErrorForMissingDestination() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_headers_missing_destination.txt");
        expectResponse(client, "/ERROR/error_headers_missing_destination.txt");

    }

    @Test
    public void shouldRespondToSubscribeToTopicWithErrorForMissingId() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SUBSCRIBE/invalid_frame_subscribe_headers_missing_id.txt");
        expectResponse(client, "/ERROR/error_headers_missing_id.txt");

    }

    @Test
    public void shouldRespondToUnknownDestinationTypeWithError() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_unsupported_destination_type.txt");
        expectResponse(client, "/ERROR/error_unsupported_destination_type.txt");
    }

    @Test
    public void shouldRespondToInvalidQueueDestinationNameWithError() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_invalid_queue_destination_name.txt");
        expectResponse(client, "/ERROR/error_invalid_destination_name.txt");
    }

    @Test
    public void shouldRespondToBeginWithErrorForMissingTransaction() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/BEGIN/invalid_frame_begin_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");
    }

    @Test
    public void shouldRespondToCommitWithErrorForMissingTransaction() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/COMMIT/invalid_frame_commit_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");

    }

    @Test
    public void shouldRespondToAbortWithErrorForMissingTransaction() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/ABORT/invalid_frame_abort_headers_missing_transaction.txt");
        expectResponse(client, "/ERROR/error_headers_missing_transaction.txt");

    }

    @Test
    public void shouldRespondToInvalidTopicDestinationNameWithError() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SEND/invalid_frame_send_invalid_topic_destination_name.txt");
        expectResponse(client, "/ERROR/error_invalid_destination_name.txt");
    }

    @Test
    public void shouldRespondToSubscribeToTopicWithErrorForMissingDestination() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/SUBSCRIBE/invalid_frame_subscribe_headers_missing_destination.txt");
        expectResponse(client, "/ERROR/error_headers_missing_destination.txt");

    }

    @Test
    public void shouldRespondToUnsubscribeToTopicWithErrorForMissingId() {

        //  given
        StompTestClient client = createConnectedClient();

        //   when
        client.sendFrame("/UNSUBSCRIBE/invalid_frame_unsubscribe_headers_missing_id.txt");
        expectResponse(client, "/ERROR/error_headers_missing_id.txt");

    }

    //  is using persistence
    @Test
    public void shouldKeepMessagesFromQueueForNextSubscriberIfSentWithoutActiveSubscribers() {

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
    public void shouldRecycleQueueSubscribersOnUnsubscribe() {

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
    public void shouldRecycleQueueSubscribersOnMoreMessages() {

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
    public void shouldReturnErrorOnBeginTwiceSameTx() {

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
    public void shouldReturnErrorOnAbortNotKnownTx() {

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
    public void shouldReturnErrorOnCommitNotKnownTx() {

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
    public void shouldReturnErrorOnCommitTwiceSameTx() {

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
    public void shouldReturnErrorOnAbortTwiceSameTx() {

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
    public void shouldSendCommitedMessagesFromOneTransactionToMultipleDestinations() {

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
    public void shouldNotSendRollbackedMessagesToTopic() {

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
    public void shouldNotSendRollbackedMessagesToQueue() {

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
    public void shouldNotSendRollbackedMessagesFromOneTransactionToMulitpleDestinations() {

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
    public void shouldSendOnlyMessagesFromCommitedTxWithSecondTxFromSameClientUncommited() {

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

        String client2response = clients[2].getResponseOrTimeout();
        String client3response = clients[3].getResponseOrTimeout();

        String possibleAnswerSubscriptionId3 = readResource("/MESSAGE/message_queue_subscription_3_m12.txt");
        String possibleAnswerSubscriptionId6 = readResource("/MESSAGE/message_queue_subscription_6_m12.txt");

        boolean found = false;

        if (client2response.trim().length() > 0) {

            if (client2response.trim().equals(possibleAnswerSubscriptionId3.trim())) {
                found = true;
            } else {
                failWithMessages(possibleAnswerSubscriptionId3, client2response);
            }
        }

        if (client3response.trim().length() > 0) {
            if (found) {
                fail("Message received by more than one client");
            }

            if (client3response.trim().equals(possibleAnswerSubscriptionId6.trim())) {
                found = true;
            } else {
                failWithMessages(possibleAnswerSubscriptionId6, client3response);
            }
        }

        if (!found) {
            fail("Message was not sent to any client");
        }

    }


    @Test
    public void shouldNotSendUncommitedMessagesToQueue() {

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
    public void shouldSendCommitedMessagesToQueue() {

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
    public void shouldSendCommitedMessagesToTopic() {

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
    public void shouldReturnErrorOnEmptyFrame() {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/OTHER/invalid_frame_empty.txt");

        //  then
        expectResponse(clients[0], "/ERROR/error_empty_frame.txt");
    }

    @Test
    public void shouldReturnErrorOnEmptyTransactionNameWithSend() {

        //  given
        StompTestClient[] clients = createClients(1);
        connectClients(clients);

        //  when
        clients[0].sendFrame("/SEND/invalid_frame_send_empty_tx.txt");

        //  then
        expectResponse(clients[0], "/ERROR/error_tx_empty_name.txt");
    }

    @Test
    public void shouldSendErrorOnProtocolNotEqualToStomp12() {

        //  given
        StompTestClient client = createClient();

        //  when
        String response = client.connectSendAndGrabAnswer("/CONNECT/basic_unsupported_protocol.txt");

        //  then
        assertThat(response, is(equalTo(readResource("/ERROR/error_unsupported_protocol.txt"))));
    }


    @Test
    public void shouldNotSendUncommitedMessagesToTopic() {

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
    public void shouldUnsubscribeFromTopic() {

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
