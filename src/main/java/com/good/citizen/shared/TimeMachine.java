package com.good.citizen.shared;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeMachine {

    private static Clock clock = Clock.systemUTC();
    private static final ZoneId zoneId = ZoneId.of("UTC");

    private TimeMachine() {
    }

    public static LocalDate now() {
        return LocalDateTime.now(getClock()).toLocalDate();
    }

    public static LocalDate infiniteFuture() {
        return LocalDate.of(9999,12,31);
    }
    public static LocalDate infinitePast() {
        return LocalDate.of(1970,01,01);
    }

    public static LocalDateTime nowLocalDateAndTime() {
        return LocalDateTime.now(getClock());
    }

    public static void useFixedClockAt(LocalDateTime date) {
        clock = Clock.fixed(date.atOffset(ZoneOffset.UTC).toInstant(), zoneId);
    }

    private static Clock getClock() {
        return clock;
    }

    // If period contains more than 0 days, adds additional month
    public static Integer getNumberOfMonthsBetweenDates(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);

        return period.getYears() * 12 + period.getMonths() + (period.getDays() > 0 ? 1 : 0);
    }
}