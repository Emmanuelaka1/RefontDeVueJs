package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.model.sigac.CommonLoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — SigacLoansRestDao.
 * <p>
 * Couvre :
 *  - Appel REST nominal → retourne CommonLoan
 *  - Réponse null → Optional.empty()
 *  - Erreur 404 → Optional.empty() (pas d'exception)
 *  - Erreur réseau → DAOException
 *  - Construction de l'URL avec base-url + /loans/{contratId}
 */
@ExtendWith(MockitoExtension.class)
class SigacLoansRestDaoTest {

    private static final String BASE_URL = "http://sigac-service:8080/api/v1";

    @Mock
    private RestTemplate restTemplate;

    private SigacLoansRestDao dao;

    @BeforeEach
    void setUp() {
        dao = new SigacLoansRestDao(restTemplate, BASE_URL);
    }

    @Test
    void getLoan_nominal_retourneCommonLoan() throws DAOException {
        CommonLoan expected = new CommonLoan();
        expected.setId("PRT-2024-08-1547");
        when(restTemplate.getForObject(BASE_URL + "/loans/PRT-2024-08-1547", CommonLoan.class))
                .thenReturn(expected);

        Optional<CommonLoan> result = dao.getLoan("PRT-2024-08-1547");

        assertTrue(result.isPresent());
        assertEquals("PRT-2024-08-1547", result.get().getId());
        verify(restTemplate).getForObject(BASE_URL + "/loans/PRT-2024-08-1547", CommonLoan.class);
    }

    @Test
    void getLoan_reponseNull_retourneEmpty() throws DAOException {
        when(restTemplate.getForObject(BASE_URL + "/loans/PRT-UNKNOWN", CommonLoan.class))
                .thenReturn(null);

        Optional<CommonLoan> result = dao.getLoan("PRT-UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void getLoan_erreur404_retourneEmpty() throws DAOException {
        when(restTemplate.getForObject(BASE_URL + "/loans/PRT-INEXISTANT", CommonLoan.class))
                .thenThrow(new RestClientException("404 Not Found"));

        Optional<CommonLoan> result = dao.getLoan("PRT-INEXISTANT");

        assertTrue(result.isEmpty());
    }

    @Test
    void getLoan_erreurReseau_lanceDAOException() {
        when(restTemplate.getForObject(BASE_URL + "/loans/PRT-2024-08-1547", CommonLoan.class))
                .thenThrow(new RestClientException("Connection refused"));

        DAOException ex = assertThrows(DAOException.class, () -> dao.getLoan("PRT-2024-08-1547"));

        assertTrue(ex.getMessage().contains("SIGAC Loans"));
        assertTrue(ex.getMessage().contains("Connection refused"));
    }

    @Test
    void getLoan_erreur500_lanceDAOException() {
        when(restTemplate.getForObject(BASE_URL + "/loans/PRT-2024-08-1547", CommonLoan.class))
                .thenThrow(new RestClientException("500 Internal Server Error"));

        DAOException ex = assertThrows(DAOException.class, () -> dao.getLoan("PRT-2024-08-1547"));

        assertTrue(ex.getMessage().contains("SIGAC Loans"));
    }

    @Test
    void getLoan_construitUrlCorrectement() throws DAOException {
        when(restTemplate.getForObject(anyString(), eq(CommonLoan.class))).thenReturn(new CommonLoan());

        dao.getLoan("PRT-2024-10-2891");

        verify(restTemplate).getForObject("http://sigac-service:8080/api/v1/loans/PRT-2024-10-2891", CommonLoan.class);
    }
}
