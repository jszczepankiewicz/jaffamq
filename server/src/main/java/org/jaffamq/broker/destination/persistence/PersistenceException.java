package org.jaffamq.broker.destination.persistence;

import org.jaffamq.Errors;
import org.jaffamq.InternalException;

/**
 * Non-recoverable exception related to persisted operations.
 */
public class PersistenceException extends InternalException{

    public PersistenceException(Errors.Code code, String contextMessage) {
        super(code, contextMessage);
    }

    public PersistenceException(Errors.Code code, Throwable cause, String contextMessage) {
        super(code, cause, contextMessage);
    }
}
