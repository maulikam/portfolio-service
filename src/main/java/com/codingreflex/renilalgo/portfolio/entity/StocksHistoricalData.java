package com.codingreflex.renilalgo.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "stocks_historical_data")
@AllArgsConstructor
@ToString
public class StocksHistoricalData extends BaseHistoricalData {
}
