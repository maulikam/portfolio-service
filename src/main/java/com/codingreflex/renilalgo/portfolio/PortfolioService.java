package com.codingreflex.renilalgo.portfolio;

import com.codingreflex.renilalgo.common.enums.Interval;
import com.codingreflex.renilalgo.config.KiteProperties;
import com.codingreflex.renilalgo.connect.KiteConnectService;
import com.codingreflex.renilalgo.portfolio.entity.PortfolioInstruments;
import com.codingreflex.renilalgo.portfolio.repository.PortfolioInstrumentRepository;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class PortfolioService {


    private final PortfolioInstrumentRepository portfolioInstrumentRepository;

    private final StocksHistoricalDataService stocksHistoricalDataService;

    private final ConfigurationService configurationService;

    private final KiteProperties kiteProperties;

    private final KiteConnect kiteConnect;

    @Autowired
    public PortfolioService(PortfolioInstrumentRepository portfolioInstrumentRepository,
                            StocksHistoricalDataService stocksHistoricalDataService,
                            ConfigurationService configurationService, KiteProperties kiteProperties) {
        this.portfolioInstrumentRepository = portfolioInstrumentRepository;
        this.stocksHistoricalDataService = stocksHistoricalDataService;
        this.configurationService = configurationService;
        this.kiteProperties = kiteProperties;
        this.kiteConnect = new KiteConnect(kiteProperties.getApiKey());
    }


    @PostConstruct
    public void fetchAndSaveInstruments() throws Exception, KiteException {

        String refreshToken = configurationService.getRefreshToken();
        String apiSecret = configurationService.getApiSecret();

        User user = kiteConnect.generateSession(refreshToken, apiSecret);
        kiteConnect.setAccessToken(user.accessToken);
        kiteConnect.setPublicToken(user.publicToken);

        configurationService.updateAccessToken(user.accessToken);
        configurationService.updatePublicToken(user.publicToken);

        configurationService.updateRefreshToken(kiteProperties.getAccessToken());
        fetchAndSaveAllInstruments();
        stocksHistoricalDataService.fetchHistoricalDataForStocks(2024, 1, 6, Interval.DAY.getInterval());
    }

    @Transactional
    public List<PortfolioInstruments> fetchAndSaveAllInstruments() throws Exception, KiteException {

        long count = portfolioInstrumentRepository.count();
        System.out.println("Found total number of instruments : " + count);

        if (count == 2311) {
            return portfolioInstrumentRepository.findAll();
        }

        List<com.zerodhatech.models.Instrument> instrumentsFromApi = kiteConnectService.getKiteConnect().getInstruments();

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
