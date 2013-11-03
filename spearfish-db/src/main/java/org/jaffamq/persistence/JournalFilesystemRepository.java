package org.jaffamq.persistence;

import journal.io.api.Journal;
import journal.io.api.JournalBuilder;
import journal.io.api.Location;
import org.jaffamq.messages.StompMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 23.10.13
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public class JournalFilesystemRepository implements Repository {

    private static final Logger LOG = LoggerFactory.getLogger(JournalFilesystemRepository.class);

    private final String rootDir;

    private Journal.WriteType writeType;

    private StompMessageSerializer serializer;

    private Journal journal;

    private Map<String, LinkedList<Location>> destinationsByName = new HashMap<>();

    public JournalFilesystemRepository(String rootDir, Journal.WriteType writeType){
        this.rootDir = rootDir;
        this.writeType = writeType;
        //serializer = new FastStompMessageSerializer();
        serializer = new StandardStompMessageSerializer();
    }

    private void printBanner(){
        LOG.info("\n==================================================================\n" +
                 "      Journal.IO db initialized with following configuration:\n" +
                 "  using archiveFiles: " + journal.isArchiveFiles() + "\n" +
                 "     using checksums: " + journal.isChecksum() + "\n" +
                 "  using physicalSync: " + journal.isPhysicalSync() + "\n" +
                 "  maxFileLength (kB): " + journal.getMaxFileLength()/1024 + "\n" +
                 "  maxWriteBatchSize (kB): " + journal.getMaxWriteBatchSize() / 1024 + "\n" +
                "==================================================================");
    }


    @Override
    public void initialize() {

        File path = new File(rootDir);

        if(path.exists()){
            LOG.info("Using existing directory with data [{}]", rootDir);
        }
        else{
            LOG.info("Directory with data [{}] not found, will create one", rootDir);
            boolean result = path.mkdir();

            if(!result){
                throw new IllegalStateException(String.format("Can not create directory for journaling: %s", rootDir));
            }
        }

        try {
            journal = JournalBuilder.of(path).open();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while opening Journal", e);
        }

        //  we need to recreate in memory mapping for every destination
        printBanner();
        recreateDestinationToLocationsMapping();
    }

    /**
     * Read the journal and build index of destinations -> locations.
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

    private LinkedList<Location> getLocationsForDestination(String destination){
        LinkedList<Location> locations = destinationsByName.get(destination);

        if(locations == null){
            locations = new LinkedList<>();
            destinationsByName.put(destination, locations);
        }

        return locations;
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
    public void clear() {
        try {
            journal.truncate();
        } catch (IOException e) {
            throw new IllegalStateException("IOException while truncating journal", e);
        }
    }
    private void unregisterLocation(String destination, Location location){
        getLocationsForDestination(destination).remove(location);
    }

    private void registerLocation(String destination, Location location){
        getLocationsForDestination(destination).add(location);
    }

    @Override
    public void persist(StompMessage message) {

        try {
            Location location = journal.write(serializer.toBytes(message), writeType);
            registerLocation(message.getDestination(), location);

        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while writing message", e);
        }
    }

    @Override
    public StompMessage poll(String destination) {

        LinkedList<Location> locations = getLocationsForDestination(destination);

        if(locations.size() == 0){
            LOG.debug("No messages for location [{}] found", destination);
            return null;
        }

        try {
            return serializer.fromBytes(journal.read(locations.getFirst(), Journal.ReadType.SYNC));
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while reading journal", e);
        }
    }

    @Override
    public StompMessage peek(String destination) {

        LinkedList<Location> locations = getLocationsForDestination(destination);

        if(locations.size() == 0){
            LOG.debug("No messages for location [{}] found", destination);
            return null;
        }

        try {
            Location location = locations.getFirst();
            StompMessage msg = serializer.fromBytes(journal.read(location, Journal.ReadType.SYNC));
            journal.delete(location);
            locations.remove(location);
            return msg;

        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while reading journal", e);
        }
    }

    @Override
    public boolean isNonEmpty(String destination) {

        LinkedList<Location> locations = getLocationsForDestination(destination);

        if(locations.size() == 0){
            LOG.debug("No messages for location [{}] found", destination);
            return false;
        }

        return true;
    }
}
