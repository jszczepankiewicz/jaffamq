package org.jaffamq;

/**
 * Stores all error codes.
 */
public class Errors {

    private Errors(){

    }

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

        @Override
        public String toString(){
            return "[Error : " + id + "]";
        }
    }

    //  client frame validation related error codes
    public static final Code HEADERS_MISSING_DESTINATION = new Code("STM-10001", "Missing [destination] header", "SEND / SUBSCRIBE / MESSAGE requires destination header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_SUBSCRIPTION_ID = new Code("STM-10002", "Missing [id] header", "SUBSCRIBE / UNSUBSCRIBE frame requires id header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_ACCEPT_VERSION = new Code("STM-10003", "Missing [accept-version] header", "CONNECT / STOMP frame requires accept-version header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_HOST = new Code("STM-10004", "Missing [host] header", "CONNECT / STOMP frame requires host header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code HEADERS_MISSING_TRANSACTION = new Code("STM-1005", "Missing [transaction] header", "BEGIN / COMMIT / ABORT frames require transaction header", "Request to server was invalid due to missing header. Please check if you are using proper STOMP client.");
    public static final Code EMPTY_FRAME = new Code("STM-1006", "Empty frame not allowed", "Every frame should contain at least command and body", "Empty frame provided. Please check if you are using proper STOMP client.");
    public static final Code UNSUPPORTED_FRAME = new Code("STM-1007", "Unsupported command", "Unrecognized command provided", "Transaction to send message with has empty name. Please check if you are using proper STOMP client.");
    public static final Code UNSUPPORTED_PROTOCOL_VERSION = new Code("STM-1008", "Unsupported protocol version", "This broker supports only Stomp 1.2 clients", "Client requested different version than supported by this broker. Please use client compatible with Stomp 1.2");
    public static final Code UNSUPPORTED_DESTINATION_TYPE = new Code("STM-2001", "Unsupported destination type", "Supported destination types are /queue/... and /topic/...", "Request to server was invalid due to invalid header. Please check if you are using proper STOMP client.");
    public static final Code INVALID_DESTINATION_NAME = new Code("STM-2002", "Invalid destination name", "Valid destination names should have at least one character after destination type", "Request to server was invalid due to invalid header. Please check if you are using proper STOMP client.");
    public static final Code TRANSACTION_ALREADY_BEGUN = new Code("STM-2003", "Transaction already begun", "Transaction with specified name already begun in this session", "Transaction can only be started once per session. Please check if you are using proper STOMP client.");
    public static final Code UNKNOWN_TRANSACTION_TO_COMMIT = new Code("STM-2004", "Unknown transaction to commit", "Transaction with specified name to commit not known in this session", "Transaction to commit with specified name not known to this session. Please check if it was started.");
    public static final Code UNKNOWN_TRANSACTION_TO_ABORT = new Code("STM-2005", "Unknown transaction to abort", "Transaction with specified name to abort not known in this session", "Transaction to abort with specified name not known to this session. Please check if it was started.");
    public static final Code TRANSACTION_ALREADY_ABORTED = new Code("STM-2006", "Transaction already aborted", "Transaction with specified name was already aborted in this session", "Transaction to abort with specified name was already aborted in this session.");
    public static final Code TRANSACTION_ALREADY_COMMITED = new Code("STM-2007", "Transaction already commited", "Transaction with specified name was already commited in this session", "Transaction to commit with specified name was already commited in this session.");
    public static final Code TRANSACTION_TO_COMMIT_ALREADY_ABORTED = new Code("STM-2008", "Attempt to commit aborted transaction", "Transaction to commit with specified name was already aborted in this session", "Transaction to commit with specified name was already aborted in this session. Please check if you are using proper STOMP client.");
    public static final Code TRANSACTION_TO_ROLLBACK_ALREADY_COMMITED = new Code("STM-2009", "Attempt to rollback commited transaction", "Transaction to abort with specified name was already commited in this session", "Transaction to abort with specified name was already commited in this session. Please check if you are using proper STOMP client.");
    public static final Code TRANSACTION_PROVIDED_WITH_EMPTY_NAME = new Code("STM-2010", "Transaction can not have empty name", "Transaction provided with empty name", "Transaction to send message with has empty name. Please check if you are using proper STOMP client.");

    //  unconsumed messages related problems
    public static final Code UNCONSUMED_MESSAGE_PERSIST_TIMEOUT = new Code("STM-3001", "Persist unconsumed operation timeout", "Timeout occurred for persist unconsumed message operation", "Timeout occurred for persisting unconsumed message operation. For stability reasons operation was aborted. Please check log files for details. It might be necessarily to tune configuration to level appropriate for available hardware");
    public static final Code UNCOSUMEND_MESSAGE_PERSIST_UNEXPECTED_ERROR = new Code("STM-3002", "Error persisting unconsumed message", "Unexpected exception occurred while persisting unconsumed message", "Please check log for details.");
    public static final Code UNCONSUMED_MESSAGE_POLL_TIMEOUT = new Code("STM-3003", "Poll unconsumed operation timeout", "Timeout occurred for poll unconsumed message operation", "Timeout occurred for polling unconsumed message operation. For stability reasons operation was aborted. Please check log files for details. It might be necessarily to tune configuration to level appropriate for available hardware");
    public static final Code UNCONSUMED_MESSAGE_POLL_UNEXPECTED_ERROR = new Code("STM-3002", "Error polling unconsumed message", "Unexpected exception occured while polling unconsumed message", "Please check log for details.");

    //  sql persistence layer messages
    public static final Code PREPARED_STATEMENT_CREATION_EXCEPTION = new Code ("STM-4001", "SQLException while creating PreparedStatement", "", "");
    public static final Code PREPARED_STATEMENT_EXECUTE_WITHOUT_PARAMETERS_PASSED = new Code("STM-4002", "SQL Statement requires some parameters but none given to execute method", "", "");
    public static final Code SQL_EXECUTE_QUERY_FAILED = new Code("STM-4003", "General SQLException while executing sql", "", "");
    public static final Code PREPARED_STATEMENT_PARAMETERS_LENGTH_NOT_EQUAL = new Code("STM-4004", "Passed different number of parameters than declared in sql query for prepared statement", "", "");
    public static final Code SQL_EXCEPTION_ON_LOOPING_RESULT_SET = new Code("STM-4005", "SQLException during resultSet.next()", "", "");
    public static final Code SQL_EXCEPTION_WHILE_SET_VALUE_ON_STATEMENT = new Code("STM-4006", "SQLException on set value on statement", "", "");
    public static final Code SQL_EXCEPTION_ON_EXECUTE_UPDATE = new Code("STM-4007", "SQLException on executeUpdate", "", "");


}
