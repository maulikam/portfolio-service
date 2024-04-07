package com.codingreflex.renilalgo.fno.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "all_stocks_historical_data")
@AllArgsConstructor
@ToString
@Builder
@NoArgsConstructor
@Getter
@Setter
public class StocksHistoricalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_token")
    private Long instrumentToken;

    @Column(name = "tradingsymbol", length = 100)
    private String tradingSymbol;

    @Column(name = "timestamps")
    private LocalDateTime timestamps;

    @Column(name = "open_price")
    private Double openPrice;

    @Column(name = "high_price")
    private Double highPrice;

    @Column(name = "low_price")
    private Double lowPrice;

    @Column(name = "close_price")
    private Double closePrice;

    @Column(name = "volume")
    private Double volume;

    @Column(name = "oi")
    private Long oi;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
