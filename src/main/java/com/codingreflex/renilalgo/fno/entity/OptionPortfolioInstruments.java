package com.codingreflex.renilalgo.fno.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "optn_portfolio_instruments")
public class OptionPortfolioInstruments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_token")
    private Long instrumentToken;

    @Column(name = "exchange_token")
    private Long exchangeToken;

    @Column(name = "tradingsymbol", length = 100)
    private String tradingSymbol;

    @Column(length = 255)
    private String name;

    @Column(name = "last_price", precision = 19, scale = 2)
    private BigDecimal lastPrice;

    @Column(name = "expiry")
    @Temporal(TemporalType.DATE)
    private Date expiry;

    @Column(name = "strike", precision = 19, scale = 2)
    private BigDecimal strike;

    @Column(name = "tick_size", precision = 19, scale = 2)
    private BigDecimal tickSize;

    @Column(name = "lot_size")
    private Integer lotSize;

    @Column(name = "instrument_type", length = 50)
    private String instrumentType;

    @Column(length = 50)
    private String segment;

    @Column(length = 50)
    private String exchange;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}


