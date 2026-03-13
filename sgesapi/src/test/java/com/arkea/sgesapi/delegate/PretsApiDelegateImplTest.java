package com.arkea.sgesapi.delegate;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import com.arkea.sgesapi.model.sigac.ServiceResponseDossierPret;
import com.arkea.sgesapi.model.sigac.ServiceResponseDossierResumeList;
import com.arkea.sgesapi.service.DossierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires — PretsApiDelegateImpl.
 */
@ExtendWith(MockitoExtension.class)
class PretsApiDelegateImplTest {

    @Mock
    private DossierService dossierService;

    @InjectMocks
    private PretsApiDelegateImpl delegate;

    // ── listerDossiers ────────────────────────────────────────────

    @Test
    void listerDossiers_retourneListeSucces() throws DAOException {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-PAP-001547")
                .emprunteur("MARTIN Jean-Pierre")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .montantPret(250000.0)
                .build();

        when(dossierService.rechercherDossiers(any())).thenReturn(List.of(resume));

        ResponseEntity<ServiceResponseDossierResumeList> response = delegate.listerDossiers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void listerDossiers_listeVide_retourneOk() throws DAOException {
        when(dossierService.rechercherDossiers(any())).thenReturn(List.of());

        ResponseEntity<ServiceResponseDossierResumeList> response = delegate.listerDossiers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData().isEmpty());
    }

    @Test
    void listerDossiers_erreurDAO_retourne500() throws DAOException {
        when(dossierService.rechercherDossiers(any())).thenThrow(new DAOException("Erreur Topaze"));

        ResponseEntity<ServiceResponseDossierResumeList> response = delegate.listerDossiers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ── getDossier ────────────────────────────────────────────────

    @Test
    void getDossier_existant_retourneOk() throws DAOException {
        DossierConsultationDto dto = DossierConsultationDto.builder()
                .numeroPret("2024-PAP-001547")
                .emprunteur("MARTIN Jean-Pierre")
                .coEmprunteur("MARTIN Catherine")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
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

        when(dossierService.consulterDossier("2024-PAP-001547")).thenReturn(dto);

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("2024-PAP-001547");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("2024-PAP-001547", response.getBody().getData().getId());
    }

    @Test
    void getDossier_inexistant_retourne404() throws DAOException {
        when(dossierService.consulterDossier("INEXISTANT"))
                .thenThrow(new DossierNotFoundException("INEXISTANT"));

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("INEXISTANT");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getDossier_erreurDAO_retourne500() throws DAOException {
        when(dossierService.consulterDossier("2024-PAP-001547"))
                .thenThrow(new DAOException("Erreur connexion"));

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("2024-PAP-001547");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ── formatMontant / formatTaux (via les résultats) ───────────

    @Test
    void listerDossiers_formateMontantCorrectement() throws DAOException {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-001")
                .montantPret(250000.0)
                .codeEtat("40")
                .libelleEtat("En gestion")
                .build();

        when(dossierService.rechercherDossiers(any())).thenReturn(List.of(resume));

        ResponseEntity<ServiceResponseDossierResumeList> response = delegate.listerDossiers();

        String montant = response.getBody().getData().get(0).getMontantPret();
        assertNotNull(montant);
        assertTrue(montant.contains("€"));
    }

    @Test
    void listerDossiers_montantNull_formateZero() throws DAOException {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-001")
                .montantPret(null)
                .codeEtat("40")
                .libelleEtat("En gestion")
                .build();

        when(dossierService.rechercherDossiers(any())).thenReturn(List.of(resume));

        ResponseEntity<ServiceResponseDossierResumeList> response = delegate.listerDossiers();

        String montant = response.getBody().getData().get(0).getMontantPret();
        assertTrue(montant.contains("0,00"));
    }

    @Test
    void getDossier_anticipationTrue_afficheOui() throws DAOException {
        DossierConsultationDto dto = createCompleteDossier();
        dto.setAnticipation(true);

        when(dossierService.consulterDossier("2024-001")).thenReturn(dto);

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("2024-001");

        assertEquals("Oui", response.getBody().getData().getDonneesPret().getAnticipation());
    }

    @Test
    void getDossier_anticipationFalse_afficheNon() throws DAOException {
        DossierConsultationDto dto = createCompleteDossier();
        dto.setAnticipation(false);

        when(dossierService.consulterDossier("2024-001")).thenReturn(dto);

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("2024-001");

        assertEquals("Non", response.getBody().getData().getDonneesPret().getAnticipation());
    }

    @Test
    void getDossier_coEmprunteurNull_videEnSortie() throws DAOException {
        DossierConsultationDto dto = createCompleteDossier();
        dto.setCoEmprunteur(null);

        when(dossierService.consulterDossier("2024-001")).thenReturn(dto);

        ResponseEntity<ServiceResponseDossierPret> response = delegate.getDossier("2024-001");

        assertEquals("", response.getBody().getData().getDonneesGenerales().getCoEmprunteur());
    }

    // ── Helper ────────────────────────────────────────────────────

    private DossierConsultationDto createCompleteDossier() {
        return DossierConsultationDto.builder()
                .numeroPret("2024-001")
                .emprunteur("MARTIN Jean")
                .coEmprunteur("MARTIN Catherine")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
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
    }
}
