package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.IDossierDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation Mock du DAO Dossiers.
 * <p>
 * Fournit des données de démonstration pour le développement frontend.
 * Sera remplacée par DossierThriftDao en production (avec Topaze).
 * <p>
 * Les identifiants personnes (noEmprunteur, noCoEmprunteur) sont stockés
 * dans les dossiers. La résolution des noms complets est effectuée par
 * DossierService via PersonnesService.getInformationsMinimalesPersonnes().
 */
@Repository
public class DossierMockDao implements IDossierDao {

    private static final Logger log = LoggerFactory.getLogger(DossierMockDao.class);

    private final List<DossierConsultationDto> dossiers = new ArrayList<>();

    @PostConstruct
    void initMockData() {
        log.info("Initialisation des données mock pour les dossiers de prêts");

        dossiers.add(DossierConsultationDto.builder()
                .noEmprunteur("PP-001547-E")
                .noCoEmprunteur("PP-001547-C")
                .numeroPret("2024-PAP-001547")
                .numeroContratSouscritProjet("PRJ-2024-08-1547")
                .numeroContratSouscritPret("PRT-2024-08-1547")
                .efs("13807")
                .structure("CIF Île-de-France")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeObjet("01")
                .libelleObjet("Acquisition ancien")
                .codeNature("PAP")
                .libelleNature("Prêt à l'Accession à la Propriété")
                .montantPret(250000.00)
                .dureePret(240)
                .tauxRemboursement(3.45)
                .tauxFranchise(0.00)
                .tauxBonification(0.00)
                .anticipation(false)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(250000.00)
                .montantDisponible(0.00)
                .montantRA(0.00)
                .encours(237845.12)
                .teg(3.72)
                .build());

        dossiers.add(DossierConsultationDto.builder()
                .noEmprunteur("PP-002891-E")
                .noCoEmprunteur("PP-002891-C")
                .numeroPret("2024-PAS-002891")
                .numeroContratSouscritProjet("PRJ-2024-10-2891")
                .numeroContratSouscritPret("PRT-2024-10-2891")
                .efs("13808")
                .structure("CIF Bretagne")
                .codeEtat("30")
                .libelleEtat("En déblocage")
                .codeObjet("02")
                .libelleObjet("Construction")
                .codeNature("PAS")
                .libelleNature("Prêt d'Accession Sociale")
                .montantPret(180000.00)
                .dureePret(300)
                .tauxRemboursement(2.85)
                .tauxFranchise(0.50)
                .tauxBonification(0.00)
                .anticipation(false)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(120000.00)
                .montantDisponible(60000.00)
                .montantRA(0.00)
                .encours(120000.00)
                .teg(3.15)
                .build());

        dossiers.add(DossierConsultationDto.builder()
                .noEmprunteur("PP-000412-E")
                .noCoEmprunteur(null)
                .numeroPret("2023-PAP-000412")
                .numeroContratSouscritProjet("PRJ-2023-03-0412")
                .numeroContratSouscritPret("PRT-2023-03-0412")
                .efs("13807")
                .structure("CIF Île-de-France")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeObjet("03")
                .libelleObjet("Travaux")
                .codeNature("PAP")
                .libelleNature("Prêt à l'Accession à la Propriété")
                .montantPret(75000.00)
                .dureePret(180)
                .tauxRemboursement(3.10)
                .tauxFranchise(0.00)
                .tauxBonification(0.25)
                .anticipation(false)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(75000.00)
                .montantDisponible(0.00)
                .montantRA(0.00)
                .encours(62340.50)
                .teg(3.35)
                .build());

        dossiers.add(DossierConsultationDto.builder()
                .noEmprunteur("PP-003102-E")
                .noCoEmprunteur("PP-003102-C")
                .numeroPret("2024-PTZ-003102")
                .numeroContratSouscritProjet("PRJ-2024-11-3102")
                .numeroContratSouscritPret("PRT-2024-11-3102")
                .efs("13810")
                .structure("CIF Sud-Ouest")
                .codeEtat("20")
                .libelleEtat("En instruction")
                .codeObjet("01")
                .libelleObjet("Acquisition ancien")
                .codeNature("PTZ")
                .libelleNature("Prêt à Taux Zéro")
                .montantPret(40000.00)
                .dureePret(300)
                .tauxRemboursement(0.00)
                .tauxFranchise(0.00)
                .tauxBonification(0.00)
                .anticipation(false)
                .typeAmortissement("Différé partiel")
                .outilInstruction("GIPSI")
                .montantDebloque(0.00)
                .montantDisponible(40000.00)
                .montantRA(0.00)
                .encours(0.00)
                .teg(0.00)
                .build());

        dossiers.add(DossierConsultationDto.builder()
                .noEmprunteur("PP-001890-E")
                .noCoEmprunteur(null)
                .numeroPret("2023-PEL-001890")
                .numeroContratSouscritProjet("PRJ-2023-06-1890")
                .numeroContratSouscritPret("PRT-2023-06-1890")
                .efs("13809")
                .structure("CIF Rhône-Alpes")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeObjet("01")
                .libelleObjet("Acquisition ancien")
                .codeNature("PEL")
                .libelleNature("Prêt Épargne Logement")
                .montantPret(92000.00)
                .dureePret(180)
                .tauxRemboursement(2.20)
                .tauxFranchise(0.00)
                .tauxBonification(1.00)
                .anticipation(true)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(92000.00)
                .montantDisponible(0.00)
                .montantRA(5000.00)
                .encours(71200.00)
                .teg(2.45)
                .build());

        log.info("{} dossiers mock initialisés", dossiers.size());
    }

