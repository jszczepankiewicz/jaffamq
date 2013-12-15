package org.jaffamq;

/**
 * Utilities helpfull for parsing/generation Stomp frame.
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

}
