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
 * <p>
 * Les données mock utilisent les formats réels SIGAC :
 *  - id : 10 caractères alphanumériques (ex: DD04063627)
 *  - typeCode : 5 chiffres (ex: 10117)
 *  - personNumber : numérique (ex: 14336390)
 *  - personFederation : code EFS court (ex: 01)
 *  - label : null (le vrai SIGAC retourne null, le libellé vient de loanType.label)
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
        ResponseEntity<CommonLoan> response = dao.getLoan("DD04063627");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DD04063627", response.getBody().getId());
        assertEquals("DD04063627", response.getBody().getMasterContractId());
    }

    @Test
    void getLoan_inexistant_retourneNotFound() {
        ResponseEntity<CommonLoan> response = dao.getLoan("INEXISTANT");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLoan_verifieParticipants() {
        CommonLoan loan = dao.getLoan("DD04063627").getBody();

        assertNotNull(loan);
        assertNotNull(loan.getParticipants());
        assertEquals(2, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
        assertEquals("COE", loan.getParticipants().get(1).getRoleCode());
        // Formats réels SIGAC : personNumber numérique, personFederation code EFS court
        assertEquals("14336390", loan.getParticipants().get(0).getPersonNumber());
        assertEquals("01", loan.getParticipants().get(0).getPersonFederation());
    }

    @Test
    void getLoan_sansCoEmprunteur_unSeulParticipant() {
        CommonLoan loan = dao.getLoan("BZ98765432").getBody();

        assertNotNull(loan);
        assertEquals(1, loan.getParticipants().size());
        assertEquals("EMP", loan.getParticipants().get(0).getRoleCode());
    }

    @Test
    void getLoan_verifieChampsMontant() {
        CommonLoan loan = dao.getLoan("DD04063627").getBody();

        assertNotNull(loan);
        assertEquals(240, loan.getDuration());
        assertNotNull(loan.getBorrowedAmount());
        assertNotNull(loan.getRate());
        assertNotNull(loan.getLoanType());
        // Format réel : loanType.code = 6 chiffres, typeCode = 5 chiffres
        assertEquals("110309", loan.getLoanType().getCode());
        assertEquals("10117", loan.getTypeCode());
    }

    @Test
    void getLoan_deuxiemePret_verifieChamps() {
        CommonLoan loan = dao.getLoan("AX12457845").getBody();

        assertNotNull(loan);
        assertEquals("AX12457845", loan.getId());
        assertEquals(300, loan.getDuration());
        assertEquals("200100", loan.getLoanType().getCode());
        assertEquals("DB", loan.getLoanState().getCode());
    }

    @Test
    void getLoan_labelEstNull_commeVraiSigac() {
        CommonLoan loan = dao.getLoan("DD04063627").getBody();

        assertNotNull(loan);
        // Dans le vrai SIGAC, label est null — le libellé vient de loanType.label
        assertNull(loan.getLabel());
        assertNotNull(loan.getLoanType().getLabel());
        assertEquals("ALTIMMO FIXE", loan.getLoanType().getLabel());
    }

    @Test
    void getLoan_verifieObjectCodeFormatReel() {
        CommonLoan loan = dao.getLoan("DD04063627").getBody();

        assertNotNull(loan);
        assertNotNull(loan.getObjectCode());
        assertEquals("AA", loan.getObjectCode().getCode());
        assertEquals("ACQUISITION ANCIEN", loan.getObjectCode().getLabel());
    }

    @Test
    void getLoan_verifieLoanStateFormatReel() {
        CommonLoan loan = dao.getLoan("DD04063627").getBody();

        assertNotNull(loan);
        assertNotNull(loan.getLoanState());
        assertEquals("AA", loan.getLoanState().getCode());
        assertEquals("EN COURS NORMALE", loan.getLoanState().getLabel());
    }
}
