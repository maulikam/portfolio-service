package com.codingreflex.renilalgo.common.enums;

public enum Interval {
    MINUTE("minute"),
    DAY("day"),
    THREE_MINUTE("3minute"),
    FIVE_MINUTE("5minute"),
    TEN_MINUTE("10minute"),
    FIFTEEN_MINUTE("15minute"),
    THIRTY_MINUTE("30minute"),
    SIXTY_MINUTE("60minute");

    private final String interval;

    Interval(String interval) {
        this.interval = interval;
    }

    public String getInterval() {
        return interval;
    }
}

