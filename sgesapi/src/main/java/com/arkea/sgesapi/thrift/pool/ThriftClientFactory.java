package com.arkea.sgesapi.thrift.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * Factory pour la création d'instances de clients Thrift dans le pool.
 * <p>
 * Pattern Apache Commons Pool2 — chaque objet du pool est un client Thrift
 * connecté via socket TCP au serveur Topaze.
 * <p>
 * Equivalent interne de {@code com.arkea.commons.thrift.pool.ThriftClientFactory}
 * utilisé dans l'écosystème Catalyst Arkea.
 *
 * @param <T> type du client Thrift (ex: DonneesGeneriquesTopaze.Client)
 */
public class ThriftClientFactory<T extends TServiceClient> extends BasePooledObjectFactory<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ThriftClientFactory.class);

    private final String host;
    private final int port;
    private final int timeout;
    private final Class<T> clientClass;

    /**
     * @param clientClass classe du client Thrift généré (ex: DonneesGeneriquesTopaze.Client.class)
     * @param host        hôte du serveur Thrift Topaze
     * @param port        port du serveur Thrift Topaze
     * @param timeout     timeout de connexion en millisecondes
     */
    public ThriftClientFactory(Class<T> clientClass, String host, int port, int timeout) {
        this.clientClass = clientClass;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Crée un nouveau client Thrift avec sa connexion TCP.
     * Appelé par le pool quand il a besoin d'un nouvel objet.
     */
    @Override
    public T create() throws Exception {
        LOG.debug("Création d'un client Thrift {} vers {}:{}", clientClass.getSimpleName(), host, port);

        TTransport transport = new TSocket(host, port, timeout);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);

        // Instancie le client via reflection (le constructeur prend un TProtocol)
        Constructor<T> constructor = clientClass.getConstructor(TProtocol.class);
        return constructor.newInstance(protocol);
    }

    /**
     * Enveloppe le client dans un PooledObject pour le suivi par le pool.
     */
    @Override
    public PooledObject<T> wrap(T client) {
        return new DefaultPooledObject<>(client);
    }

    /**
     * Détruit un client Thrift — ferme sa connexion TCP.
     * Appelé quand le pool invalide ou évacue un objet.
     */
    @Override
    public void destroyObject(PooledObject<T> pooledObject) throws Exception {
        T client = pooledObject.getObject();
        if (client != null) {
            TTransport transport = client.getInputProtocol().getTransport();
            if (transport != null && transport.isOpen()) {
                transport.close();
                LOG.debug("Transport Thrift fermé pour {}", clientClass.getSimpleName());
            }
        }
    }

    /**
     * Vérifie qu'un client du pool est toujours valide (connexion ouverte).
     */
    @Override
    public boolean validateObject(PooledObject<T> pooledObject) {
        T client = pooledObject.getObject();
        if (client == null) {
            return false;
        }
        TTransport transport = client.getInputProtocol().getTransport();
        return transport != null && transport.isOpen();
    }
}
