package com.arkea.sgesapi.thrift;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.thrift.callback.ThriftDaoCallbackIface;
import com.arkea.sgesapi.thrift.data.ResponseContext;
import com.arkea.sgesapi.thrift.data.ResponseType;
import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import com.arkea.sgesapi.thrift.spring.AbstractCatalystThriftDAO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe abstraite DAO Thrift avec le pattern execute/callback.
 * <p>
 * Equivalent interne de {@code com.arkea.rpl.dao.thrift.AbstractThriftDAO}
 * utilisé dans l'écosystème RPL Arkea.
 * <p>
 * Cette classe implémente le pattern template :
 * <ol>
 *   <li>Emprunte un client Thrift du pool ({@code getClient()})</li>
 *   <li>Exécute le callback métier ({@code clientCallback.doInConnection(client)})</li>
 *   <li>Analyse le {@link ResponseContext} de la réponse pour détecter les erreurs métier</li>
 *   <li>Gère les exceptions (transport, protocole, métier)</li>
 *   <li>Retourne ou invalide le client dans le pool ({@code finalizeClient()})</li>
 * </ol>
 * <p>
 * Chaque DAO concret doit :
 * <ul>
 *   <li>Étendre cette classe avec le bon type de client Thrift</li>
 *   <li>Implémenter {@link #getFunctionnalContextId()} pour le contexte de logs</li>
 *   <li>Appeler {@code super.execute(client -> client.xxx(request))} pour chaque opération</li>
 * </ul>
 *
 * @param <T> type du client Thrift (ex: DonneesGeneriquesTopaze.Client)
 * @author Adapté du pattern RPL Arkea
 */
public abstract class AbstractThriftDAO<T extends TServiceClient> extends AbstractCatalystThriftDAO<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractThriftDAO.class);

    protected AbstractThriftDAO(final TServiceClientPool<T> pool) {
        super(pool);
    }

    /**
     * Identifiant du contexte fonctionnel pour les logs et les messages d'erreur.
     * <p>
     * Exemple : {@code "WsAnnuairePersonneService"}, {@code "WsDonneesGeneriquesTopaze"}
     *
     * @return identifiant du service fonctionnel
     */
    protected abstract String getFunctionnalContextId();

    /**
     * Exécute un appel Thrift via le pattern callback.
     * <p>
     * Gestion complète du cycle de vie :
     * <pre>
     *   1. Emprunter un client du pool
     *   2. Exécuter le callback (appel Thrift)
     *   3. Analyser le ResponseContext pour les erreurs métier
     *   4. Gérer les exceptions
     *   5. Retourner/invalider le client dans le pool
     * </pre>
     *
     * @param clientCallback le callback contenant l'appel Thrift à exécuter
     * @return le résultat de l'appel Thrift
     * @throws DAOException en cas d'erreur (transport, métier, technique)
     */
    protected Object execute(ThriftDaoCallbackIface<T> clientCallback) throws DAOException {

        boolean invalidateClient = false;
        Object resp = null;

        // Permet de contextualiser les exceptions (et donc les logs) à venir pour cet appel
        String functionnalContextId = getFunctionnalContextId();

        T client = null;

        try {
            // Récupération d'un objet du Pool
            client = getClient();

            // Appel Service Thrift
            resp = clientCallback.doInConnection(client);

            // Analyse du ResponseContext dans la réponse Thrift
            Object responseContextObj = null;
            try {
                responseContextObj = PropertyUtils.getProperty(resp, "responseContext");
            } catch (Exception e) {
                LOG.debug(String.format(
                        "Pas de responseContext pour le context fonctionnel '%s'",
                        functionnalContextId));
            }

            if (responseContextObj instanceof ResponseContext) {
                ResponseContext responseContext = (ResponseContext) responseContextObj;
                if (responseContext.containsKey(ResponseType.ERROR)
                        && !responseContext.getMessagesByType(ResponseType.ERROR).isEmpty()) {
                    String message = responseContext.getMessagesByType(ResponseType.ERROR).get(0);
                    throw new DAOException(String.join(" ", message), null);
                }
            }

        } catch (TTransportException e) {
            LOG.error("appel DAO {}", functionnalContextId, e);
            invalidateClient = true;
            throw new DAOException(
                    String.join(" ", "Erreur pour", functionnalContextId), e);

        } catch (DAOException e) {
            LOG.error("appel DAO {}", functionnalContextId, e);
            // Exception générée suite à la réponse du service (ResponseContext)
            throw e;

        } catch (TException e) {
            LOG.error("appel DAO {}", functionnalContextId, e);
            // On n'invalide pas le client ici (pour traiter le cas des Exception fonctionnelles)
            throw new DAOException(
                    String.join(" ", "Erreur pour", functionnalContextId), e);

        } catch (Exception e) {
            LOG.error("appel DAO {}", functionnalContextId, e);
            // Exception générée par le borrowObject
            invalidateClient = true;
            throw new DAOException(
                    String.join(" ", "Erreur pour", functionnalContextId), e);

        } finally {
            // Retour ou invalidation du client dans le pool
            try {
                finalizeClient(client, invalidateClient);
            } catch (Exception e) {
                throw new DAOException(
                        String.join(" ", "Erreur au retour to pool pour", functionnalContextId), e);
            }
        }

        return resp;
    }
}
