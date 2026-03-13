package com.arkea.sgesapi.service;

import com.arkea.sgesapi.dao.api.IDossierDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — DossierService.
 * Mock du DAO et PersonnesService pour tester la logique métier.
 */
@ExtendWith(MockitoExtension.class)
class DossierServiceTest {

    @Mock
    private IDossierDao dossierDao;

    @Mock
    private PersonnesService personnesService;

    @InjectMocks
    private DossierService dossierService;

    @Test
    void rechercherDossiers_retourneResultats() throws DAOException {
        // Given
        RechercheCriteria criteria = RechercheCriteria.builder()
                .nomEmprunteur("MARTIN")
                .page(0)
                .taille(20)
                .build();

        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-PAP-001547")
                .noEmprunteur("PP-001547-E")
                .efs("13807")
                .build();

        when(dossierDao.rechercherDossiers(any())).thenReturn(List.of(resume));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", null))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", null});

        // When
        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);

        // Then
        assertEquals(1, resultats.size());
        assertEquals("MARTIN Jean-Pierre", resultats.get(0).getEmprunteur());
    }

    @Test
    void consulterDossier_existant_resoutPersonnes() throws DAOException {
        // Given
        DossierConsultationDto dossier = DossierConsultationDto.builder()
                .numeroPret("2024-PAP-001547")
                .noEmprunteur("PP-001547-E")
                .noCoEmprunteur("PP-001547-C")
                .montantPret(250000.00)
                .build();

        when(dossierDao.consulterDossier("2024-PAP-001547")).thenReturn(Optional.of(dossier));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        // When
        DossierConsultationDto result = dossierService.consulterDossier("2024-PAP-001547");

        // Then
        assertNotNull(result);
        assertEquals("MARTIN Jean-Pierre", result.getEmprunteur());
        assertEquals("MARTIN Catherine", result.getCoEmprunteur());
        assertEquals(250000.00, result.getMontantPret());
    }

    @Test
    void consulterDossier_inexistant_lanceException() throws DAOException {
        // Given
        when(dossierDao.consulterDossier("INVALID")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(DossierNotFoundException.class, () ->
                dossierService.consulterDossier("INVALID"));
    }

    @Test
    void consulterDossier_sansCoEmprunteur_resoutEmprunteurSeul() throws DAOException {
        // Given
        DossierConsultationDto dossier = DossierConsultationDto.builder()
                .numeroPret("2023-PAP-000412")
                .noEmprunteur("PP-000412-E")
                .noCoEmprunteur(null)
                .montantPret(75000.00)
                .build();

        when(dossierDao.consulterDossier("2023-PAP-000412")).thenReturn(Optional.of(dossier));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-000412-E", null))
                .thenReturn(new String[]{"LECLERC Sophie", null});

        // When
        DossierConsultationDto result = dossierService.consulterDossier("2023-PAP-000412");

        // Then
        assertNotNull(result);
        assertEquals("LECLERC Sophie", result.getEmprunteur());
        assertNull(result.getCoEmprunteur());
    }

    @Test
    void rechercherDossiers_erreurDAO_propageException() throws DAOException {
        // Given
        when(dossierDao.rechercherDossiers(any())).thenThrow(new DAOException("Erreur Topaze"));

        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0)
                .taille(20)
                .build();

        // When / Then
        assertThrows(DAOException.class, () ->
                dossierService.rechercherDossiers(criteria));
    }

    // ── compterDossiers ───────────────────────────────────────────

    @Test
    void compterDossiers_delegueAuDAO() throws DAOException {
        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(20).build();

        when(dossierDao.compterDossiers(any())).thenReturn(42L);

        long total = dossierService.compterDossiers(criteria);

        assertEquals(42L, total);
        verify(dossierDao).compterDossiers(criteria);
    }

    @Test
    void compterDossiers_erreurDAO_propageException() throws DAOException {
        when(dossierDao.compterDossiers(any())).thenThrow(new DAOException("Erreur"));

        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(20).build();

        assertThrows(DAOException.class, () ->
                dossierService.compterDossiers(criteria));
    }

    // ── enrichirNomPersonnes — cas limites ─────────────────────────

    @Test
    void rechercherDossiers_listeVide_retourneListeVide() throws DAOException {
        when(dossierDao.rechercherDossiers(any())).thenReturn(Collections.emptyList());

        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);

        assertTrue(resultats.isEmpty());
        verifyNoInteractions(personnesService);
    }

    @Test
    void rechercherDossiers_sansIdentifiantsPersonne_neResoutPas() throws DAOException {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur(null)
                .noCoEmprunteur(null)
                .build();

        when(dossierDao.rechercherDossiers(any())).thenReturn(List.of(resume));

        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);

        assertEquals(1, resultats.size());
        verifyNoInteractions(personnesService);
    }

    @Test
    void consulterDossier_erreurPersonnesService_gardeValeurs() throws DAOException {
        DossierConsultationDto dossier = DossierConsultationDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
                .montantPret(100000.0)
                .build();

        when(dossierDao.consulterDossier("2024-001")).thenReturn(Optional.of(dossier));
        when(personnesService.resoudreEmprunteurCoEmprunteur(anyString(), anyString()))
                .thenThrow(new RuntimeException("Erreur inattendue"));

        DossierConsultationDto result = dossierService.consulterDossier("2024-001");

        assertNotNull(result);
        assertNull(result.getEmprunteur());
    }

    @Test
    void consulterDossier_sansIdentifiantPersonne_neResoutPas() throws DAOException {
        DossierConsultationDto dossier = DossierConsultationDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur(null)
                .noCoEmprunteur(null)
                .montantPret(50000.0)
                .build();

        when(dossierDao.consulterDossier("2024-001")).thenReturn(Optional.of(dossier));

        DossierConsultationDto result = dossierService.consulterDossier("2024-001");

        assertNotNull(result);
        verifyNoInteractions(personnesService);
    }

    @Test
    void consulterDossier_erreurDAO_propageException() throws DAOException {
        when(dossierDao.consulterDossier("2024-001")).thenThrow(new DAOException("Erreur Topaze"));

        assertThrows(DAOException.class, () ->
                dossierService.consulterDossier("2024-001"));
    }

    @Test
    void rechercherDossiers_enrichitPlusieursResumes() throws DAOException {
        DossierResumeDto resume1 = DossierResumeDto.builder()
                .numeroPret("2024-001")
                .noEmprunteur("PP-001-E")
                .build();
        DossierResumeDto resume2 = DossierResumeDto.builder()
                .numeroPret("2024-002")
                .noEmprunteur("PP-002-E")
                .build();

        when(dossierDao.rechercherDossiers(any())).thenReturn(List.of(resume1, resume2));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001-E", null))
                .thenReturn(new String[]{"MARTIN Jean", null});
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-002-E", null))
                .thenReturn(new String[]{"DUPONT Marie", null});

        RechercheCriteria criteria = RechercheCriteria.builder()
                .page(0).taille(20).build();

        List<DossierResumeDto> resultats = dossierService.rechercherDossiers(criteria);

        assertEquals(2, resultats.size());
        assertEquals("MARTIN Jean", resultats.get(0).getEmprunteur());
        assertEquals("DUPONT Marie", resultats.get(1).getEmprunteur());
    }
}
