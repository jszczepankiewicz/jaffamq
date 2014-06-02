package org.torpidomq.webconsole


import org.jaffamq.persistence.database.CalendarUtils
import org.joda.time.DateTime

/**
 * Created by urwisy on 2014-06-02.
 */

case class TestDateTime(datetime: DateTime)

object TestDateTime {
    val A = new DateTime(2014, 4, 20, 15, 38, 2, 884, CalendarUtils.DB_TIMEZONE)
}

case class TestDateTimeAsString(String: DateTime)

object TestDateTimeAsString {
    val A = "2014-04-20T15:38:02.884Z"
}

