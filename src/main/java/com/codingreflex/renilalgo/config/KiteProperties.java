package com.codingreflex.renilalgo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kite")
@Data
public class KiteProperties {
    private String apiKey;
    private String apiSecret;
    private String userId;
    private String apiName;
    private String refreshToken;
}

