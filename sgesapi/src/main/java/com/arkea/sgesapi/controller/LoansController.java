package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.api.sigac.LoansApi;
import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.service.PersonnesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Contrôleur REST — Implémente directement LoansApi (interface générée OpenAPI 3.0.2).
 * <p>
 * Endpoint exposé :
 *   GET /loans/{id}
 * <p>
 * Flux :
 *   1. Appel service externe SIGAC REST via ISigacLoansDao → reçoit un CommonLoan
 *   2. Conversion CommonLoan → DossierConsultationDto (modèle interne)
 *   3. Résolution des noms emprunteur/coEmprunteur via PersonnesService (opentopazservice)
 *   4. Retour de la réponse au frontend
 * <p>
 * Pas de delegate pattern — on implémente LoansApi directement, comme tout controller.
 */
@RestController
@CrossOrigin(origins = "*")
public class LoansController implements LoansApi {

    private static final Logger log = LoggerFactory.getLogger(LoansController.class);

    private final ISigacLoansDao sigacLoansDao;
    private final PersonnesService personnesService;

    public LoansController(ISigacLoansDao sigacLoansDao, PersonnesService personnesService) {
        this.sigacLoansDao = sigacLoansDao;
        this.personnesService = personnesService;
    }

    /**
     * GET /loans/{id} — Récupère un prêt depuis SIGAC.
     * <p>
     * Le paramètre id = numéro contrat souscrit (Topaze Contrat ID).
     * Le CommonLoan reçu de SIGAC est converti en DossierConsultationDto
     * pour enrichissement des noms via opentopazservice.
     */
    @Override
    public ResponseEntity<CommonLoan> getLoan(String id) {
        log.info("Loans API — getLoan contratId={}", id);

        try {
            Optional<CommonLoan> optLoan = sigacLoansDao.getLoan(id);

            if (optLoan.isEmpty()) {
                log.warn("Prêt non trouvé : {}", id);
                return ResponseEntity.notFound().build();
            }

            CommonLoan loan = optLoan.get();

            // CommonLoan → DossierConsultationDto (modèle interne)
            DossierConsultationDto dossier = toDossierConsultationDto(loan);

            // Résolution des noms via opentopazservice
            enrichirNomPersonnes(dossier);

            log.debug("DossierConsultationDto construit et enrichi pour contratId={}", id);

            return ResponseEntity.ok(loan);

        } catch (DAOException e) {
            log.error("Erreur DAO lors de la consultation du prêt {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── Conversion CommonLoan → DossierConsultationDto ──────────────

    /**
     * Convertit un CommonLoan (modèle SIGAC externe) en DossierConsultationDto (modèle interne).
     * Les noms des personnes ne sont PAS remplis ici — seuls les identifiants
     * (noEmprunteur, noCoEmprunteur, efs) sont extraits des Participant.
     */
    DossierConsultationDto toDossierConsultationDto(CommonLoan loan) {
        DossierConsultationDto.Builder builder = DossierConsultationDto.builder();

        builder.numeroContratSouscritPret(loan.getId());
        builder.numeroContratSouscritProjet(loan.getMasterContractId());

        builder.dureePret(loan.getDuration());
        builder.montantPret(toDouble(loan.getBorrowedAmount()));
        builder.tauxRemboursement(toDouble(loan.getRate()));
        builder.montantDisponible(toDouble(loan.getAvailableAmount()));

        builder.libelleNature(loan.getLabel());
        builder.codeNature(loan.getTypeCode());

        if (loan.getLoanType() != null) {
            builder.codeNature(loan.getLoanType().getCode());
            builder.libelleNature(loan.getLoanType().getLabel());
        }

        if (loan.getObjectCode() != null) {
            builder.codeObjet(loan.getObjectCode().getCode());
            builder.libelleObjet(loan.getObjectCode().getLabel());
        }

        if (loan.getLoanState() != null) {
            builder.codeEtat(loan.getLoanState().getCode());
            builder.libelleEtat(loan.getLoanState().getLabel());
        }

        // Identifiants personnes seulement — noms résolus par opentopazservice
        if (loan.getParticipants() != null) {
            for (Participant p : loan.getParticipants()) {
                if ("EMP".equals(p.getRoleCode())) {
                    builder.noEmprunteur(p.getPersonNumber());
                    builder.efs(p.getPersonFederation());
                } else if ("COE".equals(p.getRoleCode())) {
                    builder.noCoEmprunteur(p.getPersonNumber());
                }
            }
        }

        return builder.build();
    }

    // ── Résolution des noms via opentopazservice ──────────────────

    private void enrichirNomPersonnes(DossierConsultationDto dto) {
        String noEmp = dto.getNoEmprunteur();
        String noCoEmp = dto.getNoCoEmprunteur();

        if ((noEmp == null || noEmp.isBlank()) && (noCoEmp == null || noCoEmp.isBlank())) {
            return;
        }

        try {
            String[] libelles = personnesService.resoudreEmprunteurCoEmprunteur(noEmp, noCoEmp);
            if (libelles[0] != null) {
                dto.setEmprunteur(libelles[0]);
            }
            if (libelles[1] != null) {
                dto.setCoEmprunteur(libelles[1]);
            }
            log.debug("Personnes résolues — emprunteur={}, coEmprunteur={}", libelles[0], libelles[1]);
        } catch (Exception e) {
            log.warn("Impossible de résoudre les personnes pour le contrat {} : {}",
                    dto.getNumeroContratSouscritPret(), e.getMessage());
        }
    }

    private Double toDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
