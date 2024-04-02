package com.codingreflex.renilalgo.portfolio;

import com.codingreflex.renilalgo.config.KiteProperties;
import com.codingreflex.renilalgo.connect.KiteConnectService;
import com.codingreflex.renilalgo.portfolio.repository.PortfolioInstrumentRepository;
import com.codingreflex.renilalgo.portfolio.repository.StocksHistoricalDataRepository;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class StocksHistoricalDataService {

    @Autowired
    private KiteConnectService kiteConnectService;

    @Autowired
    private PortfolioInstrumentRepository portfolioInstrumentRepository;

    @Autowired
    private StocksHistoricalDataRepository historicalDataRepository;

    @Autowired
    KiteProperties kiteProperties;

    @Autowired
    private ConfigurationService configurationService;


    private static final String URL = "https://api.kite.trade/instruments/historical/14286850/minute?from=2023-12-20+09:15:00&to=2024-01-20+09:15:59";


    public void fetchHistoricalDataForStocks(int year, int month, int day, String period) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Kite-Version", "3");
        headers.add("Authorization", "token " + kiteProperties.getApiKey() + ":" + configurationService.getRefreshToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);

        System.out.println("Response: " + response.getBody());

//        List<Long> allDistinctInstrumentTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
//
//        ZoneOffset offset = ZoneOffset.of("+05:30");
//        long token = 4331777;
//        OffsetDateTime from = OffsetDateTime.of(
//                LocalDateTime.of(year, month, day, 9, 15),
//                offset
//        );
//
//        OffsetDateTime to = OffsetDateTime.now(offset);
//        // Dates for historical data
//        Date fromDate = new Date(from.toInstant().toEpochMilli());
//        Date toDate = new Date(from.toInstant().toEpochMilli());
//
//        log.info("Period :" + period + ", fromDate: " + fromDate + ", toDate: " + toDate);
//
//        for (Long instrumentToken : allDistinctInstrumentTokens) {
////            System.out.println(String.format(" instrument token: %s", instrumentToken));
//            if (instrumentToken != 4331777) continue;
//
//            try {
//                HistoricalData data = kiteConnectService.getKiteConnect().getHistoricalData(fromDate, toDate, String.valueOf(token), period, false, false);
//                List<HistoricalData> dataList = data.dataArrayList;
//                int num = dataList.size();
//
//                System.out.println("NUmber " + num);
//                System.out.println("First: " + dataList.get(0).timeStamp + " \n last: " + dataList.get(num - 1).timeStamp + " :::: ");
//
////                dataPairs.add(new HistoricalDataTokenPair(data, instrumentToken));
////                System.out.println("+++++===>>>>" + dataPairs.size());
//            } catch (KiteException | IOException e) {
//                e.printStackTrace(); // Handle exceptions appropriately
//            }
//        }


    }


}
