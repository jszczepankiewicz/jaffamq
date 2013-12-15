package org.jaffamq;


/**
 * Internal (non-recoverable) exception.
 */
public class InternalException extends RuntimeException{

    protected final Errors.Code errorCode;

    protected final String contextMessage;

    protected Throwable cause;

    public InternalException(Errors.Code code, String contextMessage){
        this.errorCode = code;
        this.contextMessage = contextMessage;
    }

    public InternalException(Errors.Code code, Throwable cause, String contextMessage){
        this.errorCode = code;
        this.contextMessage = contextMessage;
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public Errors.Code getErrorCode() {
        return errorCode;
    }

    public String getContextMessage() {
        return contextMessage;
    }
}
