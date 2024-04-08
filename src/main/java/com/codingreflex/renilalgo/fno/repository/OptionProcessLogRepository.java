package com.codingreflex.renilalgo.fno.repository;

import com.codingreflex.renilalgo.fno.entity.OptionInstrumentProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionProcessLogRepository extends JpaRepository<OptionInstrumentProcessLog, Long> {
}