    @Override
    public List<DossierResumeDto> rechercherDossiers(RechercheCriteria criteria) {
        log.debug("Recherche mock avec critères : {}", criteria);

        return dossiers.stream()
                .filter(d -> matchCriteria(d, criteria))
                .skip((long) criteria.getPage() * criteria.getTaille())
                .limit(criteria.getTaille())
                .map(this::toResume)
                .collect(Collectors.toList());
    }

    @Override
    public long compterDossiers(RechercheCriteria criteria) {
        return dossiers.stream()
                .filter(d -> matchCriteria(d, criteria))
                .count();
    }

    @Override
    public Optional<DossierConsultationDto> consulterDossier(String numeroPret) {
        log.debug("Consultation mock pour le numéro de prêt : {}", numeroPret);
        return dossiers.stream()
                .filter(d -> d.getNumeroPret().equalsIgnoreCase(numeroPret))
                .findFirst();
    }

    // ── Filtrage par critères ─────────────────────────────────────
    private boolean matchCriteria(DossierConsultationDto d, RechercheCriteria c) {
        // Note: le filtrage par nom emprunteur se fait sur le champ résolu (emprunteur)
        // qui sera rempli par DossierService après appel à PersonnesService.
        // En mock, on peut aussi filtrer sur noEmprunteur si besoin.
        if (c.getNomEmprunteur() != null && !c.getNomEmprunteur().isBlank()) {
            String empLib = d.getEmprunteur();
            if (empLib == null || !empLib.toLowerCase().contains(c.getNomEmprunteur().toLowerCase())) {
                // Fallback : chercher dans noEmprunteur
                String noEmp = d.getNoEmprunteur();
                if (noEmp == null || !noEmp.toLowerCase().contains(c.getNomEmprunteur().toLowerCase())) {
                    return false;
                }
            }
        }
        if (c.getPrenomEmprunteur() != null && !c.getPrenomEmprunteur().isBlank()) {
            String empLib = d.getEmprunteur();
            if (empLib == null || !empLib.toLowerCase().contains(c.getPrenomEmprunteur().toLowerCase())) {
                return false;
            }
        }
        if (c.getNumeroPret() != null && !c.getNumeroPret().isBlank()) {
            if (!d.getNumeroPret().toLowerCase().contains(c.getNumeroPret().toLowerCase())) {
                return false;
            }
        }
        if (c.getEfs() != null && !c.getEfs().isBlank()) {
            if (!d.getEfs().equals(c.getEfs())) {
                return false;
            }
        }
        if (c.getStructure() != null && !c.getStructure().isBlank()) {
            if (!d.getStructure().toLowerCase().contains(c.getStructure().toLowerCase())) {
                return false;
            }
        }
        if (c.getCodeEtat() != null && !c.getCodeEtat().isBlank()) {
            if (!d.getCodeEtat().equals(c.getCodeEtat())) {
                return false;
            }
        }
        if (c.getCodeNature() != null && !c.getCodeNature().isBlank()) {
            if (!d.getCodeNature().equalsIgnoreCase(c.getCodeNature())) {
                return false;
            }
        }
        return true;
    }

    private DossierResumeDto toResume(DossierConsultationDto d) {
        return DossierResumeDto.builder()
                .numeroPret(d.getNumeroPret())
                .noEmprunteur(d.getNoEmprunteur())
                .noCoEmprunteur(d.getNoCoEmprunteur())
                .emprunteur(d.getEmprunteur())
                .coEmprunteur(d.getCoEmprunteur())
                .efs(d.getEfs())
                .structure(d.getStructure())
                .codeEtat(d.getCodeEtat())
                .libelleEtat(d.getLibelleEtat())
                .codeNature(d.getCodeNature())
                .libelleNature(d.getLibelleNature())
                .montantPret(d.getMontantPret())
                .tauxRemboursement(d.getTauxRemboursement())
                .build();
    }
}
