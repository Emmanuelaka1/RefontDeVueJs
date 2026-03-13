package com.arkea.sgesapi.dao.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — DossierConsultationDto.
 */
class DossierConsultationDtoTest {

    @Test
    void builder_champsComplets() {
        DossierConsultationDto dto = DossierConsultationDto.builder()
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
                .emprunteur("MARTIN Jean")
                .coEmprunteur("MARTIN Catherine")
                .numeroPret("2024-PAP-001")
                .numeroContratSouscritProjet("PRJ-2024")
                .numeroContratSouscritPret("PRT-2024")
                .efs("13807")
                .structure("CIF IDF")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeObjet("01")
                .libelleObjet("Acquisition")
                .codeNature("PAP")
                .libelleNature("PAP")
                .montantPret(250000.0)
                .dureePret(240)
                .tauxRemboursement(3.45)
                .tauxFranchise(0.0)
                .tauxBonification(0.0)
                .anticipation(false)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(250000.0)
                .montantDisponible(0.0)
                .montantRA(0.0)
                .encours(237845.12)
                .teg(3.72)
                .build();

        assertEquals("PP-001-E", dto.getNoEmprunteur());
        assertEquals("PP-001-C", dto.getNoCoEmprunteur());
        assertEquals("2024-PAP-001", dto.getNumeroPret());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(240, dto.getDureePret());
        assertEquals(3.45, dto.getTauxRemboursement());
        assertFalse(dto.getAnticipation());
        assertEquals("GIPSI", dto.getOutilInstruction());
        assertEquals(237845.12, dto.getEncours());
        assertEquals(3.72, dto.getTeg());
    }

    @Test
    void constructeurVide_champsTousNull() {
        DossierConsultationDto dto = new DossierConsultationDto();

        assertNull(dto.getNumeroPret());
        assertNull(dto.getMontantPret());
        assertNull(dto.getDureePret());
        assertNull(dto.getAnticipation());
    }

    @Test
    void setters_modifientEmprunteurs() {
        DossierConsultationDto dto = DossierConsultationDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
                .build();

        assertNull(dto.getEmprunteur());
        assertNull(dto.getCoEmprunteur());

        dto.setEmprunteur("MARTIN Jean");
        dto.setCoEmprunteur("MARTIN Catherine");

        assertEquals("MARTIN Jean", dto.getEmprunteur());
        assertEquals("MARTIN Catherine", dto.getCoEmprunteur());
    }

    @Test
    void setters_donneesPret() {
        DossierConsultationDto dto = new DossierConsultationDto();
        dto.setMontantPret(100000.0);
        dto.setDureePret(180);
        dto.setTauxRemboursement(2.5);
        dto.setTauxFranchise(0.5);
        dto.setTauxBonification(0.25);
        dto.setAnticipation(true);
        dto.setTypeAmortissement("Différé partiel");
        dto.setOutilInstruction("GIPSI");
        dto.setMontantDebloque(50000.0);
        dto.setMontantDisponible(50000.0);
        dto.setMontantRA(1000.0);
        dto.setEncours(49000.0);
        dto.setTeg(2.75);

        assertEquals(100000.0, dto.getMontantPret());
        assertEquals(180, dto.getDureePret());
        assertTrue(dto.getAnticipation());
        assertEquals(49000.0, dto.getEncours());
    }

    @Test
    void setters_donneesGenerales() {
        DossierConsultationDto dto = new DossierConsultationDto();
        dto.setNoEmprunteur("PP-001-E");
        dto.setNoCoEmprunteur("PP-001-C");
        dto.setNumeroPret("2024-001");
        dto.setNumeroContratSouscritProjet("PRJ-2024");
        dto.setNumeroContratSouscritPret("PRT-2024");
        dto.setEfs("13807");
        dto.setStructure("CIF IDF");
        dto.setCodeEtat("40");
        dto.setLibelleEtat("En gestion");
        dto.setCodeObjet("01");
        dto.setLibelleObjet("Acquisition");
        dto.setCodeNature("PAP");
        dto.setLibelleNature("PAP");

        assertEquals("PP-001-E", dto.getNoEmprunteur());
        assertEquals("PP-001-C", dto.getNoCoEmprunteur());
        assertEquals("2024-001", dto.getNumeroPret());
        assertEquals("PRJ-2024", dto.getNumeroContratSouscritProjet());
        assertEquals("PRT-2024", dto.getNumeroContratSouscritPret());
        assertEquals("13807", dto.getEfs());
        assertEquals("CIF IDF", dto.getStructure());
        assertEquals("40", dto.getCodeEtat());
        assertEquals("En gestion", dto.getLibelleEtat());
        assertEquals("01", dto.getCodeObjet());
        assertEquals("Acquisition", dto.getLibelleObjet());
        assertEquals("PAP", dto.getCodeNature());
        assertEquals("PAP", dto.getLibelleNature());
    }

    @Test
    void setters_donneesFinancieres_complet() {
        DossierConsultationDto dto = new DossierConsultationDto();
        dto.setTauxFranchise(0.5);
        dto.setTauxBonification(0.25);
        dto.setMontantDebloque(50000.0);
        dto.setMontantDisponible(25000.0);
        dto.setMontantRA(5000.0);
        dto.setTeg(2.75);

        assertEquals(0.5, dto.getTauxFranchise());
        assertEquals(0.25, dto.getTauxBonification());
        assertEquals(50000.0, dto.getMontantDebloque());
        assertEquals(25000.0, dto.getMontantDisponible());
        assertEquals(5000.0, dto.getMontantRA());
        assertEquals(2.75, dto.getTeg());
    }
}
