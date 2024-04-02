package com.codingreflex.renilalgo.common.enums;

public enum TradeAction {
    BUY("Buy"),
    SELL("Sell");

    private final String action;

    TradeAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return this.action;
    }
}

