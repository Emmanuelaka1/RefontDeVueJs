package com.arkea.sgesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration du client REST pour le service SIGAC Loans.
 * <p>
 * Activé uniquement hors profil "dev" (val, rec, hml, prod).
 * Fournit le bean RestTemplate utilisé par SigacLoansRestDao.
 */
@Configuration
@Profile("!dev")
public class SigacRestConfig {

    @Bean
    public RestTemplate sigacRestTemplate() {
        return new RestTemplate();
    }
}
