package com.codingreflex.renilalgo.portfolio.repository;

import com.codingreflex.renilalgo.portfolio.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
}

