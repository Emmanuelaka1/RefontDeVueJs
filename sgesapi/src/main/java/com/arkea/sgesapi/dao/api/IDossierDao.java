package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'accès aux dossiers de prêts.
 * Abstraction permettant de switcher entre Thrift Topaze, mock, ou autre source.
 */
public interface IDossierDao {

    /**
     * Recherche de dossiers par critères multiples.
     *
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    List<DossierResumeDto> rechercherDossiers(RechercheCriteria criteria) throws DAOException;

    /**
     * Compte le nombre total de résultats pour la pagination.
     *
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    long compterDossiers(RechercheCriteria criteria) throws DAOException;

    /**
     * Consultation complète d'un dossier par numéro de prêt.
     *
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    Optional<DossierConsultationDto> consulterDossier(String numeroPret) throws DAOException;

    /**
     * Consultation complète d'un dossier par numéro de contrat souscrit.
     * Utilisé par la Loans API (GET /loans/{id}) où id = numéro contrat souscrit.
     *
     * @param numeroContratSouscrit numéro contrat souscrit prêt (ex: "PRT-2024-08-1547")
     * @throws DAOException en cas d'erreur d'accès aux données
     */
    Optional<DossierConsultationDto> consulterDossierParContratSouscrit(String numeroContratSouscrit) throws DAOException;
}
