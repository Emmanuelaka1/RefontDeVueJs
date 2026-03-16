package com.arkea.sgesapi.delegate;

import com.arkea.sgesapi.api.sigac.LoansApiDelegate;
import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.service.PersonnesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implémentation du delegate généré depuis openapi/sigac-prets.yaml (OpenAPI 3.0.2).
 * <p>
 * Endpoint :
 *   GET /loans/{id} → getLoan(id)
 * <p>
 * Flux :
 *   1. Appel service externe SIGAC REST → reçoit un CommonLoan
 *   2. Conversion CommonLoan → DossierConsultationDto (modèle interne)
 *   3. Résolution des noms emprunteur/coEmprunteur via PersonnesService (opentopazservice)
 *   4. Retour au frontend
 * <p>
 * Le mapping CommonLoan → DossierConsultationDto traduit les structures
 * générées (LoanState, LoanType, ObjectCode, Participant) en champs
 * internes du DTO (codeEtat/libelleEtat, codeNature/libelleNature, etc.).
 * Les noms des participants sont résolus via opentopazservice (Thrift Topaze).
 */
@Service
public class LoansApiDelegateImpl implements LoansApiDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoansApiDelegateImpl.class);

    private final ISigacLoansDao sigacLoansDao;
    private final PersonnesService personnesService;

    public LoansApiDelegateImpl(ISigacLoansDao sigacLoansDao, PersonnesService personnesService) {
        this.sigacLoansDao = sigacLoansDao;
        this.personnesService = personnesService;
    }

    // ── GET /loans/{id} ────────────────────────────────────────────
    @Override
    public ResponseEntity<CommonLoan> getLoan(String id) {
        log.info("Loans API — getLoan id={}", id);

        try {
            Optional<CommonLoan> optLoan = sigacLoansDao.getLoan(id);

            if (optLoan.isEmpty()) {
                log.warn("Dossier non trouvé : {}", id);
                return ResponseEntity.notFound().build();
            }

            CommonLoan loan = optLoan.get();

            // Conversion CommonLoan → DossierConsultationDto (modèle interne)
            DossierConsultationDto dto = toDossierConsultationDto(loan);

            // Résolution des noms via opentopazservice (PersonnesService)
            enrichirNomPersonnes(dto);

            log.debug("DossierConsultationDto construit et enrichi pour id={}", id);

            return ResponseEntity.ok(loan);

        } catch (DAOException e) {
            log.error("Erreur DAO lors de la consultation du dossier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── Mapper : CommonLoan → DossierConsultationDto ───────────────

    /**
     * Convertit un CommonLoan (modèle SIGAC externe) en DossierConsultationDto (modèle interne).
     * <p>
     * Les champs emprunteur/coEmprunteur ne sont PAS remplis ici — seuls les
     * identifiants personnes (noEmprunteur, noCoEmprunteur, efs) sont extraits
     * des Participant. La résolution des noms se fait ensuite via
     * PersonnesService (opentopazservice / Thrift Topaze).
     */
    DossierConsultationDto toDossierConsultationDto(CommonLoan loan) {
        DossierConsultationDto.Builder builder = DossierConsultationDto.builder();

        // Identifiants contrat
        builder.numeroContratSouscritPret(loan.getId());
        builder.numeroContratSouscritProjet(loan.getMasterContractId());

        // Durée et montants
        builder.dureePret(loan.getDuration());
        builder.montantPret(toDouble(loan.getBorrowedAmount()));
        builder.tauxRemboursement(toDouble(loan.getRate()));
        builder.montantDisponible(toDouble(loan.getAvailableAmount()));

        // Libellé et type code
        builder.libelleNature(loan.getLabel());
        builder.codeNature(loan.getTypeCode());

        // LoanType → codeNature / libelleNature
        if (loan.getLoanType() != null) {
            builder.codeNature(loan.getLoanType().getCode());
            builder.libelleNature(loan.getLoanType().getLabel());
        }

        // ObjectCode → codeObjet / libelleObjet
        if (loan.getObjectCode() != null) {
            builder.codeObjet(loan.getObjectCode().getCode());
            builder.libelleObjet(loan.getObjectCode().getLabel());
        }

        // LoanState → codeEtat / libelleEtat
        if (loan.getLoanState() != null) {
            builder.codeEtat(loan.getLoanState().getCode());
            builder.libelleEtat(loan.getLoanState().getLabel());
        }

        // Participants → noEmprunteur / noCoEmprunteur / efs (identifiants seulement)
        // Les noms (emprunteur, coEmprunteur) seront résolus via opentopazservice
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

    /**
     * Résout les noms emprunteur/coEmprunteur via PersonnesService
     * (opentopazservice / getInformationsMinimalesPersonnesTopaze).
     */
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

    // ── Utilitaires ───────────────────────────────────────────────

    private Double toDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
