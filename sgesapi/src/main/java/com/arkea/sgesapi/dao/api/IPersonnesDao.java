package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;

import java.util.List;
import java.util.Map;

/**
 * Interface DAO pour l'accès aux données personnes via Topaze.
 * <p>
 * Le Service métier ne connaît que cette interface, jamais l'implémentation
 * Thrift Topaze sous-jacente.
 */
public interface IPersonnesDao {

    /**
     * Récupère les informations minimales de personnes (PP ou PM) à partir
     * de leurs identifiants Topaze.
     * <p>
     * Correspond à l'appel Thrift {@code getInformationsMinimalesPersonnesTopaze}.
     *
     * @param identifiantsPersonnes liste des identifiants Topaze (noEmprunteur, noCoEmprunteur, etc.)
     * @return Map identifiant → PersonneMinimaleDto (les identifiants inconnus sont absents de la map)
     */
    Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(List<String> identifiantsPersonnes);
}
