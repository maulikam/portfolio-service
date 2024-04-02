package com.codingreflex.renilalgo.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Component
public class Candle {
    private LocalDateTime timestamps;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double volume;

    public Candle(LocalDateTime timestamps, Double openPrice, Double highPrice, Double lowPrice, Double closePrice, Double volume) {
        this.timestamps = timestamps;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

}

