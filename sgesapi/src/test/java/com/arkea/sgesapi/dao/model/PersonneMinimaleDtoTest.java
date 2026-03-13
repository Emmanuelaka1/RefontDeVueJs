package com.arkea.sgesapi.dao.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — PersonneMinimaleDto.
 */
class PersonneMinimaleDtoTest {

    @Test
    void getLibelleComplet_avecPrenom_retourneNomEtPrenom() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PP-001", "MARTIN", "Jean-Pierre", "PP");

        assertEquals("MARTIN Jean-Pierre", dto.getLibelleComplet());
    }

    @Test
    void getLibelleComplet_sansPrenom_retourneNomSeul() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PM-001", "SARL DUPONT", null, "PM");

        assertEquals("SARL DUPONT", dto.getLibelleComplet());
    }

    @Test
    void getLibelleComplet_prenomVide_retourneNomSeul() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PM-001", "SCI MARTIN", "", "PM");

        assertEquals("SCI MARTIN", dto.getLibelleComplet());
    }

    @Test
    void getLibelleComplet_prenomBlanc_retourneNomSeul() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PM-001", "ENTREPRISE", "   ", "PM");

        assertEquals("ENTREPRISE", dto.getLibelleComplet());
    }

    @Test
    void constructeurVide_champsTousNull() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto();

        assertNull(dto.getIdentifiant());
        assertNull(dto.getNom());
        assertNull(dto.getPrenom());
        assertNull(dto.getTypePersonne());
    }

    @Test
    void settersEtGetters() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto();
        dto.setIdentifiant("PP-002");
        dto.setNom("DUPONT");
        dto.setPrenom("Marie");
        dto.setTypePersonne("PP");

        assertEquals("PP-002", dto.getIdentifiant());
        assertEquals("DUPONT", dto.getNom());
        assertEquals("Marie", dto.getPrenom());
        assertEquals("PP", dto.getTypePersonne());
    }

    @Test
    void toString_contientIdentifiant() {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PP-001", "MARTIN", "Jean", "PP");

        String str = dto.toString();

        assertTrue(str.contains("PP-001"));
        assertTrue(str.contains("MARTIN"));
        assertTrue(str.contains("Jean"));
        assertTrue(str.contains("PP"));
    }
}
