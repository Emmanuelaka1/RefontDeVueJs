package com.arkea.sgesapi.controller;

import com.arkea.sgesapi.dao.model.DossierConsultationDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.exception.DossierNotFoundException;
import com.arkea.sgesapi.exception.GlobalExceptionHandler;
import com.arkea.sgesapi.service.DossierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests MockMvc standalone — ConsultationController.
 */
@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    @Mock
    private DossierService dossierService;

    @InjectMocks
    private ConsultationController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void consulterDossier_retourneOk() throws Exception {
        DossierConsultationDto dossier = DossierConsultationDto.builder()
                .numeroPret("2024-PAP-001547")
                .emprunteur("MARTIN Jean-Pierre")
                .coEmprunteur("MARTIN Catherine")
                .noEmprunteur("PP-001-E")
                .noCoEmprunteur("PP-001-C")
                .numeroContratSouscritProjet("PRJ-2024")
                .numeroContratSouscritPret("PRT-2024")
                .efs("13807")
                .structure("CIF IDF")
                .codeEtat("40")
                .libelleEtat("En gestion")
                .codeObjet("01")
                .libelleObjet("Acquisition")
                .codeNature("PAP")
                .libelleNature("PAP")
                .montantPret(250000.0)
                .dureePret(240)
                .tauxRemboursement(3.45)
                .tauxFranchise(0.0)
                .tauxBonification(0.0)
                .anticipation(false)
                .typeAmortissement("Échéances constantes")
                .outilInstruction("GIPSI")
                .montantDebloque(250000.0)
                .montantDisponible(0.0)
                .montantRA(0.0)
                .encours(237845.12)
                .teg(3.72)
                .build();

        when(dossierService.consulterDossier("2024-PAP-001547")).thenReturn(dossier);

        mockMvc.perform(get("/api/v1/dossiers/2024-PAP-001547"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donneesGenerales.emprunteur").value("MARTIN Jean-Pierre"))
                .andExpect(jsonPath("$.donneesGenerales.numeroPret").value("2024-PAP-001547"))
                .andExpect(jsonPath("$.donneesPret.montantPret").value(250000.0));
    }

    @Test
    void consulterDossier_inexistant_retourne404() throws Exception {
        when(dossierService.consulterDossier("INEXISTANT"))
                .thenThrow(new DossierNotFoundException("INEXISTANT"));

        mockMvc.perform(get("/api/v1/dossiers/INEXISTANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consulterDossier_erreurDAO_retourne500() throws Exception {
        when(dossierService.consulterDossier("2024-PAP-001547"))
                .thenThrow(new DAOException("Erreur Topaze"));

        mockMvc.perform(get("/api/v1/dossiers/2024-PAP-001547"))
                .andExpect(status().isInternalServerError());
    }
}
