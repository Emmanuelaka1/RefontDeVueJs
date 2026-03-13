package com.arkea.sgesapi.thrift;

import com.arkea.sgesapi.exception.DAOException;
import com.arkea.sgesapi.thrift.callback.ThriftDaoCallbackIface;
import com.arkea.sgesapi.thrift.data.ResponseContext;
import com.arkea.sgesapi.thrift.data.ResponseType;
import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires — AbstractThriftDAO.
 * Utilise une implémentation concrète de test.
 */
@ExtendWith(MockitoExtension.class)
class AbstractThriftDAOTest {

    @Mock
    private TServiceClientPool<TServiceClient> pool;

    @Mock
    private TServiceClient mockClient;

    private TestThriftDAO dao;

    @BeforeEach
    void setUp() {
        dao = new TestThriftDAO(pool);
    }

    @Test
    void execute_succes_retourneResultat() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);
        String expected = "résultat";

        Object result = dao.execute(client -> expected);

        assertEquals(expected, result);
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_transportException_invalideClientEtLanceDAOException() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> {
                    throw new TTransportException("Connexion perdue");
                }));

        assertTrue(ex.getMessage().contains("TestService"));
        verify(pool).invalidateObject(mockClient);
    }

    @Test
    void execute_tException_nInvalidePassClientEtLanceDAOException() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> {
                    throw new TException("Erreur protocole");
                }));

        assertTrue(ex.getMessage().contains("TestService"));
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_daoException_relanceptionDirecte() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> {
                    throw new DAOException("Erreur métier");
                }));

        assertEquals("Erreur métier", ex.getMessage());
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_genericException_invalideClientEtLanceDAOException() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> {
                    throw new RuntimeException("Erreur inattendue");
                }));

        assertTrue(ex.getMessage().contains("TestService"));
        verify(pool).invalidateObject(mockClient);
    }

    @Test
    void execute_borrowObjectEchoue_lanceDAOException() throws Exception {
        when(pool.borrowObject()).thenThrow(new Exception("Pool épuisé"));

        assertThrows(DAOException.class, () ->
                dao.execute(client -> "test"));
    }

    @Test
    void execute_responseContextAvecErreur_lanceDAOException() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        // Create a response object with a responseContext property containing errors
        ResponseContext errorContext = new ResponseContext();
        errorContext.addMessage(ResponseType.ERROR, "Erreur métier Topaze");
        ResponseWithContext responseObj = new ResponseWithContext();
        responseObj.setResponseContext(errorContext);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> responseObj));

        assertTrue(ex.getMessage().contains("Erreur métier Topaze"));
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_responseContextSansErreur_retourneResultat() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        ResponseContext okContext = new ResponseContext();
        okContext.addMessage(ResponseType.SUCCESS, "OK");
        ResponseWithContext responseObj = new ResponseWithContext();
        responseObj.setResponseContext(okContext);

        Object result = dao.execute(client -> responseObj);

        assertNotNull(result);
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_responseSansResponseContext_retourneResultat() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);

        // An object that has NO responseContext property (PropertyUtils will throw)
        Object simpleResult = "résultat simple";

        Object result = dao.execute(client -> simpleResult);

        assertEquals("résultat simple", result);
        verify(pool).returnObject(mockClient);
    }

    @Test
    void execute_finalizeClientEchoue_lanceDAOException() throws Exception {
        when(pool.borrowObject()).thenReturn(mockClient);
        doThrow(new RuntimeException("Pool error")).when(pool).returnObject(mockClient);

        DAOException ex = assertThrows(DAOException.class, () ->
                dao.execute(client -> "test"));

        assertTrue(ex.getMessage().contains("retour to pool"));
    }

    @Test
    void getFunctionnalContextId_retourneValeur() {
        assertEquals("TestService", dao.getFunctionnalContextId());
    }

    // ── POJO avec propriété responseContext pour PropertyUtils ─────

    public static class ResponseWithContext {
        private ResponseContext responseContext;

        public ResponseContext getResponseContext() {
            return responseContext;
        }

        public void setResponseContext(ResponseContext responseContext) {
            this.responseContext = responseContext;
        }
    }

    // ── Implémentation concrète de test ────────────────────────────

    private static class TestThriftDAO extends AbstractThriftDAO<TServiceClient> {

        protected TestThriftDAO(TServiceClientPool<TServiceClient> pool) {
            super(pool);
        }

        @Override
        protected String getFunctionnalContextId() {
            return "TestService";
        }

        // Expose execute pour le test
        @Override
        public Object execute(ThriftDaoCallbackIface<TServiceClient> callback) throws DAOException {
            return super.execute(callback);
        }
    }
}
