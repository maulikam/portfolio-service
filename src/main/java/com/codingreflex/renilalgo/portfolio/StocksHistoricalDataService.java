package com.codingreflex.renilalgo.portfolio;

import com.codingreflex.renilalgo.common.KiteUtils;
import com.codingreflex.renilalgo.common.enums.Interval;
import com.codingreflex.renilalgo.portfolio.entity.InstrumentProcessLog;
import com.codingreflex.renilalgo.portfolio.entity.StocksHistoricalData;
import com.codingreflex.renilalgo.portfolio.mapper.HistoricalDataConverter;
import com.codingreflex.renilalgo.portfolio.repository.PortfolioInstrumentRepository;
import com.codingreflex.renilalgo.portfolio.repository.ProcessLogRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ProcessLogRepository processLogRepository;


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
            HistoricalData data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrumentToken), Interval.DAY.getInterval(), false, false);
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

    public void fetchHistoricalDataForStocks(String startDateStr, String endDateStr, Long instrumentToken, Interval interval) throws IOException, KiteException, ParseException {
        Date fromDate = KiteUtils.SDF.parse(startDateStr);
        Date toDate = KiteUtils.SDF.parse(endDateStr);

        log.info("Fetching data for period: " + interval.getInterval() + ", fromDate: " + fromDate + ", toDate: " + toDate + ", instrumentToken: " + instrumentToken);

        HistoricalData data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrumentToken), interval.getInterval(), false, false);
        if (data != null && data.dataArrayList.size() > 0) {
            List<StocksHistoricalData> stocksHistoricalDataList =
                    HistoricalDataConverter.convertToStocksHistoricalDataList(data.dataArrayList, instrumentToken);

            historicalDataRepository.saveAll(stocksHistoricalDataList);
            log.info("Successfully persisted to database: " + stocksHistoricalDataList.size() + " for instrumentToken: " + instrumentToken);
        } else {
            log.error("No stocks data found for instrumentToken: " + instrumentToken);
        }
    }

    public void fetchHistoricalDataForStocks(String startDateStr, String endDateStr, Interval interval) throws ParseException {
        List<Long> allDistinctInstrumentTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = formatter.parse(startDateStr);
        Date toDate = formatter.parse(endDateStr);

        log.info("Period: DAY, fromDate: " + fromDate + ", toDate: " + toDate);

        for (Long instrumentToken : allDistinctInstrumentTokens) {
            log.info("Fetching data for instrument token: " + instrumentToken);
            HistoricalData data = null;
            try {
                data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrumentToken), interval.getInterval(), false, false);
                if (data != null && data.dataArrayList.size() > 0) {
                    List<StocksHistoricalData> stocksHistoricalDataList =
                            HistoricalDataConverter.convertToStocksHistoricalDataList(data.dataArrayList, instrumentToken);

                    historicalDataRepository.saveAll(stocksHistoricalDataList);
                    log.info("Successfully persisted to database: " + stocksHistoricalDataList.size() + " for instrumentToken: " + instrumentToken);

                    processLogRepository.save(InstrumentProcessLog.builder()
                            .processedCount(stocksHistoricalDataList.size())
                            .instrumentToken(instrumentToken)
                            .isSucessfullyProcessed(true)
                            .errorMessage(null).build());

                } else {
                    log.error("No stocks data found for instrumentToken: " + instrumentToken);
                    processLogRepository.save(InstrumentProcessLog.builder()
                            .processedCount(0)
                            .instrumentToken(instrumentToken)
                            .isSucessfullyProcessed(false)
                            .errorMessage("No stocks data found for instrumentToken: " + instrumentToken).build());
                }
            } catch (KiteException e) {
                log.error("Error while loading stocks historical data for instrumentToken: " + instrumentToken);
                e.printStackTrace();
                processLogRepository.save(InstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrumentToken)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
            } catch (IOException e) {
                log.error("Error while loading stocks historical data for instrumentToken: " + instrumentToken);
                processLogRepository.save(InstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrumentToken)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
                e.printStackTrace();
            }

        }
    }


    private static final int BATCH_SIZE = 230;

    public void _fetchHistoricalDataForStocks(String startDateStr, String endDateStr, Interval interval) throws ParseException {
        List<Long> allDistinctInstrumentTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = formatter.parse(startDateStr);
        Date toDate = formatter.parse(endDateStr);

        log.info("Period: DAY, fromDate: {}, toDate: {}", fromDate, toDate);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < allDistinctInstrumentTokens.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, allDistinctInstrumentTokens.size());
            List<Long> batch = new ArrayList<>(allDistinctInstrumentTokens.subList(i, end));
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processBatch(batch, fromDate, toDate, interval));
            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processBatch(List<Long> batch, Date fromDate, Date toDate, Interval interval) {
        batch.forEach(instrumentToken -> {
            try {
                log.info("Fetching data for instrument token: {}", instrumentToken);
                HistoricalData data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrumentToken), interval.getInterval(), false, false);
                if (data != null && !data.dataArrayList.isEmpty()) {
                    List<StocksHistoricalData> stocksHistoricalDataList = HistoricalDataConverter.convertToStocksHistoricalDataList(data.dataArrayList, instrumentToken);
                    historicalDataRepository.saveAll(stocksHistoricalDataList);
                    log.info("Successfully persisted to database: {} for instrumentToken: {}", stocksHistoricalDataList.size(), instrumentToken);
                    processLogRepository.save(InstrumentProcessLog.builder()
                            .processedCount(stocksHistoricalDataList.size())
                            .instrumentToken(instrumentToken)
                            .isSucessfullyProcessed(true)
                            .errorMessage(null).build());

                } else {
                    log.error("No stocks data found for instrumentToken: " + instrumentToken);
                    processLogRepository.save(InstrumentProcessLog.builder()
                            .processedCount(0)
                            .instrumentToken(instrumentToken)
                            .isSucessfullyProcessed(false)
                            .errorMessage("No stocks data found for instrumentToken: " + instrumentToken).build());
                }
            } catch (KiteException e) {
                log.error("Error while loading stocks historical data for instrumentToken: " + instrumentToken);
                e.printStackTrace();
                processLogRepository.save(InstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrumentToken)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
            } catch (IOException e) {
                log.error("Error while loading stocks historical data for instrumentToken: " + instrumentToken);
                processLogRepository.save(InstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrumentToken)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
                e.printStackTrace();
            }
        });
    }


}
