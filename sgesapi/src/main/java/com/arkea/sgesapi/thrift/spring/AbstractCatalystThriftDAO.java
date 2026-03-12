package com.arkea.sgesapi.thrift.spring;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import org.apache.thrift.TServiceClient;

/**
 * Classe abstraite de base pour les DAO Thrift — gestion du pool de clients.
 * <p>
 * Equivalent interne de {@code com.arkea.catalyst.dao.thrift.spring.AbstractCatalystThriftDAO}
 * utilisé dans l'écosystème Catalyst Arkea.
 * <p>
 * Responsabilités :
 * <ul>
 *   <li>Détenir la référence au {@link TServiceClientPool}</li>
 *   <li>Fournir {@link #getClient()} pour emprunter un client du pool</li>
 *   <li>Fournir {@link #finalizeClient(TServiceClient, boolean)} pour retourner/invalider</li>
 * </ul>
 * <p>
 * La sous-classe {@code AbstractThriftDAO} ajoute la méthode {@code execute()} avec
 * le pattern callback et la gestion complète des erreurs.
 *
 * @param <T> type du client Thrift (ex: DonneesGeneriquesTopaze.Client)
 * @author Adapté du pattern Catalyst Arkea (Yves Dubromelle, 14/06/18)
 */
public abstract class AbstractCatalystThriftDAO<T extends TServiceClient> {

    private final TServiceClientPool<T> pool;

    protected AbstractCatalystThriftDAO(final TServiceClientPool<T> pool) {
        this.pool = pool;
    }

    /**
     * Emprunte un client Thrift du pool.
     * <p>
     * Le client retourné est connecté et prêt à être utilisé.
     * Il DOIT être retourné via {@link #finalizeClient(TServiceClient, boolean)}
     * dans un bloc {@code finally}.
     *
     * @return un client Thrift emprunté du pool
     * @throws DAOException si le pool ne peut pas fournir de client
     */
    protected T getClient() throws DAOException {
        final T client;
        try {
            client = pool.borrowObject();
        } catch (Exception e) {
            throw new DAOException("Erreur lors de la récupération du client dans le pool", e);
        }
        return client;
    }

    /**
     * Finalise un client Thrift — le retourne au pool ou l'invalide.
     * <p>
     * Doit TOUJOURS être appelé dans un bloc {@code finally} après
     * l'utilisation du client emprunté via {@link #getClient()}.
     *
     * @param client           le client à finaliser
     * @param invalidateClient {@code true} pour invalider (erreur transport),
     *                         {@code false} pour retourner au pool (cas normal)
     * @throws DAOException si une erreur survient lors du retour au pool
     */
    protected void finalizeClient(final T client, final boolean invalidateClient) throws DAOException {
        try {
            if (invalidateClient) {
                pool.invalidateObject(client);
            } else {
                pool.returnObject(client);
            }
        } catch (Exception e) {
            throw new DAOException("Erreur lors du retour du client dans le pool", e);
        }
    }

    /**
     * Retourne le pool de clients pour les sous-classes qui en auraient besoin.
     */
    protected TServiceClientPool<T> getPool() {
        return pool;
    }
}
