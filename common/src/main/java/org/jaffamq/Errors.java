package org.jaffamq;

/**
 * Stores all error codes.
 */
public class Errors {

    public static class Code {
        private final String id;
        private final String description;
        private final String cause;
        private final String action;

        public Code(String id, String description, String cause, String action) {
            this.id = id;
            this.description = description;
            this.cause = cause;
            this.action = action;
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getCause() {
            return cause;
        }

        public String getAction() {
            return action;
        }
    }

    //  client frame validation related error codes
    public static final Code HEADERS_MISSING_DESTINATION = new Code("STM-10001", "Missing [destination] header", "SEND / SUBSCRIBE / MESSAGE requires destination header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_SUBSCRIPTION_ID = new Code("STM-10002", "Missing [id] header", "SUBSCRIBE / UNSUBSCRIBE frame requires id header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_ACCEPT_VERSION = new Code("STM-10003", "Missing [accept-version] header", "CONNECT / STOMP frame requires accept-version header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_HOST = new Code("STM-10004", "Missing [host] header", "CONNECT / STOMP frame requires host header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code UNSUPPORTED_DESTINATION_TYPE = new Code("STM-20001", "Unsupported destination type", "Supported destination types are /queue/... and /topic/...", "Request to server was invalid due to invalid header. Please check if you are using proper STOMP client.");
    public static final Code INVALID_DESTINATION_NAME = new Code("STM-20002", "Invalid destination name", "Valid destination names should have at least one character after destination type", "Request to server was invalid due to invalid header. Please check if you are using proper STOMP client.");
}