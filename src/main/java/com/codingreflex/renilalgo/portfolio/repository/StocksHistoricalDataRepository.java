package com.codingreflex.renilalgo.portfolio.repository;

import com.codingreflex.renilalgo.portfolio.entity.StocksHistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StocksHistoricalDataRepository extends JpaRepository<StocksHistoricalData, Long> {
    long countByInstrumentTokenAndTimestampsAfter(Long instrumentToken, LocalDateTime date);

    void deleteByInstrumentToken(Long instrumentToken);

    long countByCreatedAtAfter(LocalDateTime startOfToday);

    @Query(value = "SELECT * FROM stocks_historical_data WHERE instrument_token = :instrumentToken", nativeQuery = true)
    List<StocksHistoricalData> findAllByInstrumentToken(@Param("instrumentToken") Long instrumentToken);

    @Query("SELECT h FROM StocksHistoricalData h WHERE h.instrumentToken = :instrumentToken AND h.timestamps >= :startTime AND h.timestamps <= :endTime")
    List<StocksHistoricalData> findByInstrumentTokenAndTimeframe(@Param("instrumentToken") Long instrumentToken, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}


