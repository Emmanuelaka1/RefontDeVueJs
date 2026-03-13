package com.arkea.sgesapi.dao.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — DossierResumeDto.
 */
class DossierResumeDtoTest {

    @Test
    void builder_tousLesChamps() {
        DossierResumeDto dto = DossierResumeDto.builder()
                .numeroPret("2024-PAP-001")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
                .emprunteur("MARTIN Jean")
                .coEmprunteur("MARTIN Catherine")
                .efs("13807")
                .structure("CIF IDF")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeNature("PAP")
                .libelleNature("Prêt à l'Accession")
                .montantPret(250000.0)
                .tauxRemboursement(3.45)
                .build();

        assertEquals("2024-PAP-001", dto.getNumeroPret());
        assertEquals("PP-001-E", dto.getNoEmprunteur());
        assertEquals("PP-001-C", dto.getNoCoEmprunteur());
        assertEquals("MARTIN Jean", dto.getEmprunteur());
        assertEquals("MARTIN Catherine", dto.getCoEmprunteur());
        assertEquals("13807", dto.getEfs());
        assertEquals("CIF IDF", dto.getStructure());
        assertEquals("40", dto.getCodeEtat());
        assertEquals("En gestion", dto.getLibelleEtat());
        assertEquals("PAP", dto.getCodeNature());
        assertEquals("Prêt à l'Accession", dto.getLibelleNature());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(3.45, dto.getTauxRemboursement());
    }

    @Test
    void constructeurVide_champsTousNull() {
        DossierResumeDto dto = new DossierResumeDto();

        assertNull(dto.getNumeroPret());
        assertNull(dto.getEmprunteur());
        assertNull(dto.getMontantPret());
    }

    @Test
    void setters_modifientEmprunteur() {
        DossierResumeDto dto = DossierResumeDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur("PP-001-E")
                .build();

        assertNull(dto.getEmprunteur());

        dto.setEmprunteur("DUPONT Marie");
        assertEquals("DUPONT Marie", dto.getEmprunteur());

        dto.setCoEmprunteur("DUPONT François");
        assertEquals("DUPONT François", dto.getCoEmprunteur());
    }

    @Test
    void setters_tousLesChamps() {
        DossierResumeDto dto = new DossierResumeDto();
        dto.setNumeroPret("2024-001");
        dto.setNoEmprunteur("PP-001-E");
        dto.setNoCoEmprunteur("PP-001-C");
        dto.setEmprunteur("MARTIN Jean");
        dto.setCoEmprunteur("MARTIN Catherine");
        dto.setEfs("13807");
        dto.setStructure("CIF IDF");
        dto.setCodeEtat("40");
        dto.setLibelleEtat("En gestion");
        dto.setCodeNature("PAP");
        dto.setLibelleNature("PAP");
        dto.setMontantPret(250000.0);
        dto.setTauxRemboursement(3.45);

        assertEquals("2024-001", dto.getNumeroPret());
        assertEquals("PP-001-E", dto.getNoEmprunteur());
        assertEquals("PP-001-C", dto.getNoCoEmprunteur());
        assertEquals("MARTIN Jean", dto.getEmprunteur());
        assertEquals("MARTIN Catherine", dto.getCoEmprunteur());
        assertEquals("13807", dto.getEfs());
        assertEquals("CIF IDF", dto.getStructure());
        assertEquals("40", dto.getCodeEtat());
        assertEquals("En gestion", dto.getLibelleEtat());
        assertEquals("PAP", dto.getCodeNature());
        assertEquals("PAP", dto.getLibelleNature());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(3.45, dto.getTauxRemboursement());
    }
}
