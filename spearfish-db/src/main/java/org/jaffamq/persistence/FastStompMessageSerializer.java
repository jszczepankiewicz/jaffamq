package org.jaffamq.persistence;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import org.jaffamq.messages.StompMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**

 */
public class FastStompMessageSerializer implements StompMessageSerializer{

    static FSTConfiguration conf;

    public FastStompMessageSerializer(){
        conf = FSTConfiguration.createDefaultConfiguration();
        conf.registerClass(StompMessage.class);
    }

    @Override
    public byte[] toBytes(StompMessage message) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FSTObjectOutput out = new FSTObjectOutput(outputStream);
        try {
            out.writeObject( message );
            out.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }

    @Override
    public StompMessage fromBytes(byte[] bytes) {

        FSTObjectInput in = null;
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            in = conf.getObjectInput(is);

            StompMessage result = (StompMessage)in.readObject(StompMessage.class);
            is.close();
            return result;

        } catch (Exception e) {
            throw new IllegalStateException("Unknow exception", e);
        }
    }
}
