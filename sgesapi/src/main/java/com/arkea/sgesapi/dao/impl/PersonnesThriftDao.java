package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.thrift.AbstractThriftDAO;
import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation DAO Thrift Topaze pour les données personnes.
 * <p>
 * Suit le pattern Catalyst Arkea :
 * <pre>
 *   PersonnesThriftDao
 *     extends AbstractThriftDAO&lt;DonneesGeneriquesTopaze.Client&gt;
 *       extends AbstractCatalystThriftDAO&lt;DonneesGeneriquesTopaze.Client&gt;
 *     implements IPersonnesDao
 * </pre>
 * <p>
 * Equivalent de {@code PersonneServiceDefault} dans le code RPL existant.
 * <p>
 * Chaque méthode métier appelle {@code super.execute(client -> client.xxx(request))}
 * qui gère automatiquement le pool, le callback, et les erreurs.
 * <p>
 * Activé sur tous les profils sauf "dev" (val, rec, hml, prod).
 * En développement (profil "dev"), PersonnesMockDao prend le relais.
 *
 * @see com.arkea.sgesapi.thrift.AbstractThriftDAO
 * @see com.arkea.sgesapi.thrift.spring.AbstractCatalystThriftDAO
 */
@Repository
@Profile("!dev")
public class PersonnesThriftDao extends AbstractThriftDAO<TServiceClient> implements IPersonnesDao {

    private static final Logger LOG = LoggerFactory.getLogger(PersonnesThriftDao.class);

    public PersonnesThriftDao(TServiceClientPool<TServiceClient> pool) {
        super(pool);
    }

    /**
     * Identifiant fonctionnel pour les logs et messages d'erreur.
     * Correspond au nom du service Topaze appelé.
     */
    @Override
    protected String getFunctionnalContextId() {
        return "WsDonneesGeneriquesTopaze";
    }

    /**
     * Récupère les informations minimales de personnes via Topaze.
     * <p>
     * Pattern d'appel (à activer quand le code Thrift est généré) :
     * <pre>
     *   GetInformationsMinimalesPersonnesTopazeRequest thriftReq =
     *       new GetInformationsMinimalesPersonnesTopazeRequest(identifiantsPersonnes);
     *
     *   GetInformationsMinimalesPersonnesTopazeResponse thriftResp =
     *       (GetInformationsMinimalesPersonnesTopazeResponse)
     *           super.execute(client -> client.getInformationsMinimalesPersonnesTopaze(thriftReq));
     *
     *   return thriftResp.getPersonnes().stream()
     *       .collect(Collectors.toMap(
     *           PersonneMinimale::getIdentifiant,
     *           p -> new PersonneMinimaleDto(p.getIdentifiant(), p.getNom(),
     *                                        p.getPrenom(), p.getTypePersonne())
     *       ));
     * </pre>
     */
    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
            List<String> identifiantsPersonnes) throws DAOException {

        LOG.info("DAO Topaze — getInformationsMinimalesPersonnesTopaze nb={}",
                identifiantsPersonnes.size());

        // TODO: Activer quand le code Thrift généré est disponible
        // Décommenter le bloc ci-dessous et remplacer TServiceClient par
        // DonneesGeneriquesTopaze.Client dans la déclaration de classe
        //
        // try {
        //     GetInformationsMinimalesPersonnesTopazeRequest thriftReq =
        //         new GetInformationsMinimalesPersonnesTopazeRequest(identifiantsPersonnes);
        //
        //     GetInformationsMinimalesPersonnesTopazeResponse thriftResp =
        //         (GetInformationsMinimalesPersonnesTopazeResponse)
        //             super.execute(client ->
        //                 client.getInformationsMinimalesPersonnesTopaze(thriftReq));
        //
        //     Map<String, PersonneMinimaleDto> result = new HashMap<>();
        //     if (thriftResp.getPersonnes() != null) {
        //         for (var p : thriftResp.getPersonnes()) {
        //             result.put(p.getIdentifiant(),
        //                 new PersonneMinimaleDto(
        //                     p.getIdentifiant(),
        //                     p.getNom(),
        //                     p.getPrenom(),
        //                     p.getTypePersonne()));
        //         }
        //     }
        //     return result;
        //
        // } catch (DAOException e) {
        //     LOG.error("Erreur appel Topaze getInformationsMinimalesPersonnes", e);
        //     throw new RuntimeException("Erreur accès Topaze Personnes", e);
        // }

        throw new UnsupportedOperationException(
                "Client Thrift non disponible — générer le code avec 'gradle generateThrift' "
                + "puis remplacer TServiceClient par DonneesGeneriquesTopaze.Client");
    }
}
