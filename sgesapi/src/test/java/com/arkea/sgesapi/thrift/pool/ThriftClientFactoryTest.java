package com.arkea.sgesapi.thrift.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — ThriftClientFactory.
 */
class ThriftClientFactoryTest {

    @Test
    void create_connexionRefusee_lanceException() {
        // Port invalide — connexion impossible
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 1, 100);

        assertThrows(Exception.class, factory::create);
    }

    @Test
    void wrap_retournePooledObject() {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);

        PooledObject<TServiceClient> wrapped = factory.wrap(client);

        assertNotNull(wrapped);
        assertEquals(client, wrapped.getObject());
    }

    @Test
    void destroyObject_clientAvecTransportOuvert_fermeTransport() throws Exception {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);
        TTransport transport = mock(TTransport.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(transport);
        when(transport.isOpen()).thenReturn(true);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        factory.destroyObject(pooledObject);

        verify(transport).close();
    }

    @Test
    void destroyObject_transportDejaFerme_neFermePas() throws Exception {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);
        TTransport transport = mock(TTransport.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(transport);
        when(transport.isOpen()).thenReturn(false);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        factory.destroyObject(pooledObject);

        verify(transport, never()).close();
    }

    @Test
    void destroyObject_transportNull_neLancePasException() throws Exception {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(null);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        // Should not throw
        factory.destroyObject(pooledObject);
    }

    @Test
    void destroyObject_clientNull_neLancePasException() throws Exception {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        // Wrap a null client via mock
        PooledObject<TServiceClient> pooledObject = mock(PooledObject.class);
        when(pooledObject.getObject()).thenReturn(null);

        // Should not throw
        factory.destroyObject(pooledObject);
    }

    @Test
    void validateObject_clientAvecTransportOuvert_retourneTrue() {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);
        TTransport transport = mock(TTransport.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(transport);
        when(transport.isOpen()).thenReturn(true);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        assertTrue(factory.validateObject(pooledObject));
    }

    @Test
    void validateObject_transportFerme_retourneFalse() {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);
        TTransport transport = mock(TTransport.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(transport);
        when(transport.isOpen()).thenReturn(false);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        assertFalse(factory.validateObject(pooledObject));
    }

    @Test
    void validateObject_clientNull_retourneFalse() {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        PooledObject<TServiceClient> pooledObject = mock(PooledObject.class);
        when(pooledObject.getObject()).thenReturn(null);

        assertFalse(factory.validateObject(pooledObject));
    }

    @Test
    void validateObject_transportNull_retourneFalse() {
        ThriftClientFactory<TServiceClient> factory =
                new ThriftClientFactory<>(TServiceClient.class, "localhost", 9090, 5000);

        TServiceClient client = mock(TServiceClient.class);
        TProtocol protocol = mock(TProtocol.class);

        when(client.getInputProtocol()).thenReturn(protocol);
        when(protocol.getTransport()).thenReturn(null);

        PooledObject<TServiceClient> pooledObject = factory.wrap(client);

        assertFalse(factory.validateObject(pooledObject));
    }
}
