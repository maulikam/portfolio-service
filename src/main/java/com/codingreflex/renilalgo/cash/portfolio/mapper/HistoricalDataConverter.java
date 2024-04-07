package com.codingreflex.renilalgo.cash.portfolio.mapper;

import com.codingreflex.renilalgo.common.KiteUtils;
import com.codingreflex.renilalgo.cash.portfolio.entity.StocksHistoricalData;
import com.zerodhatech.models.HistoricalData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricalDataConverter {


    public static List<StocksHistoricalData> convertToStocksHistoricalDataList(List<HistoricalData> historicalDataList, Long instrumentToken) {
        return historicalDataList.stream().map(data -> {
            LocalDateTime timestamp = LocalDateTime.parse(data.timeStamp, KiteUtils.DTF);
            return StocksHistoricalData.builder()
                    .instrumentToken(instrumentToken)
                    .timestamps(timestamp)
                    .openPrice(data.open)
                    .highPrice(data.high)
                    .lowPrice(data.low)
                    .closePrice(data.close)
                    .volume((double) data.volume)
                    .oi(data.oi)
                    .createdAt(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());
    }
}

