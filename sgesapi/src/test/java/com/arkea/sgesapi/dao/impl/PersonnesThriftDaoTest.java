package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.thrift.pool.TServiceClientPool;
import org.apache.thrift.TServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires — PersonnesThriftDao.
 */
@ExtendWith(MockitoExtension.class)
class PersonnesThriftDaoTest {

    @Mock
    private TServiceClientPool<TServiceClient> pool;

    @Test
    void getInformationsMinimalesPersonnes_lanceUnsupportedOperation() {
        PersonnesThriftDao dao = new PersonnesThriftDao(pool);

        assertThrows(UnsupportedOperationException.class, () ->
                dao.getInformationsMinimalesPersonnes(List.of("PP-001")));
    }

    @Test
    void getFunctionnalContextId_retourneWsDonneesGeneriquesTopaze() {
        PersonnesThriftDao dao = new PersonnesThriftDao(pool);

        assertEquals("WsDonneesGeneriquesTopaze", dao.getFunctionnalContextId());
    }
}
