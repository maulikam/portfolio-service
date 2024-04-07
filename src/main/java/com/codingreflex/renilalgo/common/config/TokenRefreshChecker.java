package com.codingreflex.renilalgo.common.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TokenRefreshChecker {

    private final ConfigurationService configurationService;
    private final KiteConnect kiteConnect;


    @Autowired
    public TokenRefreshChecker(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        kiteConnect = new KiteConnect(configurationService.getApiKey(), true);
        kiteConnect.setUserId(configurationService.getClientId());
    }

    @Bean
    public KiteConnect kiteConnect() {
        return kiteConnect;
    }


    public void configureKiteConnect() throws Exception {
        try {
            String storedRequestToken = configurationService.getRequestToken();
            String apiSecret = configurationService.getApiSecret();

            kiteConnect.setAccessToken(configurationService.getAccessToken());
            kiteConnect.setPublicToken(configurationService.getPublicToken());

            String email = kiteConnect.getProfile().email;

            log.info("\n ************  User fetched successfully: " + email);
            log.info("\n ************  Kite Connect session updated successfully.");

        } catch (Exception | KiteException e) {
            System.err.println("Error updating Kite Connect session: " + e.getMessage());
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public void refreshSession() throws Exception {
        try {
            String storedRequestToken = configurationService.getRequestToken();
            String apiSecret = configurationService.getApiSecret();

            User user = kiteConnect.generateSession(storedRequestToken, apiSecret);
            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);

            log.info("\n ************  User fetched successfully: " + user.userName);
            // Update stored tokens with new values
            configurationService.updateAccessToken(user.accessToken);
            configurationService.updatePublicToken(user.publicToken);
            configurationService.updateRequestToken(storedRequestToken);

            log.info("\n ************  Kite Connect session updated successfully.");

        } catch (Exception | KiteException e) {
            System.err.println("Error updating Kite Connect session: " + e.getMessage());
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}

