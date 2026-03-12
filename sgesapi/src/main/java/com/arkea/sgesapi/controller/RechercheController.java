package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.service.DossierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST — Écran de recherche (home).
 * <p>
 * L'écran de recherche est le point d'entrée principal de l'application.
 * L'utilisateur saisit des critères, reçoit une liste de résultats,
 * puis sélectionne un dossier pour accéder à la consultation.
 */
@RestController
@RequestMapping("/api/v1/recherche")
@Tag(name = "Recherche", description = "Écran de recherche principal — nouveau Home")
@CrossOrigin(origins = "*")
public class RechercheController {

    private static final Logger log = LoggerFactory.getLogger(RechercheController.class);

    private final DossierService dossierService;

    public RechercheController(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    /**
     * POST /api/v1/recherche/dossiers
     * Recherche multicritères de dossiers de prêts.
     */
    @PostMapping("/dossiers")
    @Operation(summary = "Recherche de dossiers de prêts",
               description = "Recherche par nom, numéro de prêt, EFS, structure, état, nature. "
                           + "Résultats paginés. Sélectionner un dossier pour accéder à la consultation.")
    public ResponseEntity<Map<String, Object>> rechercherDossiers(
            @RequestBody RechercheRequest request) throws DAOException {

        log.info("Recherche dossiers — requête : {}", request);

        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur(request.nomEmprunteur())
                .prenomEmprunteur(request.prenomEmprunteur())
                .numeroPret(request.numeroPret())
                .efs(request.efs())
                .structure(request.structure())
                .codeEtat(request.codeEtat())
                .codeNature(request.codeNature())
                .page(request.page() != null ? request.page() : 0)
                .taille(request.taille() != null ? request.taille() : 20)
                .build();

        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);
        long total = dossierService.compterDossiers(criteria);

        return ResponseEntity.ok(Map.of(
                "dossiers", resultats,
                "totalElements", total,
                "page", criteria.getPage(),
                "taille", criteria.getTaille()
        ));
    }

    /**
     * GET /api/v1/recherche/dossiers — Recherche rapide par query string
     * Utile pour un champ de recherche rapide (autocomplete).
     */
    @GetMapping("/dossiers")
    @Operation(summary = "Recherche rapide de dossiers",
               description = "Recherche simplifiée par terme unique (nom ou numéro de prêt)")
    public ResponseEntity<Map<String, Object>> rechercheRapide(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int taille) throws DAOException {

        log.info("Recherche rapide — q='{}' page={} taille={}", q, page, taille);

        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur(q)
                .page(page)
                .taille(taille)
                .build();

        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);
        long total = dossierService.compterDossiers(criteria);

        return ResponseEntity.ok(Map.of(
                "dossiers", resultats,
                "totalElements", total,
                "page", page,
                "taille", taille
        ));
    }

    /**
     * Record de requête de recherche multicritères.
     */
    public record RechercheRequest(
            String nomEmprunteur,
            String prenomEmprunteur,
            String numeroPret,
            String efs,
            String structure,
            String codeEtat,
            String codeNature,
            Integer page,
            Integer taille
    ) {}
}
