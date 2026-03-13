package com.arkea.sgesapi.thrift.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — TServiceClientPool.
 */
class TServiceClientPoolTest {

    @Test
    void constructeurAvecConfig_creePoolSansErreur() {
        TestableFactory factory = new TestableFactory();
        GenericObjectPoolConfig<TServiceClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(4);
        config.setMaxIdle(2);
        config.setMinIdle(0);

        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory, config);

        assertNotNull(pool);
        assertEquals(0, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());
        pool.close();
    }

    @Test
    void constructeurParDefaut_creePoolSansErreur() {
        TestableFactory factory = new TestableFactory();

        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory);

        assertNotNull(pool);
        assertEquals(0, pool.getNumActive());
        pool.close();
    }

    @Test
    void borrowEtReturn_cycleComplet() throws Exception {
        TestableFactory factory = new TestableFactory();
        GenericObjectPoolConfig<TServiceClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(2);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(false);

        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory, config);

        assertEquals(0, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());

        TServiceClient client = pool.borrowObject();
        assertNotNull(client);
        assertEquals(1, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());

        pool.returnObject(client);
        assertEquals(0, pool.getNumActive());
        assertEquals(1, pool.getNumIdle());

        pool.close();
    }

    @Test
    void returnObject_null_neFaitRien() {
        TestableFactory factory = new TestableFactory();
        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory);

        pool.returnObject(null);

        pool.close();
    }

    @Test
    void invalidateObject_null_neFaitRien() {
        TestableFactory factory = new TestableFactory();
        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory);

        pool.invalidateObject(null);

        pool.close();
    }

    @Test
    void invalidateObject_clientValide_detruit() throws Exception {
        TestableFactory factory = new TestableFactory();
        GenericObjectPoolConfig<TServiceClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(2);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(false);

        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory, config);

        TServiceClient client = pool.borrowObject();
        assertEquals(1, pool.getNumActive());

        pool.invalidateObject(client);
        assertEquals(0, pool.getNumActive());
        assertEquals(0, pool.getNumIdle());

        pool.close();
    }

    @Test
    void close_fermeLePool() {
        TestableFactory factory = new TestableFactory();
        TServiceClientPool<TServiceClient> pool = new TServiceClientPool<>(factory);

        pool.close();

        assertThrows(Exception.class, pool::borrowObject);
    }

    /**
     * Factory testable qui crée des mock clients sans connexion réseau.
     */
    private static class TestableFactory extends ThriftClientFactory<TServiceClient> {

        TestableFactory() {
            super(TServiceClient.class, "localhost", 1, 100);
        }

        @Override
        public TServiceClient create() {
            TServiceClient client = mock(TServiceClient.class);
            TProtocol protocol = mock(TProtocol.class);
            TTransport transport = mock(TTransport.class);
            when(client.getInputProtocol()).thenReturn(protocol);
            when(protocol.getTransport()).thenReturn(transport);
            when(transport.isOpen()).thenReturn(true);
            return client;
        }

        @Override
        public PooledObject<TServiceClient> wrap(TServiceClient client) {
            return new DefaultPooledObject<>(client);
        }

        @Override
        public boolean validateObject(PooledObject<TServiceClient> p) {
            return true;
        }
    }
}
