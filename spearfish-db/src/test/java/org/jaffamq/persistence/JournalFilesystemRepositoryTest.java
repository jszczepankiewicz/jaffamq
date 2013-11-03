package org.jaffamq.persistence;

import journal.io.api.Journal;
import journal.io.api.JournalBuilder;
import org.hamcrest.CoreMatchers;
import org.jaffamq.messages.StompMessage;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.TableStringConverter;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 23.10.13
 * Time: 20:47
 * To change this template use File | Settings | File Templates.
 */
public class JournalFilesystemRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(JournalFilesystemRepositoryTest.class);

    private JournalFilesystemRepository repo;
    private Journal journal;

    @Rule
    public ExternalResource journalResource = new ExternalResource() {

        @Override
        protected void before() throws IOException {

            repo = new JournalFilesystemRepository(getTempDataDir(), Journal.WriteType.SYNC);
            repo.initialize();
        }

        @Override
        protected void after() {
            repo.shutdown();
        }
    };

    private String getTempDataDir(){
        String tmpDir = System.getProperty("java.io.tmpdir");
        return tmpDir.endsWith(File.separator)?tmpDir:tmpDir.concat(File.separator) + System.currentTimeMillis();
    }

    @Ignore("This test should be run only in isolated local environment for stress testing because it produces immense amount of messages")
    @Test
    public void shouldPersistLargeAmountOfMessagesToTopic() throws Exception{

        for(int i=0; i<1000; i++){
            String destination = "dest" + i;
            for(int n=0; n<10000; n++){
                repo.persist(StompMessageFactory.createMessage(destination));
            }
        }
    }

    @Test
    public void shouldPersistAndPollMessagesFromTopic() throws Exception{

        //  given
        String destination = "xy";

        StompMessage msg = StompMessageFactory.createMessage(destination);

        //  when
        repo.persist(msg);

        //  then
        StompMessage retrived = repo.poll(destination);
        assertThat(retrived, is(equalTo(msg)));

        StompMessage retrieved2 = repo.poll(destination);
        assertThat(retrieved2, is(equalTo(msg)));

    }

    @Test
    public void shouldPersistAndPeekMessagesFromQueue() throws Exception{

        //  given
        String destination = "omega";

        StompMessage msg = StompMessageFactory.createMessage(destination);

        //  when
        repo.persist(msg);

        //  then
        StompMessage retrived = repo.peek(destination);
        assertThat(retrived, is(equalTo(msg)));

        StompMessage retrieved2 = repo.peek(destination);
        assertThat(retrieved2, is(nullValue()));
    }

    @Test
    public void shouldReturnNullForPollOnEmptyDestination() throws Exception{

        //  when
        StompMessage msg = repo.peek("nonexisting");

        //  that
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldReturnNullForPeekOnEmptyDestination() throws Exception{

        //  when
        StompMessage msg = repo.poll("nonexisting");

        //  that
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldPollMessagesFromPreviousBrokerShutdown() throws Exception{

        //  given
        String destination ="/topic/bunny";
        String directory = getTempDataDir();

        JournalFilesystemRepository livingrepo = new JournalFilesystemRepository(directory, Journal.WriteType.SYNC);
        livingrepo.initialize();
        StompMessage msg1 = StompMessageFactory.createMessage(destination);
        livingrepo.persist(msg1);
        livingrepo.shutdown();

        livingrepo = new JournalFilesystemRepository(directory, Journal.WriteType.SYNC);
        livingrepo.initialize();

        //  when
        StompMessage messageFromPreviousRun = livingrepo.poll(destination);

        //  then
        assertThat(messageFromPreviousRun, is(notNullValue()));
        assertThat(messageFromPreviousRun, is(equalTo(msg1)));
        assertThat(messageFromPreviousRun, CoreMatchers.is(not(sameInstance(msg1))));

        //  checking if that was poll really
        messageFromPreviousRun = livingrepo.poll(destination);
        assertThat(messageFromPreviousRun, is(notNullValue()));

        messageFromPreviousRun = livingrepo.peek(destination);
        assertThat(messageFromPreviousRun, is(notNullValue()));


    }

    @Test
    public void shouldReturnNonEmptySize() throws Exception{

        //  given
        String destination = "omega";

        StompMessage msg = StompMessageFactory.createMessage(destination);

        //  when
        repo.persist(msg);

        //  then
        assertThat(repo.isNonEmpty(destination), is(equalTo(true)));

    }

    @Test
    public void shouldReturnEmptySize() throws Exception{

        //  then
        assertThat(repo.isNonEmpty("x"), is(equalTo(false)));
    }

    @Test
    public void shouldPeekMessagesFromPreviousBrokerShutdown() throws Exception{

        //  given
        String destination ="/queue/bunny";
        String directory = getTempDataDir();

        JournalFilesystemRepository livingrepo = new JournalFilesystemRepository(directory, Journal.WriteType.SYNC);
        livingrepo.initialize();
        StompMessage msg1 = StompMessageFactory.createMessage(destination);
        livingrepo.persist(msg1);
        livingrepo.shutdown();

        livingrepo = new JournalFilesystemRepository(directory, Journal.WriteType.SYNC);
        livingrepo.initialize();

        //  when
        StompMessage messageFromPreviousRun = livingrepo.peek(destination);

        //  then
        assertThat(messageFromPreviousRun, is(notNullValue()));
        assertThat(messageFromPreviousRun, is(equalTo(msg1)));
        assertThat(messageFromPreviousRun, CoreMatchers.is(not(sameInstance(msg1))));

        //  checking if peek really works
        StompMessage msg2 = livingrepo.peek(destination);
        assertThat(msg2, is(nullValue()));

        msg2 = livingrepo.poll(destination);
        assertThat(msg2, is(nullValue()));


    }

}
