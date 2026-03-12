package com.arkea.sgesapi.thrift.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pool de clients Thrift basé sur Apache Commons Pool2.
 * <p>
 * Equivalent interne de {@code com.arkea.commons.thrift.pool.TServiceClientPool}
 * utilisé dans l'écosystème Catalyst Arkea.
 * <p>
 * Gère un pool d'objets {@code TServiceClient} réutilisables, évitant
 * d'ouvrir/fermer une connexion TCP à chaque appel Thrift.
 *
 * @param <T> type du client Thrift (ex: DonneesGeneriquesTopaze.Client)
 */
public class TServiceClientPool<T extends TServiceClient> {

    private static final Logger LOG = LoggerFactory.getLogger(TServiceClientPool.class);

    private final GenericObjectPool<T> internalPool;

    /**
     * Crée un pool avec la factory et la configuration données.
     *
     * @param factory factory pour créer/détruire les clients Thrift
     * @param config  configuration du pool (taille max, idle, etc.)
     */
    public TServiceClientPool(ThriftClientFactory<T> factory, GenericObjectPoolConfig<T> config) {
        this.internalPool = new GenericObjectPool<>(factory, config);
        LOG.info("Pool Thrift initialisé — maxTotal={}, maxIdle={}, minIdle={}",
                config.getMaxTotal(), config.getMaxIdle(), config.getMinIdle());
    }

    /**
     * Crée un pool avec la factory et une configuration par défaut.
     */
    public TServiceClientPool(ThriftClientFactory<T> factory) {
        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(8);
        config.setMaxIdle(4);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        this.internalPool = new GenericObjectPool<>(factory, config);
        LOG.info("Pool Thrift initialisé avec config par défaut — maxTotal=8");
    }

    /**
     * Emprunte un client du pool.
     * Correspond à {@code pool.borrowObject()} dans le code Catalyst.
     *
     * @return un client Thrift prêt à l'emploi
     * @throws Exception si le pool ne peut pas fournir de client
     */
    public T borrowObject() throws Exception {
        T client = internalPool.borrowObject();
        LOG.debug("Client emprunté du pool — actifs={}, idle={}",
                internalPool.getNumActive(), internalPool.getNumIdle());
        return client;
    }

    /**
     * Retourne un client valide au pool pour réutilisation.
     * Correspond à {@code pool.returnObject(client)} dans le code Catalyst.
     *
     * @param client le client à retourner
     */
    public void returnObject(T client) {
        if (client != null) {
            internalPool.returnObject(client);
            LOG.debug("Client retourné au pool — actifs={}, idle={}",
                    internalPool.getNumActive(), internalPool.getNumIdle());
        }
    }

    /**
     * Invalide un client défectueux (connexion perdue, erreur transport).
     * Le pool le détruira et créera un nouveau client au besoin.
     * Correspond à {@code pool.invalidateObject(client)} dans le code Catalyst.
     *
     * @param client le client à invalider
     */
    public void invalidateObject(T client) {
        if (client != null) {
            try {
                internalPool.invalidateObject(client);
                LOG.debug("Client invalidé dans le pool — actifs={}, idle={}",
                        internalPool.getNumActive(), internalPool.getNumIdle());
            } catch (Exception e) {
                LOG.warn("Erreur lors de l'invalidation du client dans le pool", e);
            }
        }
    }

    /**
     * Ferme le pool et libère toutes les ressources.
     */
    public void close() {
        internalPool.close();
        LOG.info("Pool Thrift fermé");
    }

    /**
     * Retourne le nombre de clients actifs (empruntés).
     */
    public int getNumActive() {
        return internalPool.getNumActive();
    }

    /**
     * Retourne le nombre de clients inactifs dans le pool.
     */
    public int getNumIdle() {
        return internalPool.getNumIdle();
    }
}
