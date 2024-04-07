package com.codingreflex.renilalgo.common.config.repository;

import com.codingreflex.renilalgo.common.config.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
}

