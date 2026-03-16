package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.model.sigac.CommonLoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — LoansApiDaoMock.
 * <p>
 * Vérifie le bouchon du client SIGAC Loans (profil dev).
 * LoansApiDaoMock implémente LoansApi et retourne des ResponseEntity&lt;CommonLoan&gt;.
 */
class LoansApiDaoMockTest {

    private LoansApiDaoMock dao;

    @BeforeEach
    void setUp() {
        dao = new LoansApiDaoMock();
        dao.initMockData();
    }

    @Test
    void getLoan_existant_retourneOkAvecCommonLoan() {
        ResponseEntity<CommonLoan> response = dao.getLoan("PRT-2024-08-1547");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PRT-2024-08-1547", response.getBody().getId());
        assertEquals("PRJ-2024-08-1547", response.getBody().getMasterContractId());
    }

    @Test
    void getLoan_inexistant_retourneNotFound() {
        ResponseEntity<CommonLoan> response = dao.getLoan("INEXISTANT");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLoan_verifieParticipants() {
        CommonLoan loan = dao.getLoan("PRT-2024-08-1547").getBody();

        assertNotNull(loan);
        assertNotNull(loan.getParticipants());
        assertEquals(2, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
        assertEquals("COE", loan.getParticipants().get(1).getRoleCode());
    }

    @Test
    void getLoan_sansCoEmprunteur_unSeulParticipant() {
        CommonLoan loan = dao.getLoan("PRT-2023-03-0412").getBody();

        assertNotNull(loan);
        assertEquals(1, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
    }

    @Test
    void getLoan_verifieChampsMontant() {
        CommonLoan loan = dao.getLoan("PRT-2024-08-1547").getBody();

        assertNotNull(loan);
        assertEquals(240, loan.getDuration());
        assertNotNull(loan.getBorrowedAmount());
        assertNotNull(loan.getRate());
        assertNotNull(loan.getLoanType());
        assertEquals("PAP", loan.getLoanType().getCode());
    }

    @Test
    void getLoan_deuxiemePret_verifieChamps() {
        CommonLoan loan = dao.getLoan("PRT-2024-10-2891").getBody();

        assertNotNull(loan);
        assertEquals("PRT-2024-10-2891", loan.getId());
        assertEquals(300, loan.getDuration());
        assertEquals("PAS", loan.getLoanType().getCode());
        assertEquals("30", loan.getLoanState().getCode());
    }
}
