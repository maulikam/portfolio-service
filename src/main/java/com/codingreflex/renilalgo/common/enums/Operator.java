package com.codingreflex.renilalgo.common.enums;

public enum Operator {
    GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL("<="), EQUAL("==");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    // Method to get Operator from symbol
    public static Operator fromSymbol(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("No operator with symbol " + symbol);
    }

    public String getSymbol() {
        return this.symbol;
    }
}

