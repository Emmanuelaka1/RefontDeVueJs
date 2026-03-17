package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.api.sigac.LoansApi;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.service.PersonnesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — LoansApiDelegateImpl.
 * <p>
 * Couvre :
 *  - searchLoans : flux nominal, 404, erreur interne
 *  - toDossierConsultationDto : mapping de tous les champs CommonLoan
 *  - enrichirNomPersonnes : résolution via PersonnesService, tolérance erreurs
 * <p>
 * Les données de test utilisent les formats réels SIGAC :
 *  - id : 10 caractères alphanumériques (DD04063627)
 *  - typeCode : 5 chiffres (10117)
 *  - personNumber : numérique (14336390)
 *  - personFederation : code EFS court (01)
 *  - label : null (le libellé vient de loanType.label)
 */
@ExtendWith(MockitoExtension.class)
class LoansApiDelegateImplTest {

    @Mock
    private LoansApi loansApi;

    @Mock
    private PersonnesService personnesService;

    @InjectMocks
    private LoansApiDelegateImpl delegate;

    // ── searchLoans — flux complet ─────────────────────────────────

    @Test
    void searchLoans_nominal_retourneOkAvecDossierEnrichi() {
        CommonLoan loan = createCompleteLoan();
        when(loansApi.getLoan("DD04063627")).thenReturn(ResponseEntity.ok(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("14336390", "14336391"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        ResponseEntity<DossierConsultationDto> response = delegate.searchLoans("DD04063627");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DossierConsultationDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("DD04063627", dto.getNumeroContratSouscritPret());
        assertEquals("MARTIN Jean-Pierre", dto.getEmprunteur());
        assertEquals("MARTIN Catherine", dto.getCoEmprunteur());
    }

    @Test
    void searchLoans_pretNonTrouve_retourne404() {
        when(loansApi.getLoan("INEXISTANT"))
                .thenReturn(ResponseEntity.notFound().build());

        ResponseEntity<DossierConsultationDto> response = delegate.searchLoans("INEXISTANT");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(personnesService);
    }

    @Test
    void searchLoans_bodyNull_retourne404() {
        when(loansApi.getLoan("VIDE"))
                .thenReturn(ResponseEntity.ok(null));

        ResponseEntity<DossierConsultationDto> response = delegate.searchLoans("VIDE");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(personnesService);
    }

    @Test
    void searchLoans_erreurLoansApi_retourne500() {
        when(loansApi.getLoan("DD04063627"))
                .thenThrow(new RuntimeException("Connexion SIGAC refusée"));

        ResponseEntity<DossierConsultationDto> response = delegate.searchLoans("DD04063627");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void searchLoans_appellePersonnesServicePourResoudreNoms() {
        CommonLoan loan = createCompleteLoan();
        when(loansApi.getLoan("DD04063627")).thenReturn(ResponseEntity.ok(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("14336390", "14336391"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        delegate.searchLoans("DD04063627");

        verify(personnesService).resoudreEmprunteurCoEmprunteur("14336390", "14336391");
    }

    @Test
    void searchLoans_erreurPersonnesService_retourneQuandMeme200() {
        CommonLoan loan = createCompleteLoan();
        when(loansApi.getLoan("DD04063627")).thenReturn(ResponseEntity.ok(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur(anyString(), anyString()))
                .thenThrow(new RuntimeException("Topaze indisponible"));

        ResponseEntity<DossierConsultationDto> response = delegate.searchLoans("DD04063627");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DossierConsultationDto dto = response.getBody();
        assertNotNull(dto);
        // Noms non résolus mais le dto est quand même retourné
        assertNull(dto.getEmprunteur());
        assertNull(dto.getCoEmprunteur());
    }

    // ── toDossierConsultationDto — mapping ─────────────────────────

    @Test
    void toDossierConsultationDto_mappeIdentifiantsContrat() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("DD04063627", dto.getNumeroContratSouscritPret());
        assertEquals("DD04063627", dto.getNumeroContratSouscritProjet());
    }

    @Test
    void toDossierConsultationDto_mappeDureeEtMontant() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals(240, dto.getDureePret());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(3.45, dto.getTauxRemboursement());
        assertEquals(0.0, dto.getMontantDisponible());
    }

    @Test
    void toDossierConsultationDto_mappeLoanStateObjectCodeLoanType() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("AA", dto.getCodeEtat());
        assertEquals("EN COURS NORMALE", dto.getLibelleEtat());
        assertEquals("110309", dto.getCodeNature());
        assertEquals("ALTIMMO FIXE", dto.getLibelleNature());
        assertEquals("AA", dto.getCodeObjet());
        assertEquals("ACQUISITION ANCIEN", dto.getLibelleObjet());
    }

    @Test
    void toDossierConsultationDto_extraitIdentifiantsPersonnesSansNoms() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("14336390", dto.getNoEmprunteur());
        assertEquals("14336391", dto.getNoCoEmprunteur());
        assertEquals("01", dto.getEfs());
        // Noms NON remplis — résolus ensuite via PersonnesService
        assertNull(dto.getEmprunteur());
        assertNull(dto.getCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_sansCoEmprunteur() {
        CommonLoan loan = createCompleteLoan();
        loan.setParticipants(List.of(loan.getParticipants().get(0)));

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("14336390", dto.getNoEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_montantNull() {
        CommonLoan loan = createCompleteLoan();
        loan.setBorrowedAmount(null);
        loan.setRate(null);
        loan.setAvailableAmount(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getMontantPret());
        assertNull(dto.getTauxRemboursement());
        assertNull(dto.getMontantDisponible());
    }

    @Test
    void toDossierConsultationDto_sansParticipants() {
        CommonLoan loan = createCompleteLoan();
        loan.setParticipants(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getNoEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
        assertNull(dto.getEfs());
    }

    @Test
    void toDossierConsultationDto_sansLoanType_utiliseLabelEtTypeCode() {
        CommonLoan loan = createCompleteLoan();
        loan.setLoanType(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        // Fallback sur typeCode et label quand loanType est absent
        assertEquals("10117", dto.getCodeNature());
        // label est null dans le vrai SIGAC — pas de libellé fallback
        assertNull(dto.getLibelleNature());
    }

    @Test
    void toDossierConsultationDto_sansObjectCode() {
        CommonLoan loan = createCompleteLoan();
        loan.setObjectCode(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getCodeObjet());
        assertNull(dto.getLibelleObjet());
    }

    @Test
    void toDossierConsultationDto_sansLoanState() {
        CommonLoan loan = createCompleteLoan();
        loan.setLoanState(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getCodeEtat());
        assertNull(dto.getLibelleEtat());
    }

    // ── Helper ────────────────────────────────────────────────────

    /**
     * Crée un CommonLoan complet avec les formats réels SIGAC :
     * - id/masterContractId : 10 chars alphanumériques
     * - typeCode : 5 chiffres
     * - loanType.code : 6 chiffres
     * - personNumber : numérique
     * - personFederation : code EFS court (01, 03)
     * - label : null (comme le vrai SIGAC)
     */
    private CommonLoan createCompleteLoan() {
        CommonLoan loan = new CommonLoan();
        loan.setId("DD04063627");
        loan.setMasterContractId("DD04063627");
        loan.setDuration(240);
        loan.setBorrowedAmount(BigDecimal.valueOf(250000.0));
        loan.setRate(BigDecimal.valueOf(3.45));
        loan.setAvailableAmount(BigDecimal.valueOf(0.0));
        loan.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan.setLabel(null);  // null dans le vrai SIGAC
        loan.setTypeCode("10117");

        LoanType loanType = new LoanType();
        loanType.setCode("110309");
        loanType.setLabel("ALTIMMO FIXE");
        loan.setLoanType(loanType);

        ObjectCode objectCode = new ObjectCode();
        objectCode.setCode("AA");
        objectCode.setLabel("ACQUISITION ANCIEN");
        loan.setObjectCode(objectCode);

        LoanState loanState = new LoanState();
        loanState.setCode("AA");
        loanState.setLabel("EN COURS NORMALE");
        loan.setLoanState(loanState);

        Participant emp = new Participant();
        emp.setPersonNumber("14336390");
        emp.setPersonFederation("01");
        emp.setRoleCode("EMP");

        Participant coe = new Participant();
        coe.setPersonNumber("14336391");
        coe.setPersonFederation("01");
        coe.setRoleCode("COE");

        loan.setParticipants(List.of(emp, coe));

        return loan;
    }
}
