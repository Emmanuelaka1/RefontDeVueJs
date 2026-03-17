package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.AbstractSpringBootTest;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration — LoansController avec RestAssured.
 * <p>
 * Profil "dev" activé :
 *   - LoansApiDaoMock fournit les données SIGAC (3 prêts mock format réel)
 *   - PersonnesMockDao fournit la résolution des noms
 * <p>
 * Teste le flux complet :
 *   LoansController → LoansApiDelegateImpl → LoansApiDaoMock → PersonnesMockDao
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class LoansControllerTest extends AbstractSpringBootTest {

    private static final String LOANS_PATH = "/api/v1/loans/";

    // ── GET /api/v1/loans/{numeroPret} ────────────────────────────

    @Test
    void getLoan_existant_retourneOkAvecDossierComplet() {
        // DD04063627 = prêt 1 dans LoansApiDaoMock, emprunteur 14336390 + co-emprunteur 14336391
        DossierConsultationDto dto = getResource(
                LOANS_PATH + "DD04063627",
                DossierConsultationDto.class
        );

        assertNotNull(dto);
        assertEquals("DD04063627", dto.getNumeroContratSouscritPret());
        assertEquals("DD04063627", dto.getNumeroContratSouscritProjet());
        assertEquals(240, dto.getDureePret());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(3.45, dto.getTauxRemboursement());
    }

    @Test
    void getLoan_existant_nomsResolusViaPersonnesService() {
        DossierConsultationDto dto = getResource(
                LOANS_PATH + "DD04063627",
                DossierConsultationDto.class
        );

        // PersonnesMockDao résout 14336390 → MARTIN Jean-Pierre, 14336391 → MARTIN Catherine
        assertEquals("14336390", dto.getNoEmprunteur());
        assertEquals("14336391", dto.getNoCoEmprunteur());
        assertEquals("01", dto.getEfs());
        assertEquals("MARTIN Jean-Pierre", dto.getEmprunteur());
        assertEquals("MARTIN Catherine", dto.getCoEmprunteur());
    }

    @Test
    void getLoan_existant_verifieMappingNatureEtatObjet() {
        DossierConsultationDto dto = getResource(
                LOANS_PATH + "DD04063627",
                DossierConsultationDto.class
        );

        // loanType.code → codeNature, loanType.label → libelleNature
        assertEquals("110309", dto.getCodeNature());
        assertEquals("ALTIMMO FIXE", dto.getLibelleNature());
        // loanState
        assertEquals("AA", dto.getCodeEtat());
        assertEquals("EN COURS NORMALE", dto.getLibelleEtat());
        // objectCode
        assertEquals("AA", dto.getCodeObjet());
        assertEquals("ACQUISITION ANCIEN", dto.getLibelleObjet());
    }

    @Test
    void getLoan_deuxiemePret_retourneOk() {
        DossierConsultationDto dto = getResource(
                LOANS_PATH + "AX12457845",
                DossierConsultationDto.class
        );

        assertNotNull(dto);
        assertEquals("AX12457845", dto.getNumeroContratSouscritPret());
        assertEquals(300, dto.getDureePret());
        assertEquals("200100", dto.getCodeNature());
        assertEquals("PRET ACCESSION SOCIALE", dto.getLibelleNature());
        assertEquals("DUPONT Marie", dto.getEmprunteur());
    }

    @Test
    void getLoan_sansCoEmprunteur_coEmprunteurNull() {
        DossierConsultationDto dto = getResource(
                LOANS_PATH + "BZ98765432",
                DossierConsultationDto.class
        );

        assertNotNull(dto);
        assertEquals("BZ98765432", dto.getNumeroContratSouscritPret());
        assertEquals("12004567", dto.getNoEmprunteur());
        assertEquals("LECLERC Sophie", dto.getEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
        assertNull(dto.getCoEmprunteur());
    }

    @Test
    void getLoan_inexistant_retourne404() {
        getError(LOANS_PATH + "INEXISTANT", 404);
    }
}
