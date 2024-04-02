package com.codingreflex.renilalgo.portfolio.mapper;

import com.codingreflex.renilalgo.portfolio.entity.StocksHistoricalData;
import com.zerodhatech.models.HistoricalData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricalDataConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static List<StocksHistoricalData> convertToStocksHistoricalDataList(List<HistoricalData> historicalDataList, Long instrumentToken) {
        return historicalDataList.stream().map(data -> {
            LocalDateTime timestamp = LocalDateTime.parse(data.timeStamp, FORMATTER);
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

