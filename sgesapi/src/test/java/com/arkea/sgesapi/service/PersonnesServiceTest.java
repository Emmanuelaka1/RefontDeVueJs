package com.arkea.sgesapi.service;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import com.arkea.sgesapi.exception.DAOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — PersonnesService.
 */
@ExtendWith(MockitoExtension.class)
class PersonnesServiceTest {

    @Mock
    private IPersonnesDao personnesDao;

    @InjectMocks
    private PersonnesService personnesService;

    // ── getInformationsMinimalesPersonnes ──────────────────────────

    @Test
    void getInformationsMinimalesPersonnes_retourneMap() throws DAOException {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PP-001", "MARTIN", "Jean", "PP");
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("PP-001")))
                .thenReturn(Map.of("PP-001", dto));

        Map<String, PersonneMinimaleDto> result =
                personnesService.getInformationsMinimalesPersonnes(List.of("PP-001"));

        assertEquals(1, result.size());
        assertEquals("MARTIN", result.get("PP-001").getNom());
        verify(personnesDao).getInformationsMinimalesPersonnes(List.of("PP-001"));
    }

    @Test
    void getInformationsMinimalesPersonnes_propageDAOException() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(anyList()))
                .thenThrow(new DAOException("Erreur Topaze"));

        assertThrows(DAOException.class, () ->
                personnesService.getInformationsMinimalesPersonnes(List.of("PP-001")));
    }

    @Test
    void getInformationsMinimalesPersonnes_listeVide_retourneMapVide() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());

        Map<String, PersonneMinimaleDto> result =
                personnesService.getInformationsMinimalesPersonnes(Collections.emptyList());

        assertTrue(result.isEmpty());
    }

    // ── resoudreLibellePersonne ────────────────────────────────────

    @Test
    void resoudreLibellePersonne_identifiantValide_retourneLibelle() throws DAOException {
        PersonneMinimaleDto dto = new PersonneMinimaleDto("PP-001", "MARTIN", "Jean", "PP");
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("PP-001")))
                .thenReturn(Map.of("PP-001", dto));

        String result = personnesService.resoudreLibellePersonne("PP-001");

        assertEquals("MARTIN Jean", result);
    }

    @Test
    void resoudreLibellePersonne_identifiantNull_retourneNull() {
        String result = personnesService.resoudreLibellePersonne(null);

        assertNull(result);
        verifyNoInteractions(personnesDao);
    }

    @Test
    void resoudreLibellePersonne_identifiantVide_retourneNull() {
        String result = personnesService.resoudreLibellePersonne("");

        assertNull(result);
        verifyNoInteractions(personnesDao);
    }

    @Test
    void resoudreLibellePersonne_identifiantBlanc_retourneNull() {
        String result = personnesService.resoudreLibellePersonne("   ");

        assertNull(result);
        verifyNoInteractions(personnesDao);
    }

    @Test
    void resoudreLibellePersonne_identifiantInconnu_retourneNull() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("INCONNU")))
                .thenReturn(Collections.emptyMap());

        String result = personnesService.resoudreLibellePersonne("INCONNU");

        assertNull(result);
    }

    @Test
    void resoudreLibellePersonne_erreurDAO_retourneNull() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(anyList()))
                .thenThrow(new DAOException("Erreur connexion"));

        String result = personnesService.resoudreLibellePersonne("PP-001");

        assertNull(result);
    }

    // ── resoudreEmprunteurCoEmprunteur ─────────────────────────────

    @Test
    void resoudreEmprunteurCoEmprunteur_deuxIdentifiants_resoutLesDeux() throws DAOException {
        PersonneMinimaleDto emp = new PersonneMinimaleDto("PP-001-E", "MARTIN", "Jean", "PP");
        PersonneMinimaleDto coEmp = new PersonneMinimaleDto("PP-001-C", "MARTIN", "Catherine", "PP");
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("PP-001-E", "PP-001-C")))
                .thenReturn(Map.of("PP-001-E", emp, "PP-001-C", coEmp));

        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("PP-001-E", "PP-001-C");

        assertEquals("MARTIN Jean", result[0]);
        assertEquals("MARTIN Catherine", result[1]);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_emprunteurSeul_coEmprunteurNull() throws DAOException {
        PersonneMinimaleDto emp = new PersonneMinimaleDto("PP-001-E", "LECLERC", "Sophie", "PP");
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("PP-001-E")))
                .thenReturn(Map.of("PP-001-E", emp));

        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("PP-001-E", null);

        assertEquals("LECLERC Sophie", result[0]);
        assertNull(result[1]);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_deuxNull_retourneTableauNull() {
        String[] result = personnesService.resoudreEmprunteurCoEmprunteur(null, null);

        assertNull(result[0]);
        assertNull(result[1]);
        verifyNoInteractions(personnesDao);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_deuxBlancs_retourneTableauNull() {
        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("  ", "  ");

        assertNull(result[0]);
        assertNull(result[1]);
        verifyNoInteractions(personnesDao);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_erreurDAO_retourneTableauNull() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(anyList()))
                .thenThrow(new DAOException("Erreur réseau"));

        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("PP-001-E", "PP-001-C");

        assertNull(result[0]);
        assertNull(result[1]);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_emprunteurInconnu_retourneNullPourEmprunteur() throws DAOException {
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("INCONNU")))
                .thenReturn(Collections.emptyMap());

        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("INCONNU", null);

        assertNull(result[0]);
        assertNull(result[1]);
    }

    @Test
    void resoudreEmprunteurCoEmprunteur_coEmprunteurBlanc_appelAvecEmprunteurSeul() throws DAOException {
        PersonneMinimaleDto emp = new PersonneMinimaleDto("PP-001-E", "DUPONT", "Marie", "PP");
        when(personnesDao.getInformationsMinimalesPersonnes(List.of("PP-001-E")))
                .thenReturn(Map.of("PP-001-E", emp));

        String[] result = personnesService.resoudreEmprunteurCoEmprunteur("PP-001-E", "   ");

        assertEquals("DUPONT Marie", result[0]);
        assertNull(result[1]);
    }
}
