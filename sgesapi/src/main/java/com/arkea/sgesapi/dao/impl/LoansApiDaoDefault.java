package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.api.sigac.LoansApi;
import com.arkea.sgesapi.model.sigac.CommonLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Implémentation REST du client SIGAC Loans — profils val/rec/hml/prod.
 * <p>
 * Appelle le service REST SIGAC externe pour récupérer les données de prêts.
 * Utilise {@link RestClient} (Spring 6.1+), successeur fluent de RestTemplate.
 * <p>
 * L'URL de base est configurée via le bean {@code sigacRestClient}
 * défini dans {@link com.arkea.sgesapi.config.SigacRestConfig}.
 * <p>
 * Endpoint appelé : GET {base-url}/loans/{contratId}
 * Retourne un CommonLoan conforme au contrat OpenAPI sigac-prets.yaml.
 * <p>
 * En dev (@Profile "dev"), c'est LoansApiDaoMock qui prend le relai.
 */
@Repository
@Profile("!dev")
public class LoansApiDaoDefault implements LoansApi {

    private static final Logger log = LoggerFactory.getLogger(LoansApiDaoDefault.class);

    private final RestClient restClient;

    public LoansApiDaoDefault(RestClient sigacRestClient) {
        this.restClient = sigacRestClient;
    }

    @Override
    public ResponseEntity<CommonLoan> getLoan(String contratId) {
        log.info("SIGAC REST — GET /loans/{}", contratId);

        try {
            CommonLoan loan = restClient.get()
                    .uri("/loans/{contratId}", contratId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            // 404 traité en aval — on ne lance pas d'exception
                            return;
                        }
                        throw new RestClientException(
                                "SIGAC erreur client : " + response.getStatusCode());
                    })
                    .body(CommonLoan.class);

            // Si body null (ex: 404 silencieux) → notFound
            if (loan == null) {
                log.warn("SIGAC — Prêt non trouvé : {}", contratId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(loan);

        } catch (RestClientException e) {
            log.error("SIGAC — Erreur lors de l'appel REST pour le contrat {}", contratId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
