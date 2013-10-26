package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 23.10.13
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public interface Repository {

    /**
     * Blocking operation related to initialization of the repository. Role of this method is implementation specific.
     */
    void initialize();

    /**
     * Closes the journal cleanly.
     */
    void shutdown();

    /**
     * Clears the journal completely. The purpose of this method is strictly for testing purposes and should be not tested in production.
     */
    void clear();

    /**
     * Add method to the queue.
     * @param message
     */
    void persist(StompMessage message);

    /**
     * Retrieves and removes the head of this destination, or null if this destination is empty.
     * @param destination
     * @return
     */
    StompMessage poll(String destination);

    /**
     * Retrieves, but does not remove, the head of this destination, returning null if this destination is empty.
     * @param destination
     * @return
     */
    StompMessage peek(String destination);
}
