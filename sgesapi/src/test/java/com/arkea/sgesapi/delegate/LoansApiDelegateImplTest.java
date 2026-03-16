package com.arkea.sgesapi.delegate;

import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.exception.DAOException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — LoansApiDelegateImpl.
 * <p>
 * Couvre le endpoint GET /loans/{id} et le mapping
 * CommonLoan (SIGAC externe) → DossierConsultationDto (modèle interne)
 * avec résolution des noms via PersonnesService (opentopazservice).
 */
@ExtendWith(MockitoExtension.class)
class LoansApiDelegateImplTest {

    @Mock
    private ISigacLoansDao sigacLoansDao;

    @Mock
    private PersonnesService personnesService;

    @InjectMocks
    private LoansApiDelegateImpl delegate;

    // ── getLoan — cas nominal ───────────────────────────────────────

    @Test
    void getLoan_existant_retourneOk() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        ResponseEntity<CommonLoan> response = delegate.getLoan("PRT-2024-08-1547");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getLoan_inexistant_retourne404() throws DAOException {
        when(sigacLoansDao.getLoan("INEXISTANT")).thenReturn(Optional.empty());

        ResponseEntity<CommonLoan> response = delegate.getLoan("INEXISTANT");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getLoan_erreurDAO_retourne500() throws DAOException {
        when(sigacLoansDao.getLoan("PRT-2024-08-1547"))
                .thenThrow(new DAOException("Erreur connexion SIGAC"));

        ResponseEntity<CommonLoan> response = delegate.getLoan("PRT-2024-08-1547");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getLoan_appellePersonnesServicePourResoudreNoms() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        delegate.getLoan("PRT-2024-08-1547");

        verify(personnesService).resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C");
    }

    @Test
    void getLoan_erreurPersonnesService_retourneQuandMeme200() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur(anyString(), anyString()))
                .thenThrow(new RuntimeException("Topaze indisponible"));

        ResponseEntity<CommonLoan> response = delegate.getLoan("PRT-2024-08-1547");

        // L'erreur PersonnesService ne doit pas bloquer la réponse
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ── Mapping CommonLoan → DossierConsultationDto ─────────────────

    @Test
    void toDossierConsultationDto_mappeIdentifiantsContrat() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("PRT-2024-08-1547", dto.getNumeroContratSouscritPret());
        assertEquals("PRJ-2024-08-1547", dto.getNumeroContratSouscritProjet());
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
    void toDossierConsultationDto_mappeLoanState() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("40", dto.getCodeEtat());
        assertEquals("En gestion", dto.getLibelleEtat());
    }

    @Test
    void toDossierConsultationDto_mappeLoanType() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("PAP", dto.getCodeNature());
        assertEquals("Prêt à l'Accession à la Propriété", dto.getLibelleNature());
    }

    @Test
    void toDossierConsultationDto_mappeObjectCode() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("01", dto.getCodeObjet());
        assertEquals("Acquisition ancien", dto.getLibelleObjet());
    }

    @Test
    void toDossierConsultationDto_extraitIdentifiantsPersonnesSansNoms() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        // Identifiants extraits des Participant
        assertEquals("PP-001547-E", dto.getNoEmprunteur());
        assertEquals("PP-001547-C", dto.getNoCoEmprunteur());
        assertEquals("13807", dto.getEfs());

        // Noms NON remplis — seront résolus via PersonnesService
        assertNull(dto.getEmprunteur());
        assertNull(dto.getCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_sansCoEmprunteur_coEmprunteurNull() {
        CommonLoan loan = createCompleteLoan();
        Participant emp = loan.getParticipants().get(0);
        loan.setParticipants(List.of(emp));

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("PP-001547-E", dto.getNoEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_montantNull_retourneNull() {
        CommonLoan loan = createCompleteLoan();
        loan.setBorrowedAmount(null);
        loan.setAvailableAmount(null);
        loan.setRate(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getMontantPret());
        assertNull(dto.getMontantDisponible());
        assertNull(dto.getTauxRemboursement());
    }

    @Test
    void toDossierConsultationDto_sansLoanType_utiliseLabelEtTypeCode() {
        CommonLoan loan = createCompleteLoan();
        loan.setLoanType(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertEquals("PAP", dto.getCodeNature());
        assertEquals("Prêt à l'Accession à la Propriété", dto.getLibelleNature());
    }

    @Test
    void toDossierConsultationDto_sansParticipants_identifiantsNull() {
        CommonLoan loan = createCompleteLoan();
        loan.setParticipants(null);

        DossierConsultationDto dto = delegate.toDossierConsultationDto(loan);

        assertNull(dto.getNoEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
        assertNull(dto.getEfs());
    }

    // ── Helper ────────────────────────────────────────────────────

    private CommonLoan createCompleteLoan() {
        CommonLoan loan = new CommonLoan();
        loan.setId("PRT-2024-08-1547");
        loan.setMasterContractId("PRJ-2024-08-1547");
        loan.setDuration(240);
        loan.setBorrowedAmount(BigDecimal.valueOf(250000.0));
        loan.setRate(BigDecimal.valueOf(3.45));
        loan.setAvailableAmount(BigDecimal.valueOf(0.0));
        loan.setPeriodicity(CommonLoan.PeriodicityEnum.M);
        loan.setLabel("Prêt à l'Accession à la Propriété");
        loan.setTypeCode("PAP");

        LoanType loanType = new LoanType();
        loanType.setCode("PAP");
        loanType.setLabel("Prêt à l'Accession à la Propriété");
        loan.setLoanType(loanType);

        ObjectCode objectCode = new ObjectCode();
        objectCode.setCode("01");
        objectCode.setLabel("Acquisition ancien");
        loan.setObjectCode(objectCode);

        LoanState loanState = new LoanState();
        loanState.setCode("40");
        loanState.setLabel("En gestion");
        loan.setLoanState(loanState);

        Participant emp = new Participant();
        emp.setPersonNumber("PP-001547-E");
        emp.setPersonFederation("13807");
        emp.setRoleCode("EMP");

        Participant coe = new Participant();
        coe.setPersonNumber("PP-001547-C");
        coe.setPersonFederation("13807");
        coe.setRoleCode("COE");

        loan.setParticipants(List.of(emp, coe));

        return loan;
    }
}
