package com.codingreflex.renilalgo.config.controller;

import com.codingreflex.renilalgo.config.TokenRefreshChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.codingreflex.renilalgo.config.ConfigurationService;

@RestController
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final TokenRefreshChecker tokenRefreshChecker;


    @Autowired
    public ConfigurationController(ConfigurationService configurationService,
                                   TokenRefreshChecker tokenRefreshChecker) {
        this.configurationService = configurationService;
        this.tokenRefreshChecker = tokenRefreshChecker;
    }


    @GetMapping("/configuration")
    public ResponseEntity<?> refreshSession() {
        try {
            tokenRefreshChecker.configureKiteConnect(); // Update and check session
            return ResponseEntity.ok("DB: Session kiteconnect loaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error DB: Session kiteconnect: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> updateRequestToken(
            @RequestParam("action") String action,
            @RequestParam("type") String type,
            @RequestParam("status") String status,
            @RequestParam("request_token") String requestToken) {

        if ("login".equals(action) && "login".equals(type) && "success".equals(status)) {
            try {
                configurationService.updateConfigurationValue("request_token", requestToken);
                tokenRefreshChecker.refreshSession(); // Update and check session
                return ResponseEntity.ok("Request token updated successfully");
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error updating request token: " + e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid parameters");
        }
    }
}

