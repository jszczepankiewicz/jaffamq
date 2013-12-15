package org.jaffamq;

/**
 * Stomp protocol command representation.
 */
public enum Command {

    NONE("NONE"),
    SEND("SEND"),
    SUBSCRIBE("SUBSCRIBE"),
    UNSUBSCRIBE("UNSUBSCRIBE"),
    BEGIN("BEGIN"),
    COMMIT("COMMIT"),
    ABORT("ABORT"),
    DISCONNECT("DISCONNECT"),
    CONNECT("CONNECT"),
    MESSAGE("MESSAGE"),
    RECEIPT("RECEIPT"),
    CONNECTED("CONNECTED"),
    ERROR("ERROR"),
    _EMPTY("_EMPTY"),
    _UNKNOWN("_UNKNOWN");

    private String text;

    Command(String text) {
        this.text = text;
    }

    public static Command forName(String value) {

        value = value.trim();

        if("".equals(value)){
            return _EMPTY;
        }

        try{
            return Command.valueOf(value);
        }catch(IllegalArgumentException e){
            return _UNKNOWN;
        }
    }

}
