package com.arkea.sgesapi.thrift.callback;

import com.arkea.sgesapi.exception.DAOException;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

/**
 * Interface callback pour les appels Thrift via le pattern DAO.
 * <p>
 * Equivalent interne de {@code com.arkea.rpl.dao.thrift.ThriftDaoCallbackIface}
 * utilisé dans l'écosystème Arkea.
 * <p>
 * Le pattern callback permet de séparer la gestion du cycle de vie du client
 * (emprunter/retourner au pool) de la logique d'appel métier.
 * <p>
 * Utilisation typique via lambda :
 * <pre>
 *     super.execute(client -> client.getPersonList(request));
 * </pre>
 *
 * @param <T> type du client Thrift (ex: DonneesGeneriquesTopaze.Client)
 */
@FunctionalInterface
public interface ThriftDaoCallbackIface<T extends TServiceClient> {

    /**
     * Exécute l'opération métier avec le client Thrift fourni.
     *
     * @param client le client Thrift emprunté du pool, connecté et prêt
     * @return le résultat de l'appel Thrift (struct de réponse)
     * @throws DAOException en cas d'erreur métier DAO
     * @throws TException   en cas d'erreur Thrift (transport, protocole)
     */
    Object doInConnection(T client) throws DAOException, TException;
}
