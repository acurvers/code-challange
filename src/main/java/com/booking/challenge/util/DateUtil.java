package com.booking.challenge.util;

import org.joda.time.DateTime;

public class DateUtil {

    public static DateTime parseDateTime(String dateTimeStr) {
        try {
            return DateTime.parse(dateTimeStr);
        } catch (Exception ex){
            return null;
        }
    }

    public static DateTime parseDateTimeStartDay(String dateTimeStr) {
        final DateTime dateTime = parseDateTime(dateTimeStr);
        return(dateTime==null) ? null : dateTime.withTimeAtStartOfDay();
    }
}
