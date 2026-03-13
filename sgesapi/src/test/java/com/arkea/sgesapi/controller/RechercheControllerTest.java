package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.model.DossierResumeDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.GlobalExceptionHandler;
import com.arkea.sgesapi.service.DossierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests MockMvc standalone — RechercheController.
 */
@ExtendWith(MockitoExtension.class)
class RechercheControllerTest {

    @Mock
    private DossierService dossierService;

    @InjectMocks
    private RechercheController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void rechercherDossiers_retourneResultats() throws Exception {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-PAP-001547")
                .emprunteur("MARTIN Jean-Pierre")
                .efs("13807")
                .build();

        when(dossierService.rechercherDossiers(any())).thenReturn(List.of(resume));
        when(dossierService.compterDossiers(any())).thenReturn(1L);

        String body = objectMapper.writeValueAsString(Map.of(
                "nomEmprunteur", "MARTIN",
                "page", 0,
                "taille", 20
        ));

        mockMvc.perform(post("/api/v1/recherche/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.dossiers[0].numeroPret").value("2024-PAP-001547"));
    }

    @Test
    void rechercherDossiers_sansCritere_retourneTous() throws Exception {
        when(dossierService.rechercherDossiers(any())).thenReturn(List.of());
        when(dossierService.compterDossiers(any())).thenReturn(0L);

        String body = objectMapper.writeValueAsString(Map.of(
                "page", 0,
                "taille", 20
        ));

        mockMvc.perform(post("/api/v1/recherche/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void rechercherDossiers_erreurDAO_retourne500() throws Exception {
        when(dossierService.rechercherDossiers(any())).thenThrow(new DAOException("Erreur Topaze"));

        String body = objectMapper.writeValueAsString(Map.of(
                "page", 0,
                "taille", 20
        ));

        mockMvc.perform(post("/api/v1/recherche/dossiers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    // ── Recherche rapide ──────────────────────────────────────────

    @Test
    void rechercheRapide_retourneResultats() throws Exception {
        DossierResumeDto resume = DossierResumeDto.builder()
                .numeroPret("2024-PAP-001547")
                .emprunteur("MARTIN Jean-Pierre")
                .build();

        when(dossierService.rechercherDossiers(any())).thenReturn(List.of(resume));
        when(dossierService.compterDossiers(any())).thenReturn(1L);

        mockMvc.perform(get("/api/v1/recherche/dossiers")
                        .param("q", "MARTIN")
                        .param("page", "0")
                        .param("taille", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void rechercheRapide_sansParametres_retourneTous() throws Exception {
        when(dossierService.rechercherDossiers(any())).thenReturn(List.of());
        when(dossierService.compterDossiers(any())).thenReturn(0L);

        mockMvc.perform(get("/api/v1/recherche/dossiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
