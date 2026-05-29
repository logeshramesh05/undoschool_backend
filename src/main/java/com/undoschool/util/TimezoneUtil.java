package com.undoschool.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * All timezone conversion logic lives here.
 * Rule: store UTC (Instant), convert only at the edges.
 */
public class TimezoneUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private TimezoneUtil() {}

    /**
     * Teacher sends "2025-06-07T18:00:00" + "Asia/Kolkata"
     * Returns UTC Instant to persist.
     */
    public static Instant toUtc(String localDateTime, String ianaTimezone) {
        LocalDateTime ldt = LocalDateTime.parse(localDateTime);
        ZoneId zone = ZoneId.of(ianaTimezone);
        return ldt.atZone(zone).toInstant();
    }

    /**
     * Convert stored UTC Instant to viewer's local timezone string.
     * Returns ISO offset format: "2025-06-07T08:30:00-04:00"
     */
    public static String toLocalString(Instant utcInstant, String ianaTimezone) {
        ZoneId zone = ZoneId.of(ianaTimezone);
        ZonedDateTime zdt = utcInstant.atZone(zone);
        return FORMATTER.format(zdt);
    }
}
