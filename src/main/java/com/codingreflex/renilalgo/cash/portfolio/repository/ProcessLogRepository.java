package com.codingreflex.renilalgo.cash.portfolio.repository;

import com.codingreflex.renilalgo.cash.portfolio.entity.InstrumentProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessLogRepository extends JpaRepository<InstrumentProcessLog, Long> {
}
