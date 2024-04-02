package com.codingreflex.renilalgo.portfolio;


import com.codingreflex.renilalgo.config.TokenRefreshChecker;
import com.codingreflex.renilalgo.portfolio.entity.PortfolioInstruments;
import com.codingreflex.renilalgo.portfolio.repository.PortfolioInstrumentRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PortfolioService {


    private final PortfolioInstrumentRepository portfolioInstrumentRepository;

    private final StocksHistoricalDataService stocksHistoricalDataService;

    private final KiteConnect kiteConnect;

    private final TokenRefreshChecker tokenRefreshChecker;


    @Autowired
    public PortfolioService(PortfolioInstrumentRepository portfolioInstrumentRepository,
                            StocksHistoricalDataService stocksHistoricalDataService,
                            KiteConnect kiteConnect,
                            TokenRefreshChecker tokenRefreshChecker) {
        this.portfolioInstrumentRepository = portfolioInstrumentRepository;
        this.stocksHistoricalDataService = stocksHistoricalDataService;
        this.kiteConnect = kiteConnect;
        this.tokenRefreshChecker = tokenRefreshChecker;
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
