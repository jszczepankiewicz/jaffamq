package org.jaffamq;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 17.08.13
 * Time: 12:16
 * To change this template use File | Settings | File Templates.
 */
public enum Command {

    //  to-broker
    NONE("NONE"),
    SEND("SEND"),
    SUBSCRIBE("SUBSCRIBE"),
    UNSUBSCRIBE("UNSUBSCRIBE"),
    BEGIN("BEGIN"),
    COMMIT("COMMIT"),
    ABORT("ABORT"),
    DISCONNECT("DISCONNECT"),
    CONNECT("CONNECT"),
    //  to-client
    MESSAGE("MESSAGE"),
    RECEIPT("RECEIPT"),
    CONNECTED("CONNECTED"),
    ERROR("ERROR"),;
    private String text;

    Command(String text) {
        this.text = text;
    }

    public static Command forName(String value) {
        value = value.trim();
        if (value.equals("SEND")) return SEND;
        else if (value.equals("SUBSCRIBE")) return SUBSCRIBE;
        else if (value.equals("UNSUBSCRIBE")) return UNSUBSCRIBE;
        else if (value.equals("BEGIN")) return BEGIN;
        else if (value.equals("COMMIT")) return COMMIT;
        else if (value.equals("ABORT")) return ABORT;
        else if (value.equals("CONNECT")) return CONNECT;
        else if (value.equals("MESSAGE")) return MESSAGE;
        else if (value.equals("RECEIPT")) return RECEIPT;
        else if (value.equals("CONNECTED")) return CONNECTED;
        else if (value.equals("DISCONNECT")) return DISCONNECT;
        else if (value.equals("ERROR")) return ERROR;
        throw new IllegalArgumentException("Unrecognised command " + value);
    }

}
