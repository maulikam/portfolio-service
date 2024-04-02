package com.codingreflex.renilalgo.config.repository;

import com.codingreflex.renilalgo.config.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
}

