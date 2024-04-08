package com.codingreflex.renilalgo.common.enums;
public enum Interval {
    MINUTE("minute", 60),
    THREE_MINUTE("3minute", 100),
    FIVE_MINUTE("5minute", 100),
    TEN_MINUTE("10minute", 100),
    FIFTEEN_MINUTE("15minute", 200),
    THIRTY_MINUTE("30minute", 200),
    SIXTY_MINUTE("60minute", 400),
    DAY("day", 2000);

    private final String interval;
    private final int allowedDays;

    Interval(String interval, int allowedDays) {
        this.interval = interval;
        this.allowedDays = allowedDays;
    }

    public String getInterval() {
        return interval;
    }

    public int getAllowedDays() {
        return allowedDays;
    }
}


