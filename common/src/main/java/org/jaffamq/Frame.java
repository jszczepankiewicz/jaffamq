package org.jaffamq;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 17.08.13
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class Frame {

    public static String encodeHeaderValue(String value){

        //  in most cases retval will have same length as input (no characters to escape)
        StringBuilder retval = new StringBuilder(value.length());

        for (char c : value.toCharArray()) {
            switch(c){
                case '\\':
                    retval.append("\\\\");
                    break;
                case '\n':
                    retval.append("\\n");
                    break;
                case '\r':
                    retval.append("\\r");
                    break;
                case ':':
                    retval.append("\\c");
                    break;
                default:
                    retval.append(c);
                    break;
            }
        }

        return retval.toString();
    }

    /**
     * \r (octet 92 and 114) translates to carriage return (octet 13)
     * \n (octet 92 and 110) translates to line feed (octet 10)
     * \c (octet 92 and 99) translates to : (octet 58)
     * \\ (octet 92 and 92) translates to \ (octet 92)
     *
     * @param value
     * @return
     */
    public static String decodeHeaderValue(String value){

        //  in most cases retval will have same length as input (no characters to unescape)
        StringBuilder retval = new StringBuilder(value.length());
        boolean escapingStarted = false;

        for (char c : value.toCharArray()) {

            if(escapingStarted){

                switch(c){
                    case '\\':
                        retval.append('\\');
                        break;
                    case 'c':
                        retval.append(':');
                        break;
                    case 'n':
                        retval.append('\n');
                        break;
                    case 'r':
                        retval.append('\r');
                        break;
                    default:
                        retval.append('\\');
                        retval.append(c);
                        break;
                }

                escapingStarted = false;
            }
            else{
                if(c == '\\'){
                    escapingStarted = true;
                }
                else{
                    retval.append(c);
                }
            }
        }

        return retval.toString();
    }

    public enum FrameParsingState {
        READING_COMMAND,
        READING_HEADERS,
        READING_BODY,
        FINISHED_PARSING
    }

    public enum Command {

        //  to-server
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





}
