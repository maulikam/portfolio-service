package com.codingreflex.renilalgo.fno;

import com.codingreflex.renilalgo.common.KiteUtils;
import com.codingreflex.renilalgo.common.enums.Interval;
import com.codingreflex.renilalgo.fno.entity.OptionInstrumentProcessLog;
import com.codingreflex.renilalgo.fno.entity.OptionPortfolioInstruments;
import com.codingreflex.renilalgo.fno.entity.OptionStocksHistoricalData;
import com.codingreflex.renilalgo.fno.mapper.OptionHistoricalDataConverter;
import com.codingreflex.renilalgo.fno.repository.OptionPortfolioInstrumentRepository;
import com.codingreflex.renilalgo.fno.repository.OptionProcessLogRepository;
import com.codingreflex.renilalgo.fno.repository.OptionStocksHistoricalDataRepository;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class OptionStocksHistoricalDataService {


    @Autowired
    private OptionPortfolioInstrumentRepository portfolioInstrumentRepository;

    @Autowired
    private OptionStocksHistoricalDataRepository historicalDataRepository;


    @Autowired
    private KiteConnect kiteConnect;

    @Autowired
    private OptionProcessLogRepository processLogRepository;

    public void _fetchHistoricalDataForStocks(String startDateStr, String endDateStr, Interval interval) throws ParseException {
        List<String> allDistinctInstrumentTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = formatter.parse(startDateStr);
        Date toDate = formatter.parse(endDateStr);

        log.info("Period: {}, fromDate: {}, toDate: {}", interval.getInterval(), fromDate, toDate);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Adjust batch size dynamically based on the size of instrument tokens list
        final int dynamicBatchSize = Math.max(1, allDistinctInstrumentTokens.size() / 11);

        // Create date batches based on the interval's allowed days
        int allowedDays = interval.getAllowedDays();
        Date batchStartDate = fromDate;
        while (batchStartDate.before(toDate)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(batchStartDate);
            calendar.add(Calendar.DAY_OF_YEAR, allowedDays);
            Date batchEndDate = calendar.getTime();
            if (batchEndDate.after(toDate)) {
                batchEndDate = toDate;
            }

            Date finalBatchStartDate = batchStartDate;
            Date finalBatchEndDate = batchEndDate;

            for (int i = 0; i < allDistinctInstrumentTokens.size(); i += dynamicBatchSize) {
                int end = Math.min(i + dynamicBatchSize, allDistinctInstrumentTokens.size());
                List<String> batch = new ArrayList<>(allDistinctInstrumentTokens.subList(i, end));
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processBatch(batch, finalBatchStartDate, finalBatchEndDate, interval));
                futures.add(future);
            }

            batchStartDate = batchEndDate; // Move to next date batch
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        historicalDataRepository.updateTradingSymbol();
    }

    private void processBatch(List<String> batch, Date fromDate, Date toDate, Interval interval) {
        batch.forEach(instrument -> {
            try {
                log.info("Fetching data for instrument token: {}", instrument);
                HistoricalData data = kiteConnect.getHistoricalData(fromDate, toDate, String.valueOf(instrument), interval.getInterval(), false, false);
                if (data != null && !data.dataArrayList.isEmpty()) {
                    List<OptionStocksHistoricalData> stocksHistoricalDataList = OptionHistoricalDataConverter.convertToStocksHistoricalDataList(data.dataArrayList, instrument);
                    historicalDataRepository.saveAll(stocksHistoricalDataList);
                    log.info("Successfully persisted to database: {} for instrument: {}", stocksHistoricalDataList.size(), instrument);
                    processLogRepository.save(OptionInstrumentProcessLog.builder()
                            .processedCount(stocksHistoricalDataList.size())
                            .instrumentToken(instrument)
                            .isSucessfullyProcessed(true)
                            .errorMessage(null).build());

                } else {
                    log.error("No stocks data found for instrument: " + instrument);
                    processLogRepository.save(OptionInstrumentProcessLog.builder()
                            .processedCount(0)
                            .instrumentToken(instrument)
                            .isSucessfullyProcessed(false)
                            .errorMessage("No stocks data found for instrument: " + instrument).build());
                }
            } catch (KiteException e) {
                log.error("Error while loading stocks historical data for instrument: " + instrument);
                e.printStackTrace();
                processLogRepository.save(OptionInstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrument)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
            } catch (IOException e) {
                log.error("Error while loading stocks historical data for instrument: " + instrument);
                processLogRepository.save(OptionInstrumentProcessLog.builder()
                        .processedCount(0)
                        .instrumentToken(instrument)
                        .isSucessfullyProcessed(false)
                        .errorMessage(e.getMessage()).build());
                e.printStackTrace();
            }
        });
    }


}
