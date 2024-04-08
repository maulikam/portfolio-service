package com.codingreflex.renilalgo.fno.repository;

import com.codingreflex.renilalgo.fno.entity.OptionMergedDf;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OptionMergedDfRepository extends JpaRepository<OptionMergedDf, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE public.option_merged_df md " +
            "SET tradingsymbol = (SELECT pi.tradingsymbol FROM public.option_portfolio_instruments pi WHERE pi.instrument_token = md.instrument_token) " +
            "WHERE md.tradingsymbol IS NULL",
            nativeQuery = true)
    void updateTradingSymbolFromPortfolioInstruments();
}
