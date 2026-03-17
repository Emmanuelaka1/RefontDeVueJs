package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import com.arkea.sgesapi.exception.DAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — PersonnesMockDao.
 * <p>
 * Identifiants alignés sur les personNumber du LoansApiDaoMock (format réel SIGAC).
 */
class PersonnesMockDaoTest {

    private PersonnesMockDao dao;

    @BeforeEach
    void setUp() {
        dao = new PersonnesMockDao();
        dao.initMockData();
    }

    @Test
    void getInformationsMinimalesPersonnes_identifiantExistant() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("14336390"));

        assertEquals(1, result.size());
        PersonneMinimaleDto personne = result.get("14336390");
        assertNotNull(personne);
        assertEquals("MARTIN", personne.getNom());
        assertEquals("Jean-Pierre", personne.getPrenom());
        assertEquals("PP", personne.getTypePersonne());
    }

    @Test
    void getInformationsMinimalesPersonnes_plusieursIdentifiants() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("14336390", "14336391", "15789012"));

        assertEquals(3, result.size());
        assertEquals("MARTIN", result.get("14336390").getNom());
        assertEquals("MARTIN", result.get("14336391").getNom());
        assertEquals("DUPONT", result.get("15789012").getNom());
    }

    @Test
    void getInformationsMinimalesPersonnes_identifiantInconnu() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("INCONNU"));

        assertTrue(result.isEmpty());
    }

    @Test
    void getInformationsMinimalesPersonnes_listeVide() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(Collections.emptyList());

        assertTrue(result.isEmpty());
    }

    @Test
    void getInformationsMinimalesPersonnes_avecNull_filtreNull() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("14336390"));

        assertEquals(1, result.size());
    }

    @Test
    void getInformationsMinimalesPersonnes_mixteExistantInconnu() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("14336390", "INCONNU"));

        assertEquals(1, result.size());
        assertNotNull(result.get("14336390"));
        assertNull(result.get("INCONNU"));
    }

    @Test
    void mockData_contient5Personnes() throws DAOException {
        List<String> allIds = List.of(
                "14336390", "14336391", "15789012", "15789013", "12004567"
        );

        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(allIds);

        assertEquals(5, result.size());
    }

    @Test
    void getLibelleComplet_formatCorrect() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("12004567"));

        PersonneMinimaleDto leclerc = result.get("12004567");
        assertEquals("LECLERC Sophie", leclerc.getLibelleComplet());
    }
}
