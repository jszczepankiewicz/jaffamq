package org.jaffamq.persistence.journal;

import journal.io.api.Location;
import journal.io.api.Journal;
import journal.io.api.JournalBuilder;
import org.jaffamq.messages.StompMessage;
import org.jaffamq.persistence.FastStompMessageSerializer;
import org.jaffamq.persistence.PersistedMessageId;
import org.jaffamq.persistence.StompMessageSerializer;
import org.jaffamq.persistence.UnconsumedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class UnconsumedMessageJournalRepository implements UnconsumedMessageRepository{

    private static final Logger LOG = LoggerFactory.getLogger(UnconsumedMessageJournalRepository.class);

    private final StompMessageSerializer serializer;
    private final String rootDir;
    private Journal journal;
    private Map<String, Queue<PersistedMessageId>> destinationsByName = new ConcurrentHashMap<>();

    public UnconsumedMessageJournalRepository(String rootDir) {
        this.serializer = new FastStompMessageSerializer();
        this.rootDir = rootDir;
    }

    @Override
    public void shutdown() {
        try {
            journal.close();
        } catch (IOException e) {
            throw new IllegalStateException("IOException while closing journal", e);
        }
    }

    @Override
    public void init() {

        File path = new File(rootDir);

        if(path.exists()){
            LOG.info("Using existing directory with data [{}]", rootDir);
        }
        else{
            LOG.info("Directory with data [{}] not found, will create one", rootDir);
            boolean result = path.mkdirs();

            if(!result){
                throw new IllegalStateException(String.format("Can not create directory for journaling: %s", rootDir));
            }
        }

        try {
            journal = JournalBuilder.of(path).open();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while opening Journal", e);
        }

        recreateDestinationToLocationsMapping();
    }

    public Map<String, Queue<PersistedMessageId>> getPersistedMessagesByLocation(){
        return destinationsByName;
    }

    private void unregisterLocation(String destination, Location location){
        getLocationsForDestination(destination).remove(location);
    }

    private void registerLocation(String destination, Location location){
        getLocationsForDestination(destination).add(new JournalMessageMessageId(location));
    }



    /**
     * Retrieve of create & retrieve list of locations for given destionations.
     * WARNING: this method is not internally thread safe and should be run in synchronized context (to locations & destinationsByName) if createIfNoPresent = true.
     * @param destination
     * @return
     */
    private Queue<PersistedMessageId> getLocationsForDestination(String destination, boolean createIfNoPresent){
        Queue<PersistedMessageId> locations = destinationsByName.get(destination);

        if(locations == null && createIfNoPresent){
            locations = new ArrayDeque<PersistedMessageId>();
            destinationsByName.put(destination, locations);
        }

        return locations;
    }

    private Queue<PersistedMessageId> getLocationsForDestination(String destination){
        return getLocationsForDestination(destination, true);
    }

    /**
     * Read the journal and build index of destinations -> locations.
     * This method is not thread safe and should be done only once per live of this object.
     */
    private void recreateDestinationToLocationsMapping(){
        LOG.debug("Indexing database started...");
        int counter = 0;
        try {
            for (Location location : journal.redo()) {
                byte[] record = journal.read(location, Journal.ReadType.SYNC);
                StompMessage m = serializer.fromBytes(record);
                registerLocation(m.getDestination(), location);
                counter++;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while redoing the journal", e);
        }

        LOG.debug("Database indexing finished, indexed {} messages", counter);
    }

    @Override
    public PersistedMessageId persistMessage(StompMessage message) {

        LOG.info("Persisting message id: {} for destination: {}", message.getMessageId(), message.getDestination());

        try {
            Location location = journal.write(serializer.toBytes(message), Journal.WriteType.ASYNC);
            JournalMessageMessageId id = new JournalMessageMessageId(location);
            LOG.debug("Persisted message id: {}", message.getMessageId());
            return id;
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while writing message", e);
        }
    }

    @Override
    public StompMessage pollMessage(PersistedMessageId id) {

        LOG.debug("Polling message id: {}", id);

        JournalMessageMessageId jid = (JournalMessageMessageId)id;
        Location loc = jid.getLocation();

        try{
            StompMessage msg = serializer.fromBytes(journal.read(loc, Journal.ReadType.SYNC));
            journal.delete(loc);
            return msg;
        }
        catch(IOException e){
            throw new IllegalStateException("Unexpected IOException while reading message", e);
        }
    }


}
