package com.codingreflex.renilalgo.portfolio.repository;

import com.codingreflex.renilalgo.portfolio.entity.InstrumentProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessLogRepository extends JpaRepository<InstrumentProcessLog, Long> {
}
