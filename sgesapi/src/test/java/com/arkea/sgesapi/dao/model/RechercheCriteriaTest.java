package com.arkea.sgesapi.dao.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — RechercheCriteria.
 */
class RechercheCriteriaTest {

    @Test
    void builder_tousLesCriteres() {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur("MARTIN")
                .prenomEmprunteur("Jean")
                .numeroPret("2024-PAP")
                .efs("13807")
                .structure("CIF")
                .codeEtat("40")
                .codeNature("PAP")
                .page(0)
                .taille(20)
                .build();

        assertEquals("MARTIN", criteria.getNomEmprunteur());
        assertEquals("Jean", criteria.getPrenomEmprunteur());
        assertEquals("2024-PAP", criteria.getNumeroPret());
        assertEquals("13807", criteria.getEfs());
        assertEquals("CIF", criteria.getStructure());
        assertEquals("40", criteria.getCodeEtat());
        assertEquals("PAP", criteria.getCodeNature());
        assertEquals(0, criteria.getPage());
        assertEquals(20, criteria.getTaille());
    }

    @Test
    void builder_criteresMinimaux() {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0)
                .taille(10)
                .build();

        assertNull(criteria.getNomEmprunteur());
        assertNull(criteria.getNumeroPret());
        assertEquals(0, criteria.getPage());
        assertEquals(10, criteria.getTaille());
    }

    @Test
    void constructeurVide_valeurParDefaut() {
        RechercheCriteria criteria = new RechercheCriteria();

        assertNull(criteria.getNomEmprunteur());
        assertEquals(0, criteria.getPage());
        assertEquals(0, criteria.getTaille());
    }

    @Test
    void settersEtGetters() {
        RechercheCriteria criteria = new RechercheCriteria();
        criteria.setNomEmprunteur("DUPONT");
        criteria.setPrenomEmprunteur("Marie");
        criteria.setNumeroPret("2024-001");
        criteria.setEfs("13808");
        criteria.setStructure("CIF Bretagne");
        criteria.setCodeEtat("30");
        criteria.setCodeNature("PAS");
        criteria.setPage(1);
        criteria.setTaille(50);

        assertEquals("DUPONT", criteria.getNomEmprunteur());
        assertEquals("Marie", criteria.getPrenomEmprunteur());
        assertEquals("2024-001", criteria.getNumeroPret());
        assertEquals("13808", criteria.getEfs());
        assertEquals("CIF Bretagne", criteria.getStructure());
        assertEquals("30", criteria.getCodeEtat());
        assertEquals("PAS", criteria.getCodeNature());
        assertEquals(1, criteria.getPage());
        assertEquals(50, criteria.getTaille());
    }

    @Test
    void toString_contientCriteres() {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur("MARTIN")
                .page(0)
                .taille(20)
                .build();

        String str = criteria.toString();

        assertTrue(str.contains("MARTIN"));
        assertTrue(str.contains("RechercheCriteria"));
    }
}
