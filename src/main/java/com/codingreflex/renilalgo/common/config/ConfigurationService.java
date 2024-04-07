package com.codingreflex.renilalgo.common.config;

import com.codingreflex.renilalgo.common.config.entity.Configuration;
import com.codingreflex.renilalgo.common.config.repository.ConfigurationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public String getConfigurationValue(String key) {
        return configurationRepository.findById(key)
                .map(Configuration::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Configuration key not found: " + key));
    }

    @Transactional
    public void updateConfigurationValue(String key, String newValue) {
        Configuration config = configurationRepository.findById(key)
                .orElseThrow(() -> new IllegalArgumentException("Configuration key not found: " + key));
        config.setValue(newValue);
        configurationRepository.save(config);
    }

    // Convenience methods for specific configurations
    public String getAccessToken() {
        return getConfigurationValue("access_token");
    }

    public void updateAccessToken(String newToken) {
        updateConfigurationValue("access_token", newToken);
    }

    public String getPublicToken() {
        return getConfigurationValue("public_token");
    }

    public void updatePublicToken(String newToken) {
        updateConfigurationValue("public_token", newToken);
    }


    public String getRequestToken() {
        return getConfigurationValue("request_token");
    }

    public void updateRequestToken(String newToken) {
        updateConfigurationValue("request_token", newToken);
    }

    public String getApiKey() {
        return getConfigurationValue("apiKey");
    }

    public void updateApiKey(String apiKey) {
        updateConfigurationValue("apiKey", apiKey);
    }

    public String getApiSecret() {
        return getConfigurationValue("apiSecret");
    }

    public void updateApiSecret(String apiSecret) {
        updateConfigurationValue("apiSecret", apiSecret);
    }

    public String getClientId() {
        return getConfigurationValue("clientId");
    }

    public void updateClientId(String clientId) {
        updateConfigurationValue("clientId", clientId);
    }

    public String getAppName() {
        return getConfigurationValue("appName");
    }

    public void updateAppName(String appName) {
        updateConfigurationValue("appName", appName);
    }
}


