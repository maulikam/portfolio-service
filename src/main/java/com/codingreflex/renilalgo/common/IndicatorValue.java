package com.codingreflex.renilalgo.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Component
public class IndicatorValue {
    private LocalDateTime timestamp;
    private Double value;

    public IndicatorValue(LocalDateTime timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}

