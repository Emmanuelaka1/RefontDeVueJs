package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.api.LoansApiDelegate;
import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires — LoansController.
 * <p>
 * Le controller délègue à LoansApiDelegate.searchLoans().
 * On vérifie ici le routage HTTP et la bonne propagation des réponses.
 */
@ExtendWith(MockitoExtension.class)
class LoansControllerTest {

    @Mock
    private LoansApiDelegate loansApiDelegate;

    @InjectMocks
    private LoansController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── GET /api/v1/loans/{numeroPret} ────────────────────────────

    @Test
    void searchLoans_retourneOkAvecDossier() throws Exception {
        DossierConsultationDto dto = DossierConsultationDto.builder()
                .numeroContratSouscritPret("DD04063627")
                .dureePret(240)
                .emprunteur("MARTIN Jean-Pierre")
                .build();
        when(loansApiDelegate.searchLoans("DD04063627"))
                .thenReturn(ResponseEntity.ok(dto));

        mockMvc.perform(get("/api/v1/loans/DD04063627"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroContratSouscritPret").value("DD04063627"))
                .andExpect(jsonPath("$.dureePret").value(240))
                .andExpect(jsonPath("$.emprunteur").value("MARTIN Jean-Pierre"));

        verify(loansApiDelegate).searchLoans("DD04063627");
    }

    @Test
    void searchLoans_pretInexistant_retourne404() throws Exception {
        when(loansApiDelegate.searchLoans("INEXISTANT"))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/api/v1/loans/INEXISTANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchLoans_erreurInterne_retourne500() throws Exception {
        when(loansApiDelegate.searchLoans("DD04063627"))
                .thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get("/api/v1/loans/DD04063627"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchLoans_delegueAvecBonParametre() throws Exception {
        when(loansApiDelegate.searchLoans(anyString()))
                .thenReturn(ResponseEntity.ok(DossierConsultationDto.builder().build()));

        mockMvc.perform(get("/api/v1/loans/PRT-2024-08-1547"))
                .andExpect(status().isOk());

        verify(loansApiDelegate).searchLoans("PRT-2024-08-1547");
        verifyNoMoreInteractions(loansApiDelegate);
    }
}
