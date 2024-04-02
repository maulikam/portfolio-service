package com.codingreflex.renilalgo.portfolio.repository;

import com.codingreflex.renilalgo.portfolio.entity.PortfolioInstruments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioInstrumentRepository extends JpaRepository<PortfolioInstruments, Long> {

    List<PortfolioInstruments> findByExchange(String exchange);

    PortfolioInstruments findByTradingSymbol(String tradingSymbol);

    List<PortfolioInstruments> findByInstrumentType(String instrumentType);

    List<PortfolioInstruments> findByTradingSymbolIn(List<String> tradingSymbols);

    long countByCreatedAtAfter(LocalDateTime startOfDay);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM portfolio_instruments " +
            "WHERE exchange IN ('CDS', 'BCD', 'NFO', 'BFO', 'NSEIX', 'MCX', 'BSE') " +
            "OR instrument_type IN ('FUT', 'CE', 'PE') " +
            "OR name IS NULL " +
            "OR name LIKE '%BEES%' " +
            "OR name LIKE '%ETF%' " +
            "OR tradingsymbol LIKE '%ETF%' " +
            "OR (tick_size != 0.05 AND instrument_type = 'EQ') " +
            "OR tradingsymbol IN (" +
            "'HANGSENG BEES-NAV', " +
            "'NIFTY GS 10YR', " +
            "'NIFTY GS 10YR CLN', " +
            "'NIFTY GS 11 15YR', " +
            "'NIFTY GS 15YRPLUS', " +
            "'NIFTY GS 4 8YR', " +
            "'NIFTY GS 8 13YR', " +
            "'NIFTY GS COMPSITE', " +
            "'NIFTY MID LIQ 15', " +
            "'NIFTY50 PR 1X INV', " +
            "'NIFTY50 PR 2X LEV', " +
            "'NIFTY50 TR 1X INV', " +
            "'NIFTY50 TR 2X LEV'" +
            ")", nativeQuery = true)
    void deleteSpecificInstruments();


    @Query("SELECT i.tradingSymbol FROM PortfolioInstruments i WHERE i.instrumentToken = :instrumentToken")
    String findTradingSymbolByInstrumentToken(Long instrumentToken);

    @Query("SELECT DISTINCT i.instrumentToken FROM PortfolioInstruments i")
    List<Long> findAllDistinctInstrumentTokens();

}
