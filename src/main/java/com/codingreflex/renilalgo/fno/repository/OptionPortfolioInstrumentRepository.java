package com.codingreflex.renilalgo.fno.repository;

import com.codingreflex.renilalgo.fno.entity.OptionPortfolioInstruments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


// QUERY: "SELECT * FROM public.option_portfolio_instruments where exchange = 'NSE'
// AND tradingsymbol in ('NIFTY 50', 'NIFTY BANK', 'ADANIENT', 'ADANIPORTS', 'APOLLOHOSP',
// 'ASIANPAINT', 'AXISBANK', 'BAJAJ-AUTO', 'BAJFINANCE', 'BAJAJFINSV', 'BPCL', 'BHARTIARTL',
// 'BRITANNIA', 'CIPLA', 'COALINDIA', 'DIVISLAB', 'DRREDDY', 'EICHERMOT', 'GRASIM', 'HCLTECH',
// 'HDFCBANK', 'HDFCLIFE', 'HEROMOTOCO', 'HINDALCO', 'HINDUNILVR', 'ICICIBANK', 'ITC',
// 'INDUSINDBK', 'INFY', 'JSWSTEEL', 'KOTAKBANK', 'LTIM', 'LT', 'M&M', 'MARUTI', 'NTPC',
// 'NESTLEIND', 'ONGC', 'POWERGRID', 'RELIANCE', 'SBILIFE', 'SHRIRAMFIN', 'SBIN', 'SUNPHARMA',
// 'TCS', 'TATACONSUM', 'TATAMOTORS', 'TATASTEEL', 'TECHM', 'TITAN', 'ULTRACEMCO', 'WIPRO')
// ORDER BY id ASC "

@Repository
public interface OptionPortfolioInstrumentRepository extends JpaRepository<OptionPortfolioInstruments, Long> {

    List<OptionPortfolioInstruments> findByExchange(String exchange);

    OptionPortfolioInstruments findByTradingSymbol(String tradingSymbol);

    List<OptionPortfolioInstruments> findByInstrumentType(String instrumentType);

    List<OptionPortfolioInstruments> findByTradingSymbolIn(List<String> tradingSymbols);

    long countByCreatedAtAfter(LocalDateTime startOfDay);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM option_portfolio_instruments " +
            "WHERE tradingsymbol NOT IN ('NIFTY 50', 'NIFTY BANK')", nativeQuery = true)
    void deleteSpecificInstruments();


    @Query("SELECT i.tradingSymbol FROM OptionPortfolioInstruments i WHERE i.instrumentToken = :instrumentToken")
    String findTradingSymbolByInstrumentToken(String instrumentToken);

    @Query("SELECT DISTINCT i.instrumentToken FROM OptionPortfolioInstruments i")
    List<String> findAllDistinctInstrumentTokens();

}
