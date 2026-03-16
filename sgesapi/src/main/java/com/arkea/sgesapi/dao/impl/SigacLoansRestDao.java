package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.model.sigac.CommonLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Implémentation REST du client SIGAC Loans — profils val/rec/hml/prod.
 * <p>
 * Appelle le service REST SIGAC externe pour récupérer les données de prêts.
 * L'URL de base est configurable via la propriété {@code sigac.loans.base-url}.
 * <p>
 * Endpoint appelé : GET {base-url}/loans/{contratId}
 * Retourne un CommonLoan conforme au contrat OpenAPI sigac-prets.yaml.
 * <p>
 * En dev (@Profile "dev"), c'est SigacLoansMockDao qui prend le relai.
 */
@Repository
@Profile("!dev")
public class SigacLoansRestDao implements ISigacLoansDao {

    private static final Logger log = LoggerFactory.getLogger(SigacLoansRestDao.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public SigacLoansRestDao(
            RestTemplate restTemplate,
            @Value("${sigac.loans.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<CommonLoan> getLoan(String contratId) throws DAOException {
        String url = baseUrl + "/loans/" + contratId;
        log.info("SIGAC REST — GET {}", url);

        try {
            CommonLoan loan = restTemplate.getForObject(url, CommonLoan.class);
            return Optional.ofNullable(loan);

        } catch (RestClientException e) {
            if (is404(e)) {
                log.warn("SIGAC — Prêt non trouvé : {}", contratId);
                return Optional.empty();
            }
            log.error("SIGAC — Erreur lors de l'appel REST pour le contrat {}", contratId, e);
            throw new DAOException("Erreur appel SIGAC Loans : " + e.getMessage(), e);
        }
    }

    private boolean is404(RestClientException e) {
        String msg = e.getMessage();
        return msg != null && msg.contains("404");
    }
}
