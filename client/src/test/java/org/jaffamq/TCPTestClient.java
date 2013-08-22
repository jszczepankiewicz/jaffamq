package org.jaffamq;

import java.io.*;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 18.08.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class TCPTestClient {

    private final int port;
    private final String host;
    private int timeoutInMs;
    private boolean isOpen;
    private Socket clientSocket;
    private Writer out;
    private Reader in;

    public TCPTestClient(int port, String host, int timeoutInMs){
        this.port = port;
        this.host = host;
        this.timeoutInMs = timeoutInMs;
    }

    public void close(){


    }

    public String connectSendAndGrabAnswer(String requestResourcePath, boolean closeAfterNullReceive) throws IOException {

        if(isOpen){
            throw new IllegalStateException("Close before connect");
        }

            clientSocket = new Socket("taranis", 4444);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


    }

}
