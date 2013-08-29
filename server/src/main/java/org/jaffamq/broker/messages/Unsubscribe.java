package org.jaffamq.broker.messages;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 27.08.13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class Unsubscribe {

    private final String destination;

    public Unsubscribe(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
