package org.jaffamq.persistence.database.sql;

import org.jaffamq.persistence.database.DBConst;

/**
 * Created by urwisy on 26.01.14.
 */
public abstract class SelectLongOperation extends SelectOperation<Long> {

    public SelectLongOperation(String name, String statement, Integer... args) {
        super(name, statement, DBConst.LONG_MAPPER, args);
    }

}
