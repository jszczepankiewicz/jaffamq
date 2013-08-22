package org.jaffamq;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 18.08.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class TCPTestClient {
    private static Logger LOG = LoggerFactory.getLogger(TCPTestClient.class);

    private final int port;
    private final String host;
    private int timeoutInMs;
    private boolean isOpen;
    private Socket clientSocket;
    private Writer out;
    private BufferedReader in;
    private static final Charset ENC=Charset.forName("UTF-8");

    public TCPTestClient(int port, String host, int timeoutInMs){
        LOG.debug("Constructing TCPTestClient with host: {}:{}", host, port);
        this.port = port;
        this.host = host;
        this.timeoutInMs = timeoutInMs;
    }

    public void close() throws IOException {

        out.write("DISCONNECT\n\n\000\n");
        out.flush();
        /*
            Need to add some timeout, gracefull shutdown
         */
        //String responseIgnored = getResponse();

        LOG.debug("Before closing()");
        if(out!=null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if(in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        LOG.debug("After closing()");

    }

    /**
     * WARNIG: it trims the values
     * @return
     * @throws IOException
     */
    private String getResponse() throws IOException {

        StringBuilder builder = new StringBuilder();
        String line;

        //  FIXME: dodaje na sztynwo \n powinna być detekcja czy przeszło już na tryb body wtedy treść powinna być bajtowo traktowana
        while ((line = in.readLine()) != null && !line.equals("\000")) {
            builder.append(line);
            builder.append("\n");
        }

        return builder.toString().trim();
    }

    private String readResource(String classpathResource) throws IOException {
        LOG.debug("readResponse({})", classpathResource);
        StringWriter writer = new StringWriter();
        InputStream is = this.getClass().getResourceAsStream(classpathResource);
        if(is == null){
            throw new IllegalArgumentException("Classpath resource: " + classpathResource + " not found");
        }
        IOUtils.copy(is, writer, ENC);
        return writer.getBuffer().toString();
    }

    public void sendFrame(String frameResource) throws IOException{
        LOG.debug("Sending client frame from resource: {}", frameResource);

        out.write(readResource(frameResource));
        out.write("\000");
        out.write("\n");
        out.flush();
    }

    public String connectSendAndGrabAnswer(String requestResourcePath) throws IOException {

        if(isOpen){
            throw new IllegalStateException("Close before connect");
        }

        clientSocket = new Socket(host, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        sendFrame(requestResourcePath);

        return getResponse();

    }

}
