package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import com.arkea.sgesapi.exception.DAOException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implémentation Mock du DAO Personnes.
 * <p>
 * Fournit des données de démonstration pour le développement frontend.
 * Sera remplacée par PersonnesThriftDao en production (via Topaze).
 * <p>
 * Activé uniquement avec le profil Spring "dev".
 * En production (profil "prod"), PersonnesThriftDao prend le relais.
 */
@Repository
@Profile("dev")
public class PersonnesMockDao implements IPersonnesDao {

    private static final Logger log = LoggerFactory.getLogger(PersonnesMockDao.class);

    private final Map<String, PersonneMinimaleDto> personnesStore = new HashMap<>();

    @PostConstruct
    void initMockData() {
        log.info("Initialisation des données mock pour les personnes");

        // Identifiants alignés sur les personNumber du LoansApiDaoMock (format réel SIGAC)
        personnesStore.put("14336390", new PersonneMinimaleDto("14336390", "MARTIN", "Jean-Pierre", "PP"));
        personnesStore.put("14336391", new PersonneMinimaleDto("14336391", "MARTIN", "Catherine", "PP"));
        personnesStore.put("15789012", new PersonneMinimaleDto("15789012", "DUPONT", "Marie", "PP"));
        personnesStore.put("15789013", new PersonneMinimaleDto("15789013", "DUPONT", "François", "PP"));
        personnesStore.put("12004567", new PersonneMinimaleDto("12004567", "LECLERC", "Sophie", "PP"));

        log.info("{} personnes mock initialisées", personnesStore.size());
    }

    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(List<String> identifiantsPersonnes)
            throws DAOException {
        log.debug("Mock — getInformationsMinimalesPersonnes nb={}", identifiantsPersonnes.size());

        return identifiantsPersonnes.stream()
                .filter(id -> id != null && personnesStore.containsKey(id))
                .collect(Collectors.toMap(
                        id -> id,
                        personnesStore::get
                ));
    }
}
