package com.arkea.sgesapi.dao.api;

import com.arkea.sgesapi.exception.ThriftDaoException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Pool de connexions Thrift vers le système Topaze.
 * <p>
 * Chaque appel ouvre une connexion TCP, exécute l'opération Thrift,
 * puis ferme proprement la connexion (stateless, thread-safe).
 */
@Component
public class ThriftClientPool {

    private static final Logger log = LoggerFactory.getLogger(ThriftClientPool.class);

    @Value("${thrift.client.topaze.host}")
    private String host;

    @Value("${thrift.client.topaze.port}")
    private int port;

    @Value("${thrift.client.topaze.timeout:5000}")
    private int timeout;

    /**
     * Exécute une opération Thrift via un client DonneesGeneriquesTopaze.
     *
     * @param operation l'opération à exécuter sur le client Thrift
     * @param <T>       le type de retour
     * @return le résultat de l'opération
     * @throws ThriftDaoException en cas d'erreur de communication
     */
    public <T> T execute(ThriftOperation<T> operation) {
        TTransport transport = null;
        try {
            transport = new TSocket(host, port, timeout);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            // Le client Thrift sera généré depuis topaze-personnes.thrift
            // DonneesGeneriquesTopaze.Client client = new DonneesGeneriquesTopaze.Client(protocol);
            // return operation.execute(client);

            // TODO: Activer quand le code Thrift est généré
            throw new UnsupportedOperationException(
                    "Client Thrift non disponible — générer le code avec 'gradle generateThrift'");
        } catch (TException e) {
            log.error("Erreur DAO Thrift Topaze : {}", e.getMessage(), e);
            throw new ThriftDaoException("Accès DAO Topaze indisponible", e);
        } finally {
            if (transport != null && transport.isOpen()) {
                transport.close();
            }
        }
    }

    /**
     * Interface fonctionnelle pour les opérations Thrift.
     */
    @FunctionalInterface
    public interface ThriftOperation<T> {
        T execute(Object client) throws TException;
    }
}
