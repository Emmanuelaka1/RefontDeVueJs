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
                dao.getInformationsMinimalesPersonnes(List.of("PP-001547-E"));

        assertEquals(1, result.size());
        PersonneMinimaleDto personne = result.get("PP-001547-E");
        assertNotNull(personne);
        assertEquals("MARTIN", personne.getNom());
        assertEquals("Jean-Pierre", personne.getPrenom());
        assertEquals("PP", personne.getTypePersonne());
    }

    @Test
    void getInformationsMinimalesPersonnes_plusieursIdentifiants() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("PP-001547-E", "PP-001547-C", "PP-002891-E"));

        assertEquals(3, result.size());
        assertEquals("MARTIN", result.get("PP-001547-E").getNom());
        assertEquals("MARTIN", result.get("PP-001547-C").getNom());
        assertEquals("DUPONT", result.get("PP-002891-E").getNom());
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
                dao.getInformationsMinimalesPersonnes(List.of("PP-001547-E"));

        assertEquals(1, result.size());
    }

    @Test
    void getInformationsMinimalesPersonnes_mixteExistantInconnu() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("PP-001547-E", "INCONNU"));

        assertEquals(1, result.size());
        assertNotNull(result.get("PP-001547-E"));
        assertNull(result.get("INCONNU"));
    }

    @Test
    void mockData_contient8Personnes() throws DAOException {
        List<String> allIds = List.of(
                "PP-001547-E", "PP-001547-C", "PP-002891-E", "PP-002891-C",
                "PP-000412-E", "PP-003102-E", "PP-003102-C", "PP-001890-E"
        );

        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(allIds);

        assertEquals(8, result.size());
    }

    @Test
    void getLibelleComplet_formatCorrect() throws DAOException {
        Map<String, PersonneMinimaleDto> result =
                dao.getInformationsMinimalesPersonnes(List.of("PP-001890-E"));

        PersonneMinimaleDto nguyen = result.get("PP-001890-E");
        assertEquals("NGUYEN Van Thi", nguyen.getLibelleComplet());
    }
}
