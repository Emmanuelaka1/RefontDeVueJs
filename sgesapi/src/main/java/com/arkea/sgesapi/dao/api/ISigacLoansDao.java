package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.model.sigac.CommonLoan;
import com.arkea.sgesapi.exception.DAOException;

import java.util.Optional;

/**
 * Interface DAO pour l'accès au service REST SIGAC Loans.
 * <p>
 * Le service externe SIGAC expose un endpoint REST qui retourne
 * un CommonLoan (conforme au contrat OpenAPI sigac-prets.yaml).
 * <p>
 * En dev (@Profile "dev") : SigacLoansMockDao retourne des données bouchonnées.
 * En prod : l'implémentation réelle appellera le service REST SIGAC via HTTP.
 */
public interface ISigacLoansDao {

    /**
     * Récupère un prêt par son identifiant contrat souscrit (Topaze Contrat ID).
     *
     * @param contratId identifiant contrat souscrit prêt (ex: "DD04063627")
     * @return le CommonLoan correspondant, ou empty si non trouvé
     * @throws DAOException en cas d'erreur d'accès au service SIGAC
     */
    Optional<CommonLoan> getLoan(String contratId) throws DAOException;
}
