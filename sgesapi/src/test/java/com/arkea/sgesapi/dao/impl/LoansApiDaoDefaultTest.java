package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.model.sigac.CommonLoan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Tests unitaires — LoansApiDaoDefault (RestClient, @Profile "!dev").
 * <p>
 * Utilise {@link MockRestServiceServer} pour simuler les réponses SIGAC.
 * <p>
 * Couvre :
 *  - Appel REST nominal → ResponseEntity OK avec CommonLoan
 *  - Erreur 404 → ResponseEntity NOT_FOUND
 *  - Erreur 500 → ResponseEntity INTERNAL_SERVER_ERROR
 *  - Construction de l'URI avec base-url + /loans/{contratId}
 */
class LoansApiDaoDefaultTest {

    private static final String BASE_URL = "http://sigac-service:8080/api/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MockRestServiceServer mockServer;
    private LoansApiDaoDefault dao;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        dao = new LoansApiDaoDefault(restClient);
    }

    @Test
    void getLoan_nominal_retourneOkAvecCommonLoan() throws Exception {
        CommonLoan expected = new CommonLoan();
        expected.setId("DD04063627");

        mockServer.expect(requestTo(BASE_URL + "/loans/DD04063627"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expected), MediaType.APPLICATION_JSON));

        ResponseEntity<CommonLoan> response = dao.getLoan("DD04063627");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DD04063627", response.getBody().getId());
        mockServer.verify();
    }

    @Test
    void getLoan_erreur404_retourneNotFound() throws Exception {
        mockServer.expect(requestTo(BASE_URL + "/loans/INEXISTANT"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        ResponseEntity<CommonLoan> response = dao.getLoan("INEXISTANT");

        // onStatus 404 → return sans exception → body null → notFound()
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        mockServer.verify();
    }

    @Test
    void getLoan_erreur500_retourneInternalServerError() throws Exception {
        mockServer.expect(requestTo(BASE_URL + "/loans/DD04063627"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<CommonLoan> response = dao.getLoan("DD04063627");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void getLoan_construitUrlCorrectement() throws Exception {
        CommonLoan loan = new CommonLoan();
        loan.setId("PRT-2024-10-2891");

        mockServer.expect(requestTo(BASE_URL + "/loans/PRT-2024-10-2891"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(loan), MediaType.APPLICATION_JSON));

        dao.getLoan("PRT-2024-10-2891");

        mockServer.verify();
    }

    @Test
    void getLoan_erreur400_retourneInternalServerError() throws Exception {
        mockServer.expect(requestTo(BASE_URL + "/loans/INVALID"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        ResponseEntity<CommonLoan> response = dao.getLoan("INVALID");

        // 400 non-404 → traité comme erreur → INTERNAL_SERVER_ERROR
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        mockServer.verify();
    }
}
