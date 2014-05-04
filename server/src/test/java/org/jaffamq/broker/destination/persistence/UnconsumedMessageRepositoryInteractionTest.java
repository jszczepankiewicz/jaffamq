package org.jaffamq.broker.destination.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.PollUnconsumedMessageResponse;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageRequest;
import org.jaffamq.broker.messages.persistence.StoreUnconsumedMessageResponse;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.journal.JournalMessageMessageId;
import org.jaffamq.persistence.journal.UnconsumedMessageJournalRepository;
import org.jaffamq.test.StompMessageFactory;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test that represent interaction between destination / destination manager and unconsumed message repository
 */
public class UnconsumedMessageRepositoryInteractionTest {

    private static final Logger LOG = LoggerFactory.getLogger(UnconsumedMessageRepositoryInteractionTest.class);

    private UnconsumedMessageJournalRepository repo;

    private ActorRef pollUnconsumedMessageService;

    private ActorRef persistUnconsumedMessageService;

    private static ActorSystem system;

    @Rule
    public ExternalResource systemResource = new ExternalResource() {

        @Override
        protected void before() {
            system = ActorSystem.create();
            repo = new UnconsumedMessageJournalRepository(org.jaffamq.test.IOTestHelper.getTempDataDir());
            repo.init();
        }

        @Override
        protected void after() {
            JavaTestKit.shutdownActorSystem(system);
            system = null;
        }
    };


    @Ignore("This test duplicates EndToEnd and is not running because of run-time dependency checking for QueueDestinationManager")
    @Test
    public void shouldPersistAndPollUnconsumedMessage() {

        new JavaTestKit(system) {{

            //  =============== persisting
            //  given
            StompMessage messageToPersist = StompMessageFactory.createMessage();
            StoreUnconsumedMessageRequest request = new StoreUnconsumedMessageRequest(messageToPersist);
            pollUnconsumedMessageService = system.actorOf(Props.create(PollUnconsumedMessageService.class, repo));
            persistUnconsumedMessageService = system.actorOf(Props.create(StoreUnconsumedMessageService.class, repo));

            //  when
            persistUnconsumedMessageService.tell(request, getRef());

            //  then
            StoreUnconsumedMessageResponse response = expectMsgClass(StoreUnconsumedMessageResponse.class);

            assertThat(response, is(notNullValue()));
            assertThat(response.getDestination(), is(equalTo(messageToPersist.getDestination())));
            assertThat(response.getMid(), is(instanceOf(JournalMessageMessageId.class)));
            JournalMessageMessageId id = (JournalMessageMessageId) response.getMid();

            assertThat(id, is(notNullValue()));
            assertThat(id.getLocation(), is(notNullValue()));

            //  =============== polling
            //  given
            PollUnconsumedMessageRequest pollRequest = new PollUnconsumedMessageRequest(id);


            //  when
            pollUnconsumedMessageService.tell(pollRequest, getRef());

            //  then
            PollUnconsumedMessageResponse pollResponse = expectMsgClass(PollUnconsumedMessageResponse.class);
            assertThat(pollResponse, is(notNullValue()));
            assertThat(pollResponse.getPersistedMessageId(), is(equalTo((PersistedMessageId) id)));
            assertThat(pollResponse.getMessage(), is(equalTo(messageToPersist)));

        }};
    }

}
