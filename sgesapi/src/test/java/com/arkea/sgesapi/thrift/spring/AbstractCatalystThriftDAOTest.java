package com.arkea.sgesapi.thrift.spring;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import org.apache.thrift.TServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — AbstractCatalystThriftDAO.
 */
@ExtendWith(MockitoExtension.class)
class AbstractCatalystThriftDAOTest {

    @Mock
    private TServiceClientPool<TServiceClient> pool;

    @Mock
    private TServiceClient mockClient;

    private TestCatalystDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TestCatalystDAO(pool);
    }

    @Test
    void getClient_emprunterDuPool() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        TServiceClient client = dao.getClient();

        assertNotNull(client);
        assertEquals(mockClient, client);
        verify(pool).borrowObject();
    }

    @Test
    void getClient_poolEchoue_lanceDAOException() throws Exception {
        when(pool.borrowObject()).thenThrow(new Exception("Pool exhausted"));

        assertThrows(DAOException.class, () -> dao.getClient());
    }

    @Test
    void finalizeClient_retourneAuPool() throws DAOException {
        dao.finalizeClient(mockClient, false);

        verify(pool).returnObject(mockClient);
        verify(pool, never()).invalidateObject(any());
    }

    @Test
    void finalizeClient_invalideClient() throws DAOException {
        dao.finalizeClient(mockClient, true);

        verify(pool).invalidateObject(mockClient);
        verify(pool, never()).returnObject(any());
    }

    @Test
    void finalizeClient_erreurRetour_lanceDAOException() throws Exception {
        doThrow(new RuntimeException("Erreur retour")).when(pool).returnObject(mockClient);

        assertThrows(DAOException.class, () -> dao.finalizeClient(mockClient, false));
    }

    @Test
    void getPool_retournePool() {
        assertEquals(pool, dao.getPool());
    }

    // ── Implémentation concrète de test ────────────────────────────

    private static class TestCatalystDAO extends AbstractCatalystThriftDAO<TServiceClient> {
        protected TestCatalystDAO(TServiceClientPool<TServiceClient> pool) {
            super(pool);
        }
    }
}
