package com.codingreflex.renilalgo.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseHistoricalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_token")
    private Long instrumentToken;

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

    // Common methods, if any
}

