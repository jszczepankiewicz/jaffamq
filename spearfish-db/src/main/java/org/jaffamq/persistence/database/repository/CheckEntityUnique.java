package org.jaffamq.persistence.database.repository;

import org.jaffamq.persistence.database.sql.JDBCSession;

/**
 * Created by urwisy on 2014-04-21.
 */
public interface CheckEntityUnique {
    boolean isUnique(JDBCSession session, String name);
}
