package com.codingreflex.renilalgo.fno.repository;

import com.codingreflex.renilalgo.fno.entity.OptionStocksHistoricalData;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OptionStocksHistoricalDataRepository extends JpaRepository<OptionStocksHistoricalData, Long> {
    long countByInstrumentTokenAndTimestampsAfter(String instrumentToken, LocalDateTime date);

    void deleteByInstrumentToken(String instrumentToken);

    long countByCreatedAtAfter(LocalDateTime startOfToday);

    @Query(value = "SELECT * FROM option_stocks_historical_data WHERE instrument_token = :instrumentToken", nativeQuery = true)
    List<OptionStocksHistoricalData> findAllByInstrumentToken(@Param("instrumentToken") String instrumentToken);

    @Query("SELECT h FROM OptionStocksHistoricalData h WHERE h.instrumentToken = :instrumentToken AND h.timestamps >= :startTime AND h.timestamps <= :endTime")
    List<OptionStocksHistoricalData> findByInstrumentTokenAndTimeframe(@Param("instrumentToken") String instrumentToken, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Modifying
    @Transactional
    @Query(value = "UPDATE option_all_stocks_historical_data SET tradingsymbol = (SELECT tradingsymbol FROM option_portfolio_instruments WHERE option_portfolio_instruments.instrument_token = option_all_stocks_historical_data.instrument_token)", nativeQuery = true)
    void updateTradingSymbol();
}


