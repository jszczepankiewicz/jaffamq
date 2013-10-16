package org.jaffamq.broker;

import org.jaffamq.RequestValidationFailedException;
import org.jaffamq.broker.messages.StompMessage;
import org.jaffamq.broker.transaction.Transaction;
import org.jaffamq.broker.transaction.TransactionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test for VoldatieMemoryTransaction class
 * Since: 28.09.13
 */
public class VolatileMemoryTransactionTest {

    private Transaction tx;

    private StompMessageSender sender;

    private int messageCommitedCounter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init(){
        messageCommitedCounter = 0;
        sender = new StompMessageSender() {
            @Override
            public void sendStompMessage(StompMessage message) {
                messageCommitedCounter++;
            }
        };
        tx = TransactionFactory.createTransaction("txABC", sender);
    }

    @Test
    public void shouldReturnClientTransactionName(){

        //  then
        assertThat(tx.getName(), is(equalTo("txABC")));
    }


    @Test
    public void shouldAllowToCommitTransactionOnlyOnce() throws Exception{

        //  given
        StompMessage m1 = new StompMessage("dest1", "body1", new HashMap<String,String>(),"m1");
        tx.addStompMessage(m1);

        //  when
        tx.commit();
        exception.expect(RequestValidationFailedException.class);
        //TODO: add validation code
        //exception.expectMessage("Transaction to commit should be in STARTED state but is in COMMITED");

        tx.commit();
    }

    @Test
    public void shouldAllowToRollbackTransactionOnlyOnce() throws Exception{

        //  given
        StompMessage m1 = new StompMessage("dest1", "body1", new HashMap<String,String>(),"m1");
        tx.addStompMessage(m1);

        //  when
        tx.rollback();
        exception.expect(RequestValidationFailedException.class);
        //TODO: add validation code
        //exception.expectMessage("Transaction to commit should be in STARTED state but is in ROLLBACKED");


        tx.rollback();

    }

    @Test
    public void shouldCorrectlyAddMessagesAndIterateThroughThem()throws Exception{

        //  given
        StompMessage m1 = new StompMessage("dest1", "body1", new HashMap<String,String>(),"m1");
        StompMessage m2 = new StompMessage("dest2", "body2", new HashMap<String,String>(),"m2");
        StompMessage m3 = new StompMessage("dest3", "body3", new HashMap<String,String>(),"m3");

        //  when
        tx.addStompMessage(m1);
        tx.addStompMessage(m2);
        tx.addStompMessage(m3);

        List<StompMessage> messages = new ArrayList<>();
        Iterator<StompMessage> iterator = tx.getMessagesInTransaction();

        while(iterator.hasNext()){
            messages.add(iterator.next());
        }

        //  then
        assertThat(messages.size(), is(equalTo(3)));
        assertThat(messages.get(0), is(equalTo(m1)));
        assertThat(messages.get(1), is(equalTo(m2)));
        assertThat(messages.get(2), is(equalTo(m3)));
    }
}
