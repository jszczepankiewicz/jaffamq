package org.jaffamq.persistence.database.sql;

import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 19.01.14.
 */
public class SelectNextId extends SelectLongOperation {

    public SelectNextId() {
        super("SelectNextId", "SELECT NEXTVAL(?)", VARCHAR);
    }
}
