package com.arkea.sgesapi.service;

import com.arkea.sgesapi.dao.api.IDossierDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.dao.model.RechercheCriteria;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    void rechercherDossiers_retourneResultats() {
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
    void consulterDossier_existant_resoutPersonnes() {
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
    void consulterDossier_inexistant_lanceException() {
        // Given
        when(dossierDao.consulterDossier("INVALID")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(DossierNotFoundException.class, () ->
                dossierService.consulterDossier("INVALID"));
    }

    @Test
    void consulterDossier_sansCoEmprunteur_resoutEmprunteurSeul() {
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
}
