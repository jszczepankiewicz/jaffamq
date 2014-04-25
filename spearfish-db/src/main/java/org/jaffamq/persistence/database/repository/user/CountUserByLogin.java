package org.jaffamq.persistence.database.repository.user;

import org.jaffamq.persistence.database.repository.mappings.SelectLongOperation;

import static java.sql.Types.VARCHAR;

/**
 * Created by urwisy on 26.01.14.
 */
public class CountUserByLogin extends SelectLongOperation {

    public CountUserByLogin() {
        super("SELECT COUNT(*) FROM security_user WHERE login=?", VARCHAR);
    }
}
