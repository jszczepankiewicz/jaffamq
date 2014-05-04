package org.jaffamq.persistence.journal;

import journal.io.api.Location;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.test.StompMessageFactory;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class UnconsumedMessageJournalRepositoryTest {

    private static final Logger LOG = LoggerFactory.getLogger(UnconsumedMessageJournalRepositoryTest.class);

    private UnconsumedMessageJournalRepository repo;

    @Rule
    public ExternalResource journalResource = new ExternalResource() {

        @Override
        protected void before() throws IOException {

            repo = new UnconsumedMessageJournalRepository(getTempDataDir());
            repo.init();
        }

        @Override
        protected void after() {
            repo.shutdown();
        }
    };

    private String getTempDataDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        tmpDir = tmpDir.endsWith(File.separator) ? tmpDir : tmpDir.concat(File.separator);
        return tmpDir + System.currentTimeMillis();
    }

    @Test
    public void shouldPersistMessageSuccessfully() {

        //  given
        StompMessage msg1 = StompMessageFactory.createMessage();

        //  when
        PersistedMessageId id = repo.persistMessage(msg1);

        //  then
        assertThat(id, is(notNullValue()));
        assertThat(id, is(instanceOf(JournalMessageMessageId.class)));
        JournalMessageMessageId jid = (JournalMessageMessageId) id;
        assertThat(jid.getLocation(), is(notNullValue()));

    }

    @Test
    public void shouldPersistAndPollMessageSuccessfully() {

        //  given
        StompMessage msg1 = StompMessageFactory.createMessage();

        //  when
        PersistedMessageId id = repo.persistMessage(msg1);

        //  then
        assertThat(id, is(notNullValue()));
        StompMessage retrieved = repo.pollMessage(id);
        assertThat(retrieved, is(equalTo(msg1)));
        assertThat(retrieved, is(not(sameInstance(msg1))));
    }

    @Ignore("Not decided yet what to do with invalid location")
    @Test
    public void shouldReturnNullIfMessageRetrievedForSecondTime() {

        //  given
        StompMessage msg1 = StompMessageFactory.createMessage();

        //  when
        PersistedMessageId id = repo.persistMessage(msg1);

        //  then
        assertThat(id, is(notNullValue()));
        StompMessage retrieved = repo.pollMessage(id);
        assertThat(retrieved, is(equalTo(msg1)));
        assertThat(retrieved, is(not(sameInstance(msg1))));

        //  retrieving same location once again
        StompMessage retrieved2 = repo.pollMessage(id);
        assertThat(retrieved2, is(nullValue()));
    }

    @Ignore("Not decided yet what to do with invalid location")
    @Test
    public void shouldReturnNullIfMessageIdNotFound() {

        //  given
        Location loc = new Location();
        PersistedMessageId id = new JournalMessageMessageId(loc);

        //  when
        StompMessage retrieved = repo.pollMessage(id);

        //  then
        assertThat(retrieved, is(nullValue()));
    }
}
