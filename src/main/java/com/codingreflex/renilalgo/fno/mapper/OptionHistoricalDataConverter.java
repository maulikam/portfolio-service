package com.codingreflex.renilalgo.fno.mapper;

import com.codingreflex.renilalgo.common.KiteUtils;
import com.codingreflex.renilalgo.fno.entity.OptionPortfolioInstruments;
import com.codingreflex.renilalgo.fno.entity.OptionStocksHistoricalData;
import com.zerodhatech.models.HistoricalData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OptionHistoricalDataConverter {

    public static List<OptionStocksHistoricalData> convertToStocksHistoricalDataList(List<HistoricalData> historicalDataList, String intrumentToken) {
        return historicalDataList.stream().map(data -> {
            LocalDateTime timestamp = LocalDateTime.parse(data.timeStamp, KiteUtils.DTF);
            return OptionStocksHistoricalData.builder()
                    .instrumentToken(intrumentToken)
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
