package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 25.10.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class StandardStompMessageSerializer implements StompMessageSerializer{

    @Override
    public byte[] toBytes(StompMessage message) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = null;
        try {
            o = new ObjectOutputStream(b);
            o.writeObject(message);
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while serializing StompMessage", e);
        }

        return b.toByteArray();
    }

    @Override
    public StompMessage fromBytes(byte[] bytes) {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = null;
        try {
            o = new ObjectInputStream(b);
            return (StompMessage)o.readObject();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException while serializing StompMessage", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Unexpected ClassNotFoundException while serializing StompMessage", e);
        }
    }
}
