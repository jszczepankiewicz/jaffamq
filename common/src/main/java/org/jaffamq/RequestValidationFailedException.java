package org.jaffamq;

/**
 * Thrown when the client frame validation failed, i.e. missing headers.
 */
public class RequestValidationFailedException extends Exception {

    private final Errors.Code errorCode;

    public RequestValidationFailedException(Errors.Code code){
        this.errorCode = code;
    }

    public Errors.Code getErrorCode() {
        return errorCode;
    }


}
