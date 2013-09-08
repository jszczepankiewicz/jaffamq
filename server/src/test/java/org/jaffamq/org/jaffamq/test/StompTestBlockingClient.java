package org.jaffamq.org.jaffamq.test;


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
public class StompTestBlockingClient implements StompTestClient {
    private static Logger LOG = LoggerFactory.getLogger(StompTestBlockingClient.class);

    private final int port;
    private final String host;
    private int timeoutInMs;
    private boolean isOpen;
    private Socket clientSocket;
    private Writer out;
    private BufferedReader in;


    public StompTestBlockingClient(int port, String host, int timeoutInMs){
        LOG.debug("Constructing StompTestBlockingClient with host: {}:{}", host, port);
        this.port = port;
        this.host = host;
        this.timeoutInMs = timeoutInMs;
    }

    @Override
    public void close() throws IOException {

        if(out != null){
            out.write("DISCONNECT\n\n\000\n");
            out.flush();
        }

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
     * Get response, wait maximum of timeoutInMs. Returns on end or on timeout (without exception). Usuefull if you want to check if the
     * client DID NOT received message (in specified amount of time).
     * @return
     * @throws IOException
     */
    public String getResponseOrTimeout() throws IOException{

        StringBuilder builder = new StringBuilder();
        String line;

        try{
            while ((line = in.readLine()) != null && !line.equals("\000")) {
                builder.append(line);
                builder.append("\n");
            }
        }
        catch(java.net.SocketTimeoutException ex){
            //  timeout
        }

        String response = builder.toString().trim();
        return response;
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

        String response = builder.toString().trim();
        LOG.debug("Retrieved broker response:\n==============================================================================\n{" +
                "}\n==============================================================================", response);
        return response;
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

    @Override
    public String sendFrameAndWaitForResponseFrame(String frameResource) throws IOException{
        sendFrame(frameResource);
        return getResponse();
    }

    @Override
    public void sendFrame(String frameResource) throws IOException{
        LOG.debug("Sending client frame from resource: {}", frameResource);

        out.write(readResource(frameResource));
        out.write("\000");
        out.write("\n");
        out.flush();
    }

    @Override
    public String connectSendAndGrabAnswer(String requestResourcePath) throws IOException {

        if(isOpen){
            throw new IllegalStateException("Close before connect");
        }

        clientSocket = new Socket(host, port);

        if(timeoutInMs > 0){
            clientSocket.setSoTimeout(timeoutInMs);
        }

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        sendFrame(requestResourcePath);

        return getResponse();

    }

}
