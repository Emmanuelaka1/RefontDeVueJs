package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import org.springframework.http.ResponseEntity;

/**
 * Interface delegate pour l'API Loans.
 * <p>
 * Orchestre l'appel au service SIGAC (via LoansApi générée),
 * la conversion CommonLoan → DossierConsultationDto,
 * et l'enrichissement des noms via PersonnesService.
 * <p>
 * Implémentation : {@link com.arkea.sgesapi.dao.impl.LoansApiDelegateImpl}
 */
public interface LoansApiDelegate {

    /**
     * Recherche un prêt par son identifiant contrat souscrit (Topaze Contrat ID).
     *
     * @param contratId identifiant contrat souscrit prêt (ex: "DD04063627")
     * @return ResponseEntity contenant le DossierConsultationDto, ou 404/500
     */
    ResponseEntity<DossierConsultationDto> searchLoans(String contratId);
}
