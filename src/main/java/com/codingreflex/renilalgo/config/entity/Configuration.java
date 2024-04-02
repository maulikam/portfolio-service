package com.codingreflex.renilalgo.config.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Configuration {

    @Id
    private String key;

    private String value;

    // Getters and setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

