package org.jaffamq.persistence.database;

import org.jaffamq.persistence.database.sql.JDBCSession;

import java.util.List;

/**
 * Crud interface to be implemented by all repositories with full CRUD operations.
 */
public interface CrudRepository<T> {

    /**
     * Get entity identified by id or null if not found
     *
     * @param session
     * @param id
     * @return
     */
    T get(JDBCSession session, Long id);

    boolean delete(JDBCSession session, Long id);

    Long create(JDBCSession session, T toCreate);

    boolean update(JDBCSession session, T toUpdate);

    List<T> list(JDBCSession session, int limit, int offset);
}
