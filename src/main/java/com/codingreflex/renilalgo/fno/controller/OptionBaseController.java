package com.codingreflex.renilalgo.fno.controller;

import com.codingreflex.renilalgo.common.enums.Interval;
import com.codingreflex.renilalgo.fno.OptionPortfolioService;
import com.codingreflex.renilalgo.fno.OptionStocksHistoricalDataService;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/options")
public class OptionBaseController {

    private final OptionPortfolioService portfolioService;

    private final OptionStocksHistoricalDataService stocksHistoricalDataService;

    @Autowired
    public OptionBaseController(OptionPortfolioService portfolioService, OptionStocksHistoricalDataService stocksHistoricalDataService) {
        this.portfolioService = portfolioService;
        this.stocksHistoricalDataService = stocksHistoricalDataService;
    }

        @GetMapping("/fetchAndSaveAllInstruments")
    public ResponseEntity<?> fetchAndSaveAllInstruments() {
        try {
            var instruments = portfolioService.fetchAndSaveAllInstruments();
            return ResponseEntity.ok(instruments);
        } catch (Exception e) {
            // Handle exceptions properly - log the error, return an appropriate HTTP status, etc.
            return ResponseEntity.internalServerError().body("Error fetching and saving instruments: " + e.getMessage());
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/instrumentsTokens")
    public ResponseEntity<?> getPortfolioInstrumentsTokens() {
        try {
            List<String> tokens = portfolioService.getPortfolioInstrumentsTokens();
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/fetchHistoricalData/dates")
    public ResponseEntity<?> fetchHistoricalDataForStocks(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("interval") Interval interval) {
        try {
            stocksHistoricalDataService._fetchHistoricalDataForStocks(startDate, endDate, interval);
            return ResponseEntity.ok("Historical data fetched and saved successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching historical data: " + e.getMessage());
        }
    }

    @GetMapping("/dayEndData")
    public ResponseEntity<?> getDayEndData(@RequestParam(value = "instruments", required = false) String[] instruments) {
        try {
            return ResponseEntity.ok(portfolioService.getDayEndData(instruments));
        } catch (IOException | KiteException e) {
            return ResponseEntity.internalServerError().body("Error getting day end data: " + e.getMessage());
        } catch (Exception e) {
            // General exception handling
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }



}