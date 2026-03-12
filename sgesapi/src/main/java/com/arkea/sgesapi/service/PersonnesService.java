package com.arkea.sgesapi.service;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import com.arkea.sgesapi.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service métier — gestion des personnes (PP/PM).
 * <p>
 * Consomme le DAO par interface (IPersonnesDao).
 * Fournit des méthodes de commodité pour résoudre les identifiants Topaze
 * en libellés complets (NOM Prénom).
 * <p>
 * Les erreurs DAO (DAOException) sont propagées ou encapsulées
 * selon le contexte d'appel.
 */
@Service
public class PersonnesService {

    private static final Logger log = LoggerFactory.getLogger(PersonnesService.class);

    private final IPersonnesDao personnesDao;

    public PersonnesService(IPersonnesDao personnesDao) {
        this.personnesDao = personnesDao;
    }

    /**
     * Récupère les informations minimales de personnes par leurs identifiants Topaze.
     *
     * @param identifiantsPersonnes liste d'identifiants (noEmprunteur, noCoEmprunteur, etc.)
     * @return Map identifiant → PersonneMinimaleDto
     * @throws DAOException en cas d'erreur d'accès Topaze
     */
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
            List<String> identifiantsPersonnes) throws DAOException {
        log.info("Service — getInformationsMinimalesPersonnes nb={}", identifiantsPersonnes.size());
        return personnesDao.getInformationsMinimalesPersonnes(identifiantsPersonnes);
    }

    /**
     * Résout un identifiant personne en libellé complet "NOM Prénom".
     * Retourne null si l'identifiant est null, inconnu, ou en cas d'erreur DAO.
     *
     * @param identifiant identifiant Topaze de la personne
     * @return libellé "NOM Prénom" ou null
     */
    public String resoudreLibellePersonne(String identifiant) {
        if (identifiant == null || identifiant.isBlank()) {
            return null;
        }
        try {
            Map<String, PersonneMinimaleDto> result =
                    personnesDao.getInformationsMinimalesPersonnes(List.of(identifiant));
            PersonneMinimaleDto personne = result.get(identifiant);
            return personne != null ? personne.getLibelleComplet() : null;
        } catch (DAOException e) {
            log.warn("Impossible de résoudre la personne {} : {}", identifiant, e.getMessage());
            return null;
        }
    }

    /**
     * Résout les identifiants emprunteur et coEmprunteur en une seule requête groupée.
     * Retourne un tableau de 2 éléments : [libelléEmprunteur, libelléCoEmprunteur].
     * Chaque élément peut être null si l'identifiant correspondant est null ou inconnu.
     * <p>
     * Les erreurs DAO sont attrapées et loguées — les libellés restent null en cas d'erreur.
     *
     * @param noEmprunteur identifiant Topaze de l'emprunteur
     * @param noCoEmprunteur identifiant Topaze du co-emprunteur (peut être null)
     * @return tableau [libelléEmprunteur, libelléCoEmprunteur]
     */
    public String[] resoudreEmprunteurCoEmprunteur(String noEmprunteur, String noCoEmprunteur) {
        List<String> identifiants = new ArrayList<>();
        if (noEmprunteur != null && !noEmprunteur.isBlank()) {
            identifiants.add(noEmprunteur);
        }
        if (noCoEmprunteur != null && !noCoEmprunteur.isBlank()) {
            identifiants.add(noCoEmprunteur);
        }

        if (identifiants.isEmpty()) {
            return new String[]{null, null};
        }

        Map<String, PersonneMinimaleDto> personnes;
        try {
            personnes = personnesDao.getInformationsMinimalesPersonnes(identifiants);
        } catch (DAOException e) {
            log.warn("Erreur DAO lors de la résolution des personnes : {}", e.getMessage());
            return new String[]{null, null};
        }

        String libelleEmprunteur = null;
        String libelleCoEmprunteur = null;

        if (noEmprunteur != null) {
            PersonneMinimaleDto emp = personnes.get(noEmprunteur);
            libelleEmprunteur = emp != null ? emp.getLibelleComplet() : null;
        }
        if (noCoEmprunteur != null) {
            PersonneMinimaleDto coEmp = personnes.get(noCoEmprunteur);
            libelleCoEmprunteur = coEmp != null ? coEmp.getLibelleComplet() : null;
        }

        return new String[]{libelleEmprunteur, libelleCoEmprunteur};
    }
}
