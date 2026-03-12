package com.arkea.sgesapi.service;

import com.arkea.sgesapi.dao.api.IDossierDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier — gestion des dossiers de prêts.
 * <p>
 * Consomme le DAO par interface (IDossierDao). Aucune dépendance
 * directe sur Thrift ou toute autre source de données.
 * <p>
 * Utilise PersonnesService pour résoudre les identifiants Topaze
 * (noEmprunteur, noCoEmprunteur) en noms complets via
 * getInformationsMinimalesPersonnes.
 * <p>
 * Les erreurs DAO (DAOException) sont propagées vers le contrôleur
 * et gérées par le GlobalExceptionHandler.
 */
@Service
public class DossierService {

    private static final Logger log = LoggerFactory.getLogger(DossierService.class);

    private final IDossierDao dossierDao;
    private final PersonnesService personnesService;

    public DossierService(IDossierDao dossierDao, PersonnesService personnesService) {
        this.dossierDao = dossierDao;
        this.personnesService = personnesService;
    }

    /**
     * Recherche de dossiers par critères multiples.
     * Point d'entrée pour l'écran de recherche (home).
     * <p>
     * Les noms emprunteur/coEmprunteur sont résolus via PersonnesService
     * pour chaque résultat.
     *
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    public List<DossierResumeDto> rechercherDossiers(RechercheCriteria criteria) throws DAOException {
        log.info("Recherche dossiers — critères : {}", criteria);
        List<DossierResumeDto> resultats = dossierDao.rechercherDossiers(criteria);

        // Résolution des noms emprunteur/coEmprunteur pour chaque résumé
        for (DossierResumeDto resume : resultats) {
            enrichirNomPersonnes(resume);
        }

        return resultats;
    }

    /**
     * Compte le total de résultats pour la pagination.
     *
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    public long compterDossiers(RechercheCriteria criteria) throws DAOException {
        return dossierDao.compterDossiers(criteria);
    }

    /**
     * Consultation complète d'un dossier par numéro de prêt.
     * Redirigé depuis l'écran de recherche après sélection.
     * <p>
     * Les champs emprunteur/coEmprunteur sont résolus via
     * getInformationsMinimalesPersonnes(noEmprunteur, noCoEmprunteur)
     * du service Topaze.
     *
     * @throws DossierNotFoundException si le dossier n'existe pas
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    public DossierConsultationDto consulterDossier(String numeroPret) throws DAOException {
        log.info("Consultation dossier — N° prêt : {}", numeroPret);
        DossierConsultationDto dossier = dossierDao.consulterDossier(numeroPret)
                .orElseThrow(() -> new DossierNotFoundException(numeroPret));

        // Résolution emprunteur/coEmprunteur via Topaze (getInformationsMinimalesPersonnes)
        enrichirNomPersonnes(dossier);

        return dossier;
    }

    // ── Enrichissement des noms via PersonnesService ───────────────

    /**
     * Résout les noms emprunteur/coEmprunteur pour un DossierConsultationDto
     * en appelant PersonnesService.resoudreEmprunteurCoEmprunteur().
     */
    private void enrichirNomPersonnes(DossierConsultationDto dossier) {
        String noEmp = dossier.getNoEmprunteur();
        String noCoEmp = dossier.getNoCoEmprunteur();

        if ((noEmp == null || noEmp.isBlank()) && (noCoEmp == null || noCoEmp.isBlank())) {
            // Pas d'identifiants personnes, on garde les valeurs déjà présentes
            return;
        }

        try {
            String[] libelles = personnesService.resoudreEmprunteurCoEmprunteur(noEmp, noCoEmp);
            if (libelles[0] != null) {
                dossier.setEmprunteur(libelles[0]);
            }
            if (libelles[1] != null) {
                dossier.setCoEmprunteur(libelles[1]);
            }
            log.debug("Personnes résolues — emprunteur={}, coEmprunteur={}", libelles[0], libelles[1]);
        } catch (Exception e) {
            log.warn("Impossible de résoudre les personnes pour le dossier {} : {}",
                    dossier.getNumeroPret(), e.getMessage());
            // On garde les valeurs non-résolues (noEmprunteur comme fallback possible)
        }
    }

    /**
     * Résout les noms emprunteur/coEmprunteur pour un DossierResumeDto
     * en appelant PersonnesService.resoudreEmprunteurCoEmprunteur().
     */
    private void enrichirNomPersonnes(DossierResumeDto resume) {
        String noEmp = resume.getNoEmprunteur();
        String noCoEmp = resume.getNoCoEmprunteur();

        if ((noEmp == null || noEmp.isBlank()) && (noCoEmp == null || noCoEmp.isBlank())) {
            return;
        }

        try {
            String[] libelles = personnesService.resoudreEmprunteurCoEmprunteur(noEmp, noCoEmp);
            if (libelles[0] != null) {
                resume.setEmprunteur(libelles[0]);
            }
            if (libelles[1] != null) {
                resume.setCoEmprunteur(libelles[1]);
            }
        } catch (Exception e) {
            log.warn("Impossible de résoudre les personnes pour le résumé {} : {}",
                    resume.getNumeroPret(), e.getMessage());
        }
    }
}
