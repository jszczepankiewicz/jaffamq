package org.jaffamq.persistence.database;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;

/**
 * Created by urwisy on 2014-04-20.
 */
public class CalendarUtilsTest {

    public static final long TARGET_DATE_AS_LONG = 1398008282884l;
    public static final DateTime TARGET_DATE = new DateTime(2014,4,20,15,38,2,884, CalendarUtils.DB_TIMEZONE);

    @Test
    public void shouldReturnNow() {

        //  when
        DateTime now = CalendarUtils.now();

        //  then
        assertThat(now.getYear(), is(greaterThan(2013)));

    }

    @Test
    public void shouldConvertFromLong() {

        //  when
        DateTime converted = CalendarUtils.toDateTime(TARGET_DATE_AS_LONG);

        //  then
        assertThat(converted, is(equalTo(TARGET_DATE)));

    }

    @Test
    public void shouldConvertFromDateTime() {

        //  when
        Long converted = CalendarUtils.asLong(TARGET_DATE);

        //  then
        assertThat(converted, is(equalTo(TARGET_DATE_AS_LONG)));


    }

    @Test
    public void shouldConvertToNullFromNulledInput(){

        //  when
        Long convertedLong = CalendarUtils.asLong(null);
        DateTime convertedTime = CalendarUtils.toDateTime(null);

        //  then
        assertThat(convertedLong, is(nullValue()));
        assertThat(convertedTime, is(nullValue()));
    }


}



