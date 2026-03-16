package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.api.ISigacLoansDao;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.GlobalExceptionHandler;
import com.arkea.sgesapi.model.sigac.*;
import com.arkea.sgesapi.service.PersonnesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires — LoansController (implémente LoansApi directement).
 * <p>
 * Couvre :
 *  - GET /loans/{id} via MockMvc
 *  - Mapping CommonLoan → DossierConsultationDto
 *  - Résolution des noms via PersonnesService (opentopazservice)
 */
@ExtendWith(MockitoExtension.class)
class LoansControllerTest {

    @Mock
    private ISigacLoansDao sigacLoansDao;

    @Mock
    private PersonnesService personnesService;

    @InjectMocks
    private LoansController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── GET /loans/{id} — MockMvc ─────────────────────────────────

    @Test
    void getLoan_retourneOkAvecCommonLoan() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        mockMvc.perform(get("/loans/PRT-2024-08-1547"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PRT-2024-08-1547"))
                .andExpect(jsonPath("$.masterContractId").value("PRJ-2024-08-1547"))
                .andExpect(jsonPath("$.duration").value(240))
                .andExpect(jsonPath("$.borrowedAmount").value(250000.0))
                .andExpect(jsonPath("$.loanType.code").value("PAP"))
                .andExpect(jsonPath("$.loanState.label").value("En gestion"));
    }

    @Test
    void getLoan_inexistant_retourne404() throws Exception {
        when(sigacLoansDao.getLoan("INEXISTANT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/loans/INEXISTANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLoan_erreurDAO_retourne500() throws Exception {
        when(sigacLoansDao.getLoan("PRT-2024-08-1547"))
                .thenThrow(new DAOException("Erreur connexion SIGAC"));

        mockMvc.perform(get("/loans/PRT-2024-08-1547"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getLoan_appellePersonnesServicePourResoudreNoms() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C"))
                .thenReturn(new String[]{"MARTIN Jean-Pierre", "MARTIN Catherine"});

        mockMvc.perform(get("/loans/PRT-2024-08-1547"))
                .andExpect(status().isOk());

        verify(personnesService).resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C");
    }

    @Test
    void getLoan_erreurPersonnesService_retourneQuandMeme200() throws Exception {
        CommonLoan loan = createCompleteLoan();
        when(sigacLoansDao.getLoan("PRT-2024-08-1547")).thenReturn(Optional.of(loan));
        when(personnesService.resoudreEmprunteurCoEmprunteur(anyString(), anyString()))
                .thenThrow(new RuntimeException("Topaze indisponible"));

        mockMvc.perform(get("/loans/PRT-2024-08-1547"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PRT-2024-08-1547"));
    }

    // ── Mapping CommonLoan → DossierConsultationDto ─────────────────

    @Test
    void toDossierConsultationDto_mappeIdentifiantsContrat() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertEquals("PRT-2024-08-1547", dto.getNumeroContratSouscritPret());
        assertEquals("PRJ-2024-08-1547", dto.getNumeroContratSouscritProjet());
    }

    @Test
    void toDossierConsultationDto_mappeDureeEtMontant() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertEquals(240, dto.getDureePret());
        assertEquals(250000.0, dto.getMontantPret());
        assertEquals(3.45, dto.getTauxRemboursement());
        assertEquals(0.0, dto.getMontantDisponible());
    }

    @Test
    void toDossierConsultationDto_mappeLoanStateObjectCodeLoanType() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertEquals("40", dto.getCodeEtat());
        assertEquals("En gestion", dto.getLibelleEtat());
        assertEquals("PAP", dto.getCodeNature());
        assertEquals("Prêt à l'Accession à la Propriété", dto.getLibelleNature());
        assertEquals("01", dto.getCodeObjet());
        assertEquals("Acquisition ancien", dto.getLibelleObjet());
    }

    @Test
    void toDossierConsultationDto_extraitIdentifiantsPersonnesSansNoms() {
        CommonLoan loan = createCompleteLoan();

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertEquals("PP-001547-E", dto.getNoEmprunteur());
        assertEquals("PP-001547-C", dto.getNoCoEmprunteur());
        assertEquals("13807", dto.getEfs());
        // Noms NON remplis — résolus ensuite via opentopazservice
        assertNull(dto.getEmprunteur());
        assertNull(dto.getCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_sansCoEmprunteur() {
        CommonLoan loan = createCompleteLoan();
        loan.setParticipants(List.of(loan.getParticipants().get(0)));

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertEquals("PP-001547-E", dto.getNoEmprunteur());
        assertNull(dto.getNoCoEmprunteur());
    }

    @Test
    void toDossierConsultationDto_montantNull() {
        CommonLoan loan = createCompleteLoan();
        loan.setBorrowedAmount(null);
        loan.setRate(null);
        loan.setAvailableAmount(null);

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

        assertNull(dto.getMontantPret());
        assertNull(dto.getTauxRemboursement());
        assertNull(dto.getMontantDisponible());
    }

    @Test
    void toDossierConsultationDto_sansParticipants() {
        CommonLoan loan = createCompleteLoan();
        loan.setParticipants(null);

        DossierConsultationDto dto = controller.toDossierConsultationDto(loan);

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
