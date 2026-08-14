package com.bloom.bloomschool.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public final class LeaveDayCounter {

    private LeaveDayCounter() {}

    public static boolean isWeekend(LocalDate date) {
        DayOfWeek d = date.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    /** Number of weekdays (Mon-Fri) in the inclusive range, ignoring holidays. */
    public static long countWeekdays(LocalDate from, LocalDate to) {
        return countDays(from, to, true, false, Set.of());
    }

    /**
     * Counts days in the inclusive range {@code [from, to]}, optionally excluding weekends
     * and/or dates present in {@code holidayDates}.
     */
    public static long countDays(LocalDate from, LocalDate to, boolean excludeWeekends, boolean excludeHolidays, Set<LocalDate> holidayDates) {
        long count = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (excludeWeekends && isWeekend(d)) continue;
            if (excludeHolidays && holidayDates.contains(d)) continue;
            count++;
        }
        return count;
    }

    /**
     * Leave-specific day count in the inclusive range {@code [from, to]}: weekdays always count
     * as a full day; Saturday/Sunday are governed by {@code weekendPolicy} (with Saturday
     * possibly consuming only half a day); holiday dates are optionally excluded regardless of
     * weekday/weekend. Result may be fractional (e.g. {@code 4.5}).
     */
    public static double countLeaveDays(LocalDate from, LocalDate to, WeekendCountPolicy weekendPolicy, boolean excludeHolidays, Set<LocalDate> holidayDates) {
        double count = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            if (excludeHolidays && holidayDates.contains(d)) continue;
            DayOfWeek dow = d.getDayOfWeek();
            if (dow == DayOfWeek.SUNDAY) {
                if (weekendPolicy == WeekendCountPolicy.COUNT_FULL) count += 1;
                continue;
            }
            if (dow == DayOfWeek.SATURDAY) {
                if (weekendPolicy == WeekendCountPolicy.COUNT_FULL) count += 1;
                else if (weekendPolicy == WeekendCountPolicy.SATURDAY_HALF_DAY) count += 0.5;
                continue;
            }
            count += 1;
        }
        return count;
    }
}
