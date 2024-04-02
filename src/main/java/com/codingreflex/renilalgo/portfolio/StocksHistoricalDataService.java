package com.codingreflex.renilalgo.portfolio;

import com.codingreflex.renilalgo.portfolio.entity.StocksHistoricalData;
import com.codingreflex.renilalgo.portfolio.mapper.HistoricalDataConverter;
import com.codingreflex.renilalgo.portfolio.repository.PortfolioInstrumentRepository;
import com.codingreflex.renilalgo.portfolio.repository.StocksHistoricalDataRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.util.Date;
import java.util.List;

import static com.codingreflex.renilalgo.common.enums.Interval.DAY;


@Service
@Slf4j
public class StocksHistoricalDataService {


    @Autowired
    private PortfolioInstrumentRepository portfolioInstrumentRepository;

    @Autowired
    private StocksHistoricalDataRepository historicalDataRepository;


    @Autowired
    private KiteConnect kiteConnect;


    public void fetchHistoricalDataForStocks() throws IOException, KiteException {


        List<Long> allDistinctInstrumentTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate =  new Date();
        Date toDate = new Date();
        try {
            fromDate = formatter.parse("2019-09-20 09:15:00");
            toDate = formatter.parse("2024-04-01 15:30:00");
        }catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("Period :" + DAY + ", fromDate: " + fromDate + ", toDate: " + toDate);

        for (Long instrumentToken : allDistinctInstrumentTokens) {
            System.out.println(String.format(" instrument token: %s", instrumentToken));
            HistoricalData data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrumentToken), DAY.getInterval(), false, false);
            if (data != null) {
                List<HistoricalData> dataList = data.dataArrayList;
                int num = dataList.size();

                System.out.println("NUmber " + num);
                if (dataList.size() > 0) {
                    List<StocksHistoricalData> stocksHistoricalDataList =
                            HistoricalDataConverter.convertToStocksHistoricalDataList(dataList, instrumentToken);

                    historicalDataRepository.saveAll(stocksHistoricalDataList);
                    log.info("Successfully persisted to database :" + stocksHistoricalDataList.size() + " for instrumentToken :" + instrumentToken);
                } else {
                    log.error("No stocks data found for instrumentToken :" + instrumentToken);
                }
            } else {
                log.error("No stocks data found for instrumentToken :" + instrumentToken);
            }

        }


    }


}
