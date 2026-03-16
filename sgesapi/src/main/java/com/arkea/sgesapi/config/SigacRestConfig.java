package com.arkea.sgesapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

/**
 * Configuration du client REST pour le service SIGAC Loans.
 * <p>
 * Activé uniquement hors profil "dev" (val, rec, hml, prod).
 * Fournit le bean {@link RestClient} avec l'URL de base SIGAC préconfigurée.
 */
@Configuration
@Profile("!dev")
public class SigacRestConfig {

    @Bean
    public RestClient sigacRestClient(@Value("${sigac.loans.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
