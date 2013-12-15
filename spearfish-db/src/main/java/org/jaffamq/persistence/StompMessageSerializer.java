package org.jaffamq.persistence;

import org.jaffamq.messages.StompMessage;

/**
 * Represents marshaller / unmarshaller from bytes to StompMessage object and vice versa.
 */
public interface StompMessageSerializer {

    /**
     * Marshalling StompMessage object to bytes. Unmarshalling can be only done using the same implementation class.
     *
     * @param message to be marshalled
     * @return value of the message represented by bytes.
     */
    byte[] toBytes(StompMessage message);

    /**
     * Unmarshalling from bytes to StompMessage. Supports only unmarshalling from bytes produced by same implementation class.
     * @param bytes that represents StompMessage
     * @return unmarshalled StompMessage.
     */
    StompMessage fromBytes(byte[] bytes);

}
