package org.jaffamq.persistence;

/**
 * Interface for unique identifier of message that can be persisted. It is more like marker interface because depending on the
 * physical persistence layer implementing classes will expose implementation specific identifiers.
 */
public interface PersistedMessageId {
}
