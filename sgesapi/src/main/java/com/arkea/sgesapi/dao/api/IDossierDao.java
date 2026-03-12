package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;

import java.util.List;
import java.util.Optional;

/**
 * Interface DAO pour l'accès aux dossiers de prêts.
 * Abstraction permettant de switcher entre Thrift Topaze, mock, ou autre source.
 */
public interface IDossierDao {

    /**
     * Recherche de dossiers par critères multiples.
     */
    List<DossierResumeDto> rechercherDossiers(RechercheCriteria criteria);

    /**
     * Compte le nombre total de résultats pour la pagination.
     */
    long compterDossiers(RechercheCriteria criteria);

    /**
     * Consultation complète d'un dossier par numéro de prêt.
     */
    Optional<DossierConsultationDto> consulterDossier(String numeroPret);
}
