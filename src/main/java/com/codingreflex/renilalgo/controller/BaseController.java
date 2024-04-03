package com.codingreflex.renilalgo.controller;

import com.codingreflex.renilalgo.common.enums.Interval;
import com.codingreflex.renilalgo.portfolio.PortfolioService;
import com.codingreflex.renilalgo.portfolio.StocksHistoricalDataService;
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
@RequestMapping("api")
public class BaseController {

    private final PortfolioService portfolioService;

    private final StocksHistoricalDataService stocksHistoricalDataService;

    @Autowired
    public BaseController(PortfolioService portfolioService, StocksHistoricalDataService stocksHistoricalDataService) {
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
    public ResponseEntity<List<Long>> getPortfolioInstrumentsTokens() {
        try {
            List<Long> tokens = portfolioService.getPortfolioInstrumentsTokens();
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/fetchHistoricalData/stocks")
    public ResponseEntity<?> fetchHistoricalDataForStocks() {
        try {
            stocksHistoricalDataService.fetchHistoricalDataForStocks();
            return ResponseEntity.ok("Historical data fetched and saved successfully");
        } catch (IOException | KiteException e) {
            // Handle specific exceptions
            return ResponseEntity.internalServerError().body("Error fetching historical data: " + e.getMessage());
        } catch (Exception e) {
            // Handle general exceptions
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/fetchHistoricalData/allparams")
    public ResponseEntity<?> fetchHistoricalDataForStocks(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("instrumentToken") Long instrumentToken,
            @RequestParam("interval") Interval interval) {
        try {
            stocksHistoricalDataService.fetchHistoricalDataForStocks(startDate, endDate, instrumentToken, interval);
            return ResponseEntity.ok("Historical data fetched and saved successfully for token " + instrumentToken);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching historical data: " + e.getMessage());
        } catch (KiteException e) {
            throw new RuntimeException(e);
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


}