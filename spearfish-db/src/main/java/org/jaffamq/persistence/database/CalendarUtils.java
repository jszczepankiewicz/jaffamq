package org.jaffamq.persistence.database;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Utils for operations on DateTime.
 */
public class CalendarUtils {

    public static final DateTimeZone DB_TIMEZONE = DateTimeZone.UTC;

    private CalendarUtils(){
        //  no instantiation allowed
    }

    public static long nowAsLong(){
        return now().getMillis();
    }

    public static DateTime now(){
        return DateTime.now(DB_TIMEZONE);
    }

    public static Long toLong(DateTime time){

        if(time == null){
            return null;
        }

        return time.getMillis();
    }

    public static DateTime toDateTime(Long time){

        if(time == null){
            return null;
        }

        return new DateTime(time, DB_TIMEZONE);
    }
}
