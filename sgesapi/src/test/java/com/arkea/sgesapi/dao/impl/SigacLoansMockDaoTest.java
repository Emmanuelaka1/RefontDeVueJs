package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.model.sigac.CommonLoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — SigacLoansMockDao.
 * Vérifie le bouchon du client SIGAC Loans.
 */
class SigacLoansMockDaoTest {

    private SigacLoansMockDao dao;

    @BeforeEach
    void setUp() {
        dao = new SigacLoansMockDao();
        dao.initMockData();
    }

    @Test
    void getLoan_existant_retournePresent() throws DAOException {
        Optional<CommonLoan> result = dao.getLoan("PRT-2024-08-1547");

        assertTrue(result.isPresent());
        assertEquals("PRT-2024-08-1547", result.get().getId());
        assertEquals("PRJ-2024-08-1547", result.get().getMasterContractId());
    }

    @Test
    void getLoan_inexistant_retourneVide() throws DAOException {
        Optional<CommonLoan> result = dao.getLoan("INEXISTANT");

        assertTrue(result.isEmpty());
    }

    @Test
    void getLoan_verifieParticipants() throws DAOException {
        CommonLoan loan = dao.getLoan("PRT-2024-08-1547").orElseThrow();

        assertNotNull(loan.getParticipants());
        assertEquals(2, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
        assertEquals("COE", loan.getParticipants().get(1).getRoleCode());
    }

    @Test
    void getLoan_sansCoEmprunteur_unSeulParticipant() throws DAOException {
        CommonLoan loan = dao.getLoan("PRT-2023-03-0412").orElseThrow();

        assertEquals(1, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
    }

    @Test
    void getLoan_verifieChampsMontant() throws DAOException {
        CommonLoan loan = dao.getLoan("PRT-2024-08-1547").orElseThrow();

        assertEquals(240, loan.getDuration());
        assertNotNull(loan.getBorrowedAmount());
        assertNotNull(loan.getRate());
        assertNotNull(loan.getLoanType());
        assertEquals("PAP", loan.getLoanType().getCode());
    }
}
