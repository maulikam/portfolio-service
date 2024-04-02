package com.codingreflex.renilalgo.common.enums;

public enum Index {
    NIFTY_BANK("NSE:NIFTY BANK", 100, "BANKNIFTY", 15),
    NIFTY_50("NSE:NIFTY 50", 50, "NIFTY", 50),
    NIFTY_FIN_SERVICE("NSE:NIFTY FIN SERVICE", 50, "FINNIFTY", 40),
    NIFTY_MID_SELECT("NSE:NIFTY MID SELECT", 25, "MIDCPNIFTY", 75);

    private final String indexName;
    private final int strikeGap;
    private final String symbolPrefix;

    private final int lotSize;

    Index(String indexName, int strikeGap, String symbolPrefix, int lotSize) {
        this.indexName = indexName;
        this.strikeGap = strikeGap;
        this.symbolPrefix = symbolPrefix;
        this.lotSize = lotSize;
    }

    public String getIndexName() {
        return indexName;
    }

    public int getStrikeGap() {
        return strikeGap;
    }

    public String getSymbolPrefix() {
        return symbolPrefix;
    }

    public int getLotSize() {
        return lotSize;
    }
}
