package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
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
 * Marqué @Primary pour prendre la priorité sur PersonnesThriftDao en dev.
 */
@Repository
@Primary
public class PersonnesMockDao implements IPersonnesDao {

    private static final Logger log = LoggerFactory.getLogger(PersonnesMockDao.class);

    private final Map<String, PersonneMinimaleDto> personnesStore = new HashMap<>();

    @PostConstruct
    void initMockData() {
        log.info("Initialisation des données mock pour les personnes");

        personnesStore.put("PP-001547-E", new PersonneMinimaleDto("PP-001547-E", "MARTIN", "Jean-Pierre", "PP"));
        personnesStore.put("PP-001547-C", new PersonneMinimaleDto("PP-001547-C", "MARTIN", "Catherine", "PP"));
        personnesStore.put("PP-002891-E", new PersonneMinimaleDto("PP-002891-E", "DUPONT", "Marie", "PP"));
        personnesStore.put("PP-002891-C", new PersonneMinimaleDto("PP-002891-C", "DUPONT", "François", "PP"));
        personnesStore.put("PP-000412-E", new PersonneMinimaleDto("PP-000412-E", "LECLERC", "Sophie", "PP"));
        personnesStore.put("PP-003102-E", new PersonneMinimaleDto("PP-003102-E", "BERNARD", "Alain", "PP"));
        personnesStore.put("PP-003102-C", new PersonneMinimaleDto("PP-003102-C", "BERNARD", "Nathalie", "PP"));
        personnesStore.put("PP-001890-E", new PersonneMinimaleDto("PP-001890-E", "NGUYEN", "Van Thi", "PP"));

        log.info("{} personnes mock initialisées", personnesStore.size());
    }

    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(List<String> identifiantsPersonnes) {
        log.debug("Mock — getInformationsMinimalesPersonnes nb={}", identifiantsPersonnes.size());

        return identifiantsPersonnes.stream()
                .filter(id -> id != null && personnesStore.containsKey(id))
                .collect(Collectors.toMap(
                        id -> id,
                        personnesStore::get
                ));
    }
}
