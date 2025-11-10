package highwaystar.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DATE_FORMATTER);
    }

    public static String formatDateTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DATETIME_FORMATTER);
    }

    public static String formatDisplayDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DISPLAY_FORMATTER);
    }

    public static String getToday() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        LocalDate date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate();
        return date1.equals(date2);
    }

    public static int daysBetween(long timestamp1, long timestamp2) {
        LocalDate date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneId.systemDefault()).toLocalDate();
        return Math.abs((int) java.time.temporal.ChronoUnit.DAYS.between(date1, date2));
    }

    public static long getStartOfDay(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public static long getEndOfDay(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
