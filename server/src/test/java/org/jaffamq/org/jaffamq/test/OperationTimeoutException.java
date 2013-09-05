package org.jaffamq.org.jaffamq.test;

/**
 * Thrown when some operation took more time than required / expected.
 */
public class OperationTimeoutException extends RuntimeException{

    public OperationTimeoutException(String message){
        super(message);
    }
}
