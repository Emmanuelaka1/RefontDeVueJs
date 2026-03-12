package com.arkea.sgesapi.config;

import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import com.arkea.sgesapi.thrift.pool.ThriftClientFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration Spring du pool de clients Thrift Topaze.
 * <p>
 * Crée les beans nécessaires pour le pattern Catalyst :
 * <ol>
 *   <li>{@link ThriftClientFactory} — factory Commons Pool2 pour la création/destruction de clients</li>
 *   <li>{@link GenericObjectPoolConfig} — configuration du pool (taille, idle, validation)</li>
 *   <li>{@link TServiceClientPool} — wrapper du pool injecté dans les DAO Thrift</li>
 * </ol>
 * <p>
 * Les propriétés sont lues depuis {@code application.yml} :
 * <pre>
 *   thrift.client.topaze.host
 *   thrift.client.topaze.port
 *   thrift.client.topaze.timeout
 *   thrift.client.topaze.pool.*
 * </pre>
 * <p>
 * Activé sur tous les profils sauf "dev" (val, rec, hml, prod).
 * En dev, aucun pool Thrift n'est instancié — les Mock DAOs prennent le relais.
 *
 * @see com.arkea.sgesapi.thrift.pool.TServiceClientPool
 * @see com.arkea.sgesapi.thrift.pool.ThriftClientFactory
 */
@Configuration
@Profile("!dev")
public class ThriftPoolConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ThriftPoolConfig.class);

    @Value("${thrift.client.topaze.host}")
    private String host;

    @Value("${thrift.client.topaze.port}")
    private int port;

    @Value("${thrift.client.topaze.timeout:5000}")
    private int timeout;

    @Value("${thrift.client.topaze.pool.max-total:8}")
    private int maxTotal;

    @Value("${thrift.client.topaze.pool.max-idle:4}")
    private int maxIdle;

    @Value("${thrift.client.topaze.pool.min-idle:1}")
    private int minIdle;

    @Value("${thrift.client.topaze.pool.test-on-borrow:true}")
    private boolean testOnBorrow;

    @Value("${thrift.client.topaze.pool.test-while-idle:true}")
    private boolean testWhileIdle;

    /**
     * Pool de clients Thrift pour le service DonneesGeneriquesTopaze.
     * <p>
     * Utilise TServiceClient comme type générique tant que le code Thrift
     * n'est pas généré. Remplacer par DonneesGeneriquesTopaze.Client
     * après exécution de {@code gradle generateThrift}.
     */
    @Bean
    public TServiceClientPool<TServiceClient> topazeClientPool() {
        LOG.info("Configuration du pool Thrift Topaze — {}:{} (timeout={}ms)", host, port, timeout);

        // Factory pour créer les clients Thrift
        // TODO: Remplacer TServiceClient.class par DonneesGeneriquesTopaze.Client.class
        //       quand le code Thrift est généré
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, host, port, timeout);

        // Configuration du pool Apache Commons Pool2
        GenericObjectPoolConfig<TServiceClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(testWhileIdle);

        LOG.info("Pool Thrift configuré — maxTotal={}, maxIdle={}, minIdle={}", maxTotal, maxIdle, minIdle);

        return new TServiceClientPool<>(factory, config);
    }
}
