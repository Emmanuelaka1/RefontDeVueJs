package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.api.LoansApiDelegate;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST — Prêts (Loans API).
 * <p>
 * Endpoint exposé :
 *   GET /api/v1/loans/{numeroPret}
 * <p>
 * Flux :
 *   1. Le controller délègue à LoansApiDelegate (LoansApiDelegateImpl)
 *   2. Le delegate appelle SIGAC via LoansApi → reçoit un CommonLoan
 *   3. Conversion CommonLoan → DossierConsultationDto (modèle interne)
 *   4. Résolution des noms emprunteur/coEmprunteur via PersonnesService (opentopazservice)
 *   5. Retour de la réponse au frontend
 */
@RestController
@RequestMapping("/api/v1/loans")
@CrossOrigin(origins = "*")
public class LoansController {

    private final LoansApiDelegate loansApiDelegate;

    public LoansController(LoansApiDelegate loansApiDelegate) {
        this.loansApiDelegate = loansApiDelegate;
    }

    @GetMapping("/{numeroPret}")
    public ResponseEntity<DossierConsultationDto> searchLoans(@PathVariable String numeroPret) {
        return loansApiDelegate.searchLoans(numeroPret);
    }
}
