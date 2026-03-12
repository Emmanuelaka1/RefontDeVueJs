package com.arkea.sgesapi.delegate;

import com.arkea.sgesapi.api.sigac.PretsApiDelegate;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.service.DossierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du delegate généré depuis sigac-prets.yaml (OpenAPI 3.0.3).
 * <p>
 * Endpoints :
 *   GET /api/v1/prets         → listerDossiers()
 *   GET /api/v1/prets/{id}    → getDossier(id)
 * <p>
 * Ce delegate fait le pont entre les modèles générés (model.sigac.*)
 * et la couche métier (DossierService).
 * <p>
 * Utilise DossierService (au lieu de IDossierDao directement) afin
 * de bénéficier de la résolution des noms emprunteur/coEmprunteur
 * via getInformationsMinimalesPersonnes.
 */
@Service
public class PretsApiDelegateImpl implements PretsApiDelegate {

    private static final Logger log = LoggerFactory.getLogger(PretsApiDelegateImpl.class);

    private final DossierService dossierService;

    public PretsApiDelegateImpl(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    // ── GET /api/v1/prets ─────────────────────────────────────────
    @Override
    public ResponseEntity<ServiceResponseDossierResumeList> listerDossiers() {
        log.info("SIGAC — listerDossiers");

        try {
            RechercheCriteria criteria = RechercheCriteria.builder()
                    .page(0)
                    .taille(100)
                    .build();

            List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);

            List<DossierResume> resumes = resultats.stream()
                    .map(this::toSigacResume)
                    .collect(Collectors.toList());

            ServiceResponseDossierResumeList response = new ServiceResponseDossierResumeList();
            response.setData(resumes);
            response.setSuccess(true);
            response.setMessage("OK");

            return ResponseEntity.ok(response);
        } catch (DAOException e) {
            log.error("Erreur DAO lors de la recherche de dossiers", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── GET /api/v1/prets/{id} ────────────────────────────────────
    @Override
    public ResponseEntity<ServiceResponseDossierPret> getDossier(String id) {
        log.info("SIGAC — getDossier id={}", id);

        try {
            DossierConsultationDto dto = dossierService.consulterDossier(id);
            return ResponseEntity.ok(toSigacResponse(dto));
        } catch (DossierNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DAOException e) {
            log.error("Erreur DAO lors de la consultation du dossier {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── Mappers : DTO interne → modèle SIGAC généré ──────────────

    private DossierResume toSigacResume(DossierResumeDto dto) {
        DossierResume r = new DossierResume();
        r.setId(dto.getNumeroPret());
        r.setNoPret(dto.getNumeroPret());
        r.setEmprunteur(dto.getEmprunteur());
        r.setMontantPret(formatMontant(dto.getMontantPret()));
        r.setCodeEtat(dto.getCodeEtat() + " - " + dto.getLibelleEtat());
        return r;
    }

    private ServiceResponseDossierPret toSigacResponse(DossierConsultationDto dto) {
        // Données Générales
        DonneesGenerales dg = new DonneesGenerales();
        dg.setEmprunteur(dto.getEmprunteur());
        dg.setCoEmprunteur(dto.getCoEmprunteur() != null ? dto.getCoEmprunteur() : "");
        dg.setNoPret(dto.getNumeroPret());
        dg.setNoContratSouscritProjet(dto.getNumeroContratSouscritProjet());
        dg.setNoContratSouscritPret(dto.getNumeroContratSouscritPret());
        dg.setEfs(dto.getEfs());
        dg.setStructure(dto.getStructure());
        dg.setCodeEtat(dto.getCodeEtat() + " - " + dto.getLibelleEtat());
        dg.setCodeObjet(dto.getCodeObjet() + " - " + dto.getLibelleObjet());
        dg.setCodeNature(dto.getCodeNature() + " - " + dto.getLibelleNature());

        // Données Prêt
        DonneesPret dp = new DonneesPret();
        dp.setMontantPret(formatMontant(dto.getMontantPret()));
        dp.setDureePret(dto.getDureePret() + " mois");
        dp.setTauxRemboursement(formatTaux(dto.getTauxRemboursement()));
        dp.setTauxFranchise(formatTaux(dto.getTauxFranchise()));
        dp.setTauxBonification(formatTaux(dto.getTauxBonification()));
        dp.setAnticipation(Boolean.TRUE.equals(dto.getAnticipation()) ? "Oui" : "Non");
        dp.setTypeAmortissement(dto.getTypeAmortissement());
        dp.setOutilInstruction(dto.getOutilInstruction());
        dp.setMontantDebloque(formatMontant(dto.getMontantDebloque()));
        dp.setMontantDisponible(formatMontant(dto.getMontantDisponible()));
        dp.setMontantRA(formatMontant(dto.getMontantRA()));
        dp.setEncours(formatMontant(dto.getEncours()));
        dp.setTeg(formatTaux(dto.getTeg()));

        // Dates Prêt (valeurs par défaut — à brancher sur Topaze)
        DatesPret dates = new DatesPret();
        dates.setDateAcceptation("");
        dates.setDateAccord("");
        dates.setDateOuvertureCredit("");
        dates.setDatePassageGestion("");
        dates.setDateEffet("");
        dates.setDate1ereEcheance("");
        dates.setDateEffetRA("");
        dates.setDateEffetFP("");
        dates.setDateFinPret("");
        dates.setDate1ereEcheance2("");
        dates.setDatePrecedenteEcheance("");
        dates.setDateProchaineEcheance("");
        dates.setDateAbonnementPrecedent("");
        dates.setDateAbonnementSuivant("");
        dates.setDateTombeePrecedente("");
        dates.setDateTombeeSuivante("");

        // Assemblage DossierPret
        DossierPret dossier = new DossierPret();
        dossier.setId(dto.getNumeroPret());
        dossier.setDonneesGenerales(dg);
        dossier.setDonneesPret(dp);
        dossier.setDates(dates);

        // Enveloppe ServiceResponse
        ServiceResponseDossierPret response = new ServiceResponseDossierPret();
        response.setData(dossier);
        response.setSuccess(true);
        response.setMessage("OK");

        return response;
    }

    // ── Formatage ────────────────────────────────────────────────

    private String formatMontant(Double montant) {
        if (montant == null) return "0,00 €";
        return String.format("%,.2f €", montant).replace(',', ' ').replace('.', ',').replace(' ', ' ');
    }

    private String formatTaux(Double taux) {
        if (taux == null) return "0,00 %";
        return String.format("%.2f %%", taux).replace('.', ',');
    }
}
