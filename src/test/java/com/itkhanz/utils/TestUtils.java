package com.itkhanz.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {
    /*
    This is a thread-safe way of getting and formatting DateTime
    This works with JDK 8 and above and comes with java.time API
    https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html
    https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
     */
    public static String getFormattedDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }
}
