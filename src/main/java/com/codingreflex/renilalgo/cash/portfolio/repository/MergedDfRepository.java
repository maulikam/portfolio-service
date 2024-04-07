package com.codingreflex.renilalgo.cash.portfolio.repository;

import com.codingreflex.renilalgo.cash.portfolio.entity.MergedDf;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MergedDfRepository extends JpaRepository<MergedDf, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE public.merged_df md " +
            "SET tradingsymbol = (SELECT pi.tradingsymbol FROM public.portfolio_instruments pi WHERE pi.instrument_token = md.instrument_token) " +
            "WHERE md.tradingsymbol IS NULL",
            nativeQuery = true)
    void updateTradingSymbolFromPortfolioInstruments();
}
