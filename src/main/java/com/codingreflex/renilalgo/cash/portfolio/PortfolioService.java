package com.codingreflex.renilalgo.cash.portfolio;


import com.codingreflex.renilalgo.common.KiteUtils;
import com.codingreflex.renilalgo.common.config.TokenRefreshChecker;
import com.codingreflex.renilalgo.cash.portfolio.entity.MergedDf;
import com.codingreflex.renilalgo.cash.portfolio.entity.PortfolioInstruments;
import com.codingreflex.renilalgo.cash.portfolio.repository.MergedDfRepository;
import com.codingreflex.renilalgo.cash.portfolio.repository.PortfolioInstrumentRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Quote;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class PortfolioService {


    private final PortfolioInstrumentRepository portfolioInstrumentRepository;

    private final StocksHistoricalDataService stocksHistoricalDataService;

    private final KiteConnect kiteConnect;

    private final TokenRefreshChecker tokenRefreshChecker;

    private final MergedDfRepository mergedDfRepository;

    final List<String> indexTradingSymbols = Arrays.asList("INDIA VIX", "NIFTY 50", "NIFTY 500",
            "NIFTY AUTO", "NIFTY BANK",
            "NIFTY COMMODITIES", "NIFTY CONSR DURBL", "NIFTY CONSUMPTION",
            "NIFTY CPSE", "NIFTY ENERGY",
            "NIFTY FMCG",
            "NIFTY HEALTHCARE", "NIFTY INDIA MFG",
            "NIFTY IT",
            "NIFTY MEDIA", "NIFTY METAL", "NIFTY MICROCAP250",
            "NIFTY PHARMA", "NIFTY PSE", "NIFTY PSU BANK", "NIFTY PVT BANK",
            "NIFTY REALTY", "NIFTY SMLCAP 100");


    @Autowired
    public PortfolioService(PortfolioInstrumentRepository portfolioInstrumentRepository,
                            MergedDfRepository mergedDfRepository,
                            StocksHistoricalDataService stocksHistoricalDataService,
                            KiteConnect kiteConnect,
                            TokenRefreshChecker tokenRefreshChecker) {
        this.portfolioInstrumentRepository = portfolioInstrumentRepository;
        this.stocksHistoricalDataService = stocksHistoricalDataService;
        this.kiteConnect = kiteConnect;
        this.tokenRefreshChecker = tokenRefreshChecker;
        this.mergedDfRepository = mergedDfRepository;
    }


    public List<Long> getPortfolioInstrumentsTokens() {
        return portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
    }

    public List<MergedDf> getDayEndData(String[] instruments) throws IOException, KiteException {
        if (instruments == null || instruments.length == 0) {
            List<Long> instrumentsTokens = portfolioInstrumentRepository.findAllDistinctInstrumentTokens();
            instruments = instrumentsTokens.stream()
                    .map(String::valueOf)
                    .toArray(String[]::new);
        }
        // Assuming 'instruments' is a non-null array of Strings
        final int batchSize = 500;
        Map<String, Quote> quotes = new ConcurrentHashMap<>(); // Use ConcurrentHashMap if processing in parallel

        // Use parallel stream to process batches concurrently if suitable
        String[] finalInstruments = instruments;
        IntStream.range(0, (instruments.length + batchSize - 1) / batchSize)
                .parallel() // Comment this out if parallel processing is not desired
                .forEach(index -> {
                    int start = index * batchSize;
                    int end = Math.min(start + batchSize, finalInstruments.length);
                    String[] batch = Arrays.copyOfRange(finalInstruments, start, end);
                    try {
                        Map<String, Quote> batchQuotes = kiteConnect.getQuote(batch);
                        quotes.putAll(batchQuotes);
                    } catch (Exception | KiteException e) {
                        // Handle the exception (log it, throw it, or continue processing other batches)
                        System.err.println("Error processing batch " + index + ": " + e.getMessage());
                    }
                });

        System.out.println("Total number of indexes in use " + indexTradingSymbols.size() );
        List<PortfolioInstruments> indexesIntrumentsbyTradingSymbolIn = portfolioInstrumentRepository.findByTradingSymbolIn(indexTradingSymbols);
        Map<String, String> mapIndexTokenToSymbol = new HashMap<String, String>();

        for (PortfolioInstruments index : indexesIntrumentsbyTradingSymbolIn) {
            mapIndexTokenToSymbol.put(String.valueOf(index.getInstrumentToken()), index.getTradingSymbol());
        }

        System.out.println("Total number of indexes in use found in db for token " + indexesIntrumentsbyTradingSymbolIn.size());

        Map<String, Quote> indexesQuotes = kiteConnect.getQuote(mapIndexTokenToSymbol.keySet().toArray(new String[0]));

        Map<String, Double> indexSymbolToClosePrice = indexesQuotes.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> mapIndexTokenToSymbol.get(entry.getKey()),
                        entry -> entry.getValue().ohlc.close
                ));

        List<MergedDf> mergedDfList = quotes.entrySet().stream()
                .map(entry -> {
                    return MergedDf.builder().closePrice(entry.getValue().ohlc.close)
                            .highPrice(entry.getValue().ohlc.high)
                            .lowPrice(entry.getValue().ohlc.low)
                            .openPrice(entry.getValue().ohlc.open)
                            .timestamps(KiteUtils.SDF.format(entry.getValue().timestamp))
                            .volume(Math.round(entry.getValue().volumeTradedToday))
                            .indiaVixClose(indexSymbolToClosePrice.get("INDIA VIX"))
                            .nifty50Close(indexSymbolToClosePrice.get("NIFTY 50"))
                            .nifty500Close(indexSymbolToClosePrice.get("NIFTY 500"))
                            .niftyAutoClose(indexSymbolToClosePrice.get("NIFTY AUTO"))
                            .niftyBankClose(indexSymbolToClosePrice.get("NIFTY BANK"))
                            .niftyCommoditiesClose(indexSymbolToClosePrice.get("NIFTY COMMODITIES"))
                            .niftyConsuDurblClose(indexSymbolToClosePrice.get("NIFTY CONSR DURBL"))
                            .niftyConsumptionClose(indexSymbolToClosePrice.get("NIFTY CONSUMPTION"))
                            .niftyCpseClose(indexSymbolToClosePrice.get("NIFTY CPSE"))
                            .niftyEnergyClose(indexSymbolToClosePrice.get("NIFTY ENERGY"))
                            .niftyFmcgClose(indexSymbolToClosePrice.get("NIFTY FMCG"))
                            .niftyHealthcareClose(indexSymbolToClosePrice.get("NIFTY HEALTHCARE"))
                            .niftyIndiaMfgClose(indexSymbolToClosePrice.get("NIFTY INDIA MFG"))
                            .niftyItClose(indexSymbolToClosePrice.get("NIFTY IT"))
                            .niftyMediaClose(indexSymbolToClosePrice.get("NIFTY MEDIA"))
                            .niftyMetalClose(indexSymbolToClosePrice.get("NIFTY METAL"))
                            .niftyMicrocap250Close(indexSymbolToClosePrice.get("NIFTY MICROCAP250"))
                            .niftyPharmaClose(indexSymbolToClosePrice.get("NIFTY PHARMA"))
                            .niftyPseClose(indexSymbolToClosePrice.get("NIFTY PSE"))
                            .niftyPsuBankClose(indexSymbolToClosePrice.get("NIFTY PSU BANK"))
                            .niftyPvtBankClose(indexSymbolToClosePrice.get("NIFTY PVT BANK"))
                            .niftyRealtyClose(indexSymbolToClosePrice.get("NIFTY REALTY"))
                            .niftySmlcap100Close(indexSymbolToClosePrice.get("NIFTY SMLCAP 100"))
                            .build();
                })
                .collect(Collectors.toList());


        mergedDfRepository.saveAll(mergedDfList);
        mergedDfRepository.updateTradingSymbolFromPortfolioInstruments();
        return mergedDfList;

    }


    @Transactional
    public List<PortfolioInstruments> fetchAndSaveAllInstruments() throws Exception, KiteException {

        long count = portfolioInstrumentRepository.count();
        System.out.println("Found total number of instruments : " + count);

        if (count > 2300) {
            return portfolioInstrumentRepository.findAll();
        }

        List<com.zerodhatech.models.Instrument> instrumentsFromApi = kiteConnect.getInstruments();

        // Using parallel stream for mapping
        List<PortfolioInstruments> instrumentEntities = instrumentsFromApi.parallelStream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        // Optimized delete operation (if applicable)
        portfolioInstrumentRepository.deleteAllInBatch(); // Use deleteAllInBatch instead of deleteAll for better performance

        // Batch save entities
        portfolioInstrumentRepository.saveAll(instrumentEntities); // Ensure batch size is configured appropriately
        portfolioInstrumentRepository.deleteSpecificInstruments();
        return instrumentEntities;
    }

    private PortfolioInstruments convertToEntity(com.zerodhatech.models.Instrument apiInstrument) {
        PortfolioInstruments instrument = new PortfolioInstruments();

        instrument.setInstrumentToken(apiInstrument.instrument_token);
        instrument.setExchangeToken(apiInstrument.exchange_token);
        instrument.setTradingSymbol(apiInstrument.tradingsymbol);
        instrument.setName(apiInstrument.name);
        instrument.setLastPrice(apiInstrument.last_price != 0 ? BigDecimal.valueOf(apiInstrument.last_price) : null);
        instrument.setExpiry(apiInstrument.expiry);
        instrument.setStrike(apiInstrument.strike != null && !apiInstrument.strike.isEmpty() ? new BigDecimal(apiInstrument.strike) : null);
        instrument.setTickSize(apiInstrument.tick_size != 0 ? BigDecimal.valueOf(apiInstrument.tick_size) : null);
        instrument.setLotSize(apiInstrument.lot_size);
        instrument.setInstrumentType(apiInstrument.instrument_type);
        instrument.setSegment(apiInstrument.segment);
        instrument.setExchange(apiInstrument.exchange);
        instrument.setCreatedAt(LocalDateTime.now());

        return instrument;
    }


}
