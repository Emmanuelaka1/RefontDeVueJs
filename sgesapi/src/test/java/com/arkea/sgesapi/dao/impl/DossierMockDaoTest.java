package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — DossierMockDao.
 */
class DossierMockDaoTest {

    private DossierMockDao dao;

    @BeforeEach
    void setUp() {
        dao = new DossierMockDao();
        dao.initMockData();
    }

    // ── rechercherDossiers ────────────────────────────────────────

    @Test
    void rechercherDossiers_sansCritere_retourneTous() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(100).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(5, resultats.size());
    }

    @Test
    void rechercherDossiers_parNumeroPret() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .numeroPret("2024-PAP-001547")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
        assertEquals("2024-PAP-001547", resultats.get(0).getNumeroPret());
    }

    @Test
    void rechercherDossiers_parEfs() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .efs("13807")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(2, resultats.size());
    }

    @Test
    void rechercherDossiers_parCodeEtat() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .codeEtat("40")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(3, resultats.size());
    }

    @Test
    void rechercherDossiers_parCodeNature() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .codeNature("PAP")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(2, resultats.size());
    }

    @Test
    void rechercherDossiers_parStructure() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .structure("Bretagne")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
        assertEquals("2024-PAS-002891", resultats.get(0).getNumeroPret());
    }

    @Test
    void rechercherDossiers_pagination_premierePage() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(2).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(2, resultats.size());
    }

    @Test
    void rechercherDossiers_pagination_deuxiemePage() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(1).taille(2).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(2, resultats.size());
    }

    @Test
    void rechercherDossiers_pagination_dernierePage() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(2).taille(2).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
    }

    @Test
    void rechercherDossiers_aucunResultat() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .numeroPret("INEXISTANT")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertTrue(resultats.isEmpty());
    }

    @Test
    void rechercherDossiers_caseInsensitive_numeroPret() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .numeroPret("2024-pap-001547")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
    }

    // ── compterDossiers ───────────────────────────────────────────

    @Test
    void compterDossiers_sansCritere_retourne5() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(100).build();

        long total = dao.compterDossiers(criteria);

        assertEquals(5, total);
    }

    @Test
    void compterDossiers_avecCritere_compteFiltre() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .codeEtat("40")
                .page(0).taille(20).build();

        long total = dao.compterDossiers(criteria);

        assertEquals(3, total);
    }

    // ── consulterDossier ──────────────────────────────────────────

    @Test
    void consulterDossier_existant_retourneDossier() throws DAOException {
        Optional<DossierConsultationDto> result = dao.consulterDossier("2024-PAP-001547");

        assertTrue(result.isPresent());
        DossierConsultationDto dossier = result.get();
        assertEquals("2024-PAP-001547", dossier.getNumeroPret());
        assertEquals("PP-001547-E", dossier.getNoEmprunteur());
        assertEquals("PP-001547-C", dossier.getNoCoEmprunteur());
        assertEquals(250000.00, dossier.getMontantPret());
        assertEquals(240, dossier.getDureePret());
    }

    @Test
    void consulterDossier_inexistant_retourneVide() throws DAOException {
        Optional<DossierConsultationDto> result = dao.consulterDossier("INEXISTANT");

        assertTrue(result.isEmpty());
    }

    @Test
    void consulterDossier_caseInsensitive() throws DAOException {
        Optional<DossierConsultationDto> result = dao.consulterDossier("2024-pap-001547");

        assertTrue(result.isPresent());
    }

    @Test
    void consulterDossier_sansCoEmprunteur() throws DAOException {
        Optional<DossierConsultationDto> result = dao.consulterDossier("2023-PAP-000412");

        assertTrue(result.isPresent());
        assertNull(result.get().getNoCoEmprunteur());
    }

    // ── Filtrage par prenomEmprunteur et nomEmprunteur ─────────────

    @Test
    void rechercherDossiers_parPrenomEmprunteur_sansLibelleResolu_retourneVide() throws DAOException {
        // prenomEmprunteur filtre sur d.getEmprunteur() qui est null en mock (pas encore résolu)
        RechercheCriteria criteria = RechercheCriteria.builder()
                .prenomEmprunteur("Jean")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        // emprunteur est null dans les mocks → aucun ne matche
        assertTrue(resultats.isEmpty());
    }

    @Test
    void rechercherDossiers_parNomEmprunteur_fallbackNoEmprunteur() throws DAOException {
        // nomEmprunteur with a value matching noEmprunteur (emprunteur is null in mock)
        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur("PP-001547")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
        assertEquals("2024-PAP-001547", resultats.get(0).getNumeroPret());
    }

    @Test
    void rechercherDossiers_parNomEmprunteur_noMatchNiEmprunteurNiNoEmp() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur("INCONNU_TOTAL")
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertTrue(resultats.isEmpty());
    }

    // ── toResume mapping ──────────────────────────────────────────

    @Test
    void toResume_mappeLesChamps() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .numeroPret("2024-PAP-001547")
                .page(0).taille(1).build();

        List<DossierResumeDto> resultats = dao.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
        DossierResumeDto resume = resultats.get(0);
        assertEquals("2024-PAP-001547", resume.getNumeroPret());
        assertEquals("PP-001547-E", resume.getNoEmprunteur());
        assertEquals("PP-001547-C", resume.getNoCoEmprunteur());
        assertEquals("13807", resume.getEfs());
        assertEquals("40", resume.getCodeEtat());
        assertEquals("PAP", resume.getCodeNature());
        assertEquals(250000.00, resume.getMontantPret());
    }
}
