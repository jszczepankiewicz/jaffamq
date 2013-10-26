package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 24.10.13
 * Time: 20:24
 * To change this template use File | Settings | File Templates.
 */
public interface StompMessageSerializer {

    byte[] toBytes(StompMessage message);

    StompMessage fromBytes(byte[] bytes);

}
