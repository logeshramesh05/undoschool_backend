package com.undoschool.util;

import java.time.Instant;

/**
 * Allen's interval overlap: (start1 < end2) AND (end1 > start2)
 */
public class OverlapUtil {

    private OverlapUtil() {}

    public static boolean overlaps(Instant start1, Instant end1,
                                   Instant start2, Instant end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
