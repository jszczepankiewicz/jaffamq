package org.jaffamq.persistence.database.repository.mappings;

import org.jaffamq.persistence.database.sql.SelectOperation;

import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 19.01.14.
 */
public class SelectNextId extends SelectOperation<Long>{

    public SelectNextId() {
        super("SelectNextId", "SELECT NEXTVAL(?)", VARCHAR);
    }

    @Override
    protected Long mapResult(ResultSet rs, int rowNumber) throws SQLException {
        return rs.getLong(1);
    }
}
