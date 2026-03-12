package com.arkea.sgesapi.dao.impl;

import com.arkea.sgesapi.dao.api.IPersonnesDao;
import com.arkea.sgesapi.dao.api.ThriftClientPool;
import com.arkea.sgesapi.dao.model.PersonneMinimaleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Implémentation DAO Thrift Topaze pour les données personnes.
 * <p>
 * Utilise le ThriftClientPool pour communiquer avec le système externe Topaze.
 * Le mapping DTO ↔ Thrift struct se fait dans cette couche.
 * <p>
 * Note : PersonnesMockDao est marqué @Primary et prend la priorité en dev.
 * Cette implémentation sera activée en production via un profil Spring.
 */
@Repository
public class PersonnesThriftDao implements IPersonnesDao {

    private static final Logger log = LoggerFactory.getLogger(PersonnesThriftDao.class);

    private final ThriftClientPool thriftClientPool;

    public PersonnesThriftDao(ThriftClientPool thriftClientPool) {
        this.thriftClientPool = thriftClientPool;
    }

    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(List<String> identifiantsPersonnes) {
        log.info("DAO Topaze — getInformationsMinimalesPersonnesTopaze nb={}",
                identifiantsPersonnes.size());

        // TODO: Activer quand le code Thrift généré est disponible
        // GetInformationsMinimalesPersonnesTopazeRequest thriftReq =
        //     new GetInformationsMinimalesPersonnesTopazeRequest(identifiantsPersonnes);
        //
        // var thriftResponse = thriftClientPool.execute(c ->
        //     ((DonneesGeneriquesTopaze.Client) c)
        //         .getInformationsMinimalesPersonnesTopaze(thriftReq));
        //
        // return thriftResponse.getPersonnes().stream()
        //     .collect(Collectors.toMap(
        //         p -> p.getIdentifiant(),
        //         p -> new PersonneMinimaleDto(p.getIdentifiant(), p.getNom(), p.getPrenom(), p.getTypePersonne())
        //     ));

        throw new UnsupportedOperationException(
                "Client Thrift non disponible — générer le code avec 'gradle generateThrift'");
    }
}
