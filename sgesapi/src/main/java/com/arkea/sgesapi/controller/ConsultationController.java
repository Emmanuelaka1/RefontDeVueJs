package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.service.DossierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST — Consultation d'un dossier de prêt.
 * <p>
 * Correspond à l'écran de consultation (capture fournie) avec :
 * - Données Générales (emprunteur, N° prêt, EFS, structure, états, codes)
 * - Données Prêt (montant, durée, taux, amortissement, encours, TEG)
 * <p>
 * On accède à cet écran en sélectionnant un dossier depuis la recherche (home).
 */
@RestController
@RequestMapping("/api/v1/dossiers")
@Tag(name = "Consultation", description = "Consultation détaillée d'un dossier de prêt")
@CrossOrigin(origins = "*")
public class ConsultationController {

    private static final Logger log = LoggerFactory.getLogger(ConsultationController.class);

    private final DossierService dossierService;

    public ConsultationController(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    /**
     * GET /api/v1/dossiers/{numeroPret}
     * Récupère toutes les données d'un dossier pour l'écran de consultation.
     */
    @GetMapping("/{numeroPret}")
    @Operation(summary = "Consulter un dossier de prêt",
               description = "Récupère les données générales et données prêt d'un dossier. "
                           + "Redirigé depuis l'écran de recherche après sélection d'un dossier.")
    public ResponseEntity<ConsultationResponse> consulterDossier(
            @PathVariable String numeroPret) throws DAOException {

        log.info("Consultation dossier — N° prêt : {}", numeroPret);

        DossierConsultationDto dossier = dossierService.consulterDossier(numeroPret);

        ConsultationResponse response = new ConsultationResponse(
                new DonneesGenerales(
                        dossier.getEmprunteur(),
                        dossier.getCoEmprunteur(),
                        dossier.getNumeroPret(),
                        dossier.getNumeroContratSouscritProjet(),
                        dossier.getNumeroContratSouscritPret(),
                        dossier.getEfs(),
                        dossier.getStructure(),
                        dossier.getCodeEtat() + " - " + dossier.getLibelleEtat(),
                        dossier.getCodeObjet() + " - " + dossier.getLibelleObjet(),
                        dossier.getCodeNature() + " - " + dossier.getLibelleNature()
                ),
                new DonneesPret(
                        dossier.getMontantPret(),
                        dossier.getDureePret(),
                        dossier.getTauxRemboursement(),
                        dossier.getTauxFranchise(),
                        dossier.getTauxBonification(),
                        dossier.getAnticipation(),
                        dossier.getTypeAmortissement(),
                        dossier.getOutilInstruction(),
                        dossier.getMontantDebloque(),
                        dossier.getMontantDisponible(),
                        dossier.getMontantRA(),
                        dossier.getEncours(),
                        dossier.getTeg()
                )
        );

        return ResponseEntity.ok(response);
    }

    // ── Records de réponse ────────────────────────────────────────

    public record ConsultationResponse(
            DonneesGenerales donneesGenerales,
            DonneesPret donneesPret
    ) {}

    public record DonneesGenerales(
            String emprunteur,
            String coEmprunteur,
            String numeroPret,
            String numeroContratSouscritProjet,
            String numeroContratSouscritPret,
            String efs,
            String structure,
            String codeEtat,
            String codeObjet,
            String codeNature
    ) {}

    public record DonneesPret(
            Double montantPret,
            Integer dureePret,
            Double tauxRemboursement,
            Double tauxFranchise,
            Double tauxBonification,
            Boolean anticipation,
            String typeAmortissement,
            String outilInstruction,
            Double montantDebloque,
            Double montantDisponible,
            Double montantRA,
            Double encours,
            Double teg
    ) {}
}
