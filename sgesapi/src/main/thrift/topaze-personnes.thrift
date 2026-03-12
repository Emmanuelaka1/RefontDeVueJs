namespace java com.arkea.sgesapi.thrift.gen

/**
 * Requête — données minimales d'une liste de personnes (PP ou PM)
 */
struct GetInformationsMinimalesPersonnesTopazeRequest {
    1: required list<string> identifiantsPersonnes
}

struct PersonneMinimale {
    1: optional string identifiant
    2: optional string nom
    3: optional string prenom
    4: optional string typePersonne    // PP ou PM
    5: optional string dateNaissance
}

struct GetInformationsMinimalesPersonnesTopazeResponse {
    1: optional list<PersonneMinimale> personnes
}

exception TopazeMetierException {
    1: required i32    code
    2: required string message
}

exception TopazeServiceException {
    1: required i32    code
    2: required string message
}

service DonneesGeneriquesTopaze {
    /**
     * Récupération des données minimales d'une liste de personnes (PP ou PM)
     */
    GetInformationsMinimalesPersonnesTopazeResponse getInformationsMinimalesPersonnesTopaze(
        1: GetInformationsMinimalesPersonnesTopazeRequest request)
        throws (1: TopazeServiceException serviceEx,
                2: TopazeMetierException   metierEx)
}
