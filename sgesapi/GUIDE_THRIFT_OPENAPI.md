# Guide Technique : Intégration Apache Thrift & OpenAPI

## SaphirGestion (5A02) — sgesapi

> Ce document s'adresse aux développeurs qui n'ont jamais travaillé avec Apache Thrift ni OpenAPI Generator.
> Il explique pas à pas les concepts, l'architecture, et le fonctionnement concret de ces deux technologies
> dans le contexte du projet sgesapi.

---

## Table des matières

1. [Introduction — Pourquoi deux technologies ?](#1-introduction--pourquoi-deux-technologies-)
2. [Vue d'ensemble de l'architecture](#2-vue-densemble-de-larchitecture)
3. [Apache Thrift — Les fondamentaux](#3-apache-thrift--les-fondamentaux)
   - 3.1. Qu'est-ce que Thrift ?
   - 3.2. Le fichier IDL (.thrift)
   - 3.3. Les types Thrift
   - 3.4. Le protocole de communication (TBinaryProtocol / TCP)
   - 3.5. Génération de code Java
4. [OpenAPI — Les fondamentaux](#4-openapi--les-fondamentaux)
   - 4.1. Qu'est-ce qu'OpenAPI ?
   - 4.2. Swagger 2.0 vs OpenAPI 3.0.2
   - 4.3. Le pattern Delegate
   - 4.4. Génération de code Spring Boot
5. [Le cycle de vie d'une requête complète](#5-le-cycle-de-vie-dune-requête-complète)
6. [Thrift dans sgesapi — Implémentation détaillée](#6-thrift-dans-sgesapi--implémentation-détaillée)
   - 6.1. Le fichier IDL du projet
   - 6.2. Architecture Catalyst — Pool, Callback, AbstractDAO
   - 6.3. PersonnesThriftDao — L'implémentation DAO (pattern Catalyst)
   - 6.4. Mapping Thrift struct ↔ DTO Java
7. [OpenAPI dans sgesapi — Implémentation détaillée](#7-openapi-dans-sgesapi--implémentation-détaillée)
   - 7.1. La spécification Loans API (OpenAPI 3.0.2)
   - 7.2. La spécification sgesapi legacy (Swagger 2.0)
   - 7.3. LoansApiDelegateImpl — Le delegate
   - 7.4. Mapping DTO interne → modèle généré
8. [Configuration Gradle — Génération de code](#8-configuration-gradle--génération-de-code)
   - 8.1. Tâche generateThrift
   - 8.2. Tâches generateOpentopazeservice et generateSigacPrets
   - 8.3. Nettoyage des doublons
   - 8.4. Ordre de compilation
9. [Le pattern DAO — Isoler Thrift du reste](#9-le-pattern-dao--isoler-thrift-du-reste)
   - 9.1. Interface DAO
   - 9.2. Mock DAO (@Profile("dev"))
   - 9.3. Thrift DAO (production)
   - 9.4. Basculer entre mock et production
10. [La résolution des personnes — Cas concret complet](#10-la-résolution-des-personnes--cas-concret-complet)
11. [Gestion des erreurs Thrift](#11-gestion-des-erreurs-thrift)
12. [Tests sans serveur Thrift](#12-tests-sans-serveur-thrift)
13. [Guide pas à pas : ajouter un nouvel appel Thrift](#13-guide-pas-à-pas--ajouter-un-nouvel-appel-thrift)
14. [Guide pas à pas : ajouter un nouvel endpoint OpenAPI](#14-guide-pas-à-pas--ajouter-un-nouvel-endpoint-openapi)
15. [Glossaire](#15-glossaire)

---

## 1. Introduction — Pourquoi deux technologies ?

Le projet sgesapi est un **backend intermédiaire** (BFF — Backend For Frontend) qui sert d'interface entre une application Vue.js moderne et un système legacy appelé **Topaze**.

Le problème est le suivant : le système Topaze expose ses services via le protocole **Apache Thrift** (protocole binaire sur TCP). Or, le frontend Vue.js ne peut consommer que des **API REST en JSON** via HTTP.

La solution :

```
┌─────────────┐     HTTP/JSON      ┌──────────────┐     TCP/Thrift     ┌──────────┐
│  Frontend   │ ──────────────────► │   sgesapi    │ ──────────────────► │  Topaze  │
│  Vue.js     │ ◄────────────────── │  (Spring)    │ ◄────────────────── │ (legacy) │
│             │    OpenAPI REST     │              │   TBinaryProtocol   │          │
└─────────────┘                    └──────────────┘                    └──────────┘
```

**OpenAPI** définit le contrat REST entre le frontend et sgesapi (côté gauche).
**Apache Thrift** définit le contrat RPC entre sgesapi et Topaze (côté droit).

sgesapi fait donc la **traduction** entre ces deux mondes.

---

## 2. Vue d'ensemble de l'architecture

```
Frontend Vue.js
      │
      │  GET /loans/{id}    (HTTP JSON — défini par OpenAPI)
      ▼
┌─────────────────────────────────────────────────────────────────┐
│                         sgesapi (Spring Boot)                   │
│                                                                 │
│  ┌─────────────────────┐                                        │
│  │ LoansApiDelegateImpl │ ← implémente l'interface générée      │
│  │ (Delegate OpenAPI)   │   depuis sigac-prets.yaml             │
│  └─────────┬───────────┘                                        │
│            │                                                    │
│            ▼                                                    │
│  ┌─────────────────────┐                                        │
│  │   DossierService    │ ← orchestration métier                 │
│  │                     │   + résolution des noms personnes      │
│  └─────────┬───────────┘                                        │
│            │                                                    │
│    ┌───────┴──────────┐                                         │
│    │                  │                                         │
│    ▼                  ▼                                         │
│  ┌──────────┐  ┌──────────────────┐                             │
│  │IDossierDao│  │ PersonnesService │                             │
│  │(interface)│  │                  │                             │
│  └────┬─────┘  └────────┬─────────┘                             │
│       │                 │                                       │
│       │                 ▼                                       │
│       │          ┌──────────────┐                                │
│       │          │IPersonnesDao │                                │
│       │          │ (interface)  │                                │
│       │          └──────┬───────┘                                │
│       │                 │                                       │
│  ┌─────────────┐ ┌──────┴───────────┐                           │
│  │ MockDao     │ │ PersonnesThriftDao│ ← appel TCP/Thrift       │
│  │ @Profile    │ │ @Profile("!dev") │   vers Topaze             │
│  │ ("dev")     │ │                  │                           │
│  └─────────────┘ └──────┬───────────┘                           │
│                         │                                       │
│                         ▼                                       │
│                  ┌─────────────────────┐                          │
│                  │AbstractThriftDAO    │                          │
│                  │ execute() + pool    │                          │
│                  │ (pattern Catalyst)  │                          │
│                  └──────┬──────────────┘                          │
│                         │                                       │
│                  ┌──────────────────┐                             │
│                  │TServiceClientPool│                             │
│                  │ Commons Pool2    │                             │
│                  └──────┬───────────┘                             │
│                         │                                       │
└─────────────────────────┼───────────────────────────────────────┘
                          │
                          │  TCP / TBinaryProtocol (port 9090)
                          ▼
                    ┌──────────┐
                    │  TOPAZE  │
                    │ (legacy) │
                    └──────────┘
```

---

## 3. Apache Thrift — Les fondamentaux

### 3.1. Qu'est-ce que Thrift ?

Apache Thrift est un **framework RPC** (Remote Procedure Call) créé par Facebook et donné à la fondation Apache. Il permet à deux applications, potentiellement écrites dans des langages différents, de communiquer efficacement.

Concrètement, Thrift c'est :

- Un **langage de définition d'interface** (IDL) — un fichier `.thrift` qui décrit les structures de données et les services disponibles.
- Un **compilateur** (`thrift`) — qui génère du code client/serveur dans le langage cible (Java, Python, C++, etc.).
- Un **protocole binaire** — les données ne transitent pas en texte (comme JSON) mais en format binaire compact, ce qui est plus rapide.
- Un **transport TCP** — communication directe via socket TCP, sans HTTP.

**Analogie simple** : si REST/JSON est comme envoyer une lettre par la poste (lisible par tous, format texte), Thrift est comme un appel téléphonique direct (plus rapide, protocole privé entre les deux parties).

### 3.2. Le fichier IDL (.thrift)

Le fichier IDL (Interface Definition Language) est le **contrat** entre le client et le serveur Thrift. C'est l'équivalent du fichier OpenAPI YAML pour le monde REST.

Voici la structure d'un fichier `.thrift` :

```thrift
// 1. NAMESPACE — dans quel package Java le code sera généré
namespace java com.arkea.sgesapi.thrift.gen

// 2. STRUCT — structure de données (équivalent d'un objet JSON)
struct PersonneMinimale {
    1: optional string identifiant       // champ 1
    2: optional string nom               // champ 2
    3: optional string prenom            // champ 3
    4: optional string typePersonne      // PP ou PM
    5: optional string dateNaissance     // champ 5
}

// 3. REQUEST — la requête envoyée au serveur
struct GetInformationsMinimalesPersonnesTopazeRequest {
    1: required list<string> identifiantsPersonnes
}

// 4. RESPONSE — la réponse renvoyée par le serveur
struct GetInformationsMinimalesPersonnesTopazeResponse {
    1: optional list<PersonneMinimale> personnes
}

// 5. EXCEPTIONS — les erreurs métier et techniques
exception TopazeMetierException {
    1: required i32    code
    2: required string message
}

exception TopazeServiceException {
    1: required i32    code
    2: required string message
}

// 6. SERVICE — la "classe distante" exposant des méthodes
service DonneesGeneriquesTopaze {
    GetInformationsMinimalesPersonnesTopazeResponse
        getInformationsMinimalesPersonnesTopaze(
            1: GetInformationsMinimalesPersonnesTopazeRequest request
        ) throws (
            1: TopazeServiceException serviceEx,
            2: TopazeMetierException  metierEx
        )
}
```

**Points clés à comprendre :**

- Les **numéros de champ** (1:, 2:, 3:...) sont essentiels. Thrift utilise ces numéros (pas les noms) pour sérialiser/désérialiser. On ne doit **jamais réutiliser** un numéro supprimé.
- `required` signifie que le champ doit obligatoirement être présent, sinon erreur.
- `optional` signifie que le champ peut être absent (null).
- `list<string>` est l'équivalent d'un `List<String>` Java ou d'un `string[]` JSON.
- Un `service` est comme une **interface Java** avec des méthodes. Le compilateur génère un client et un serveur pour ce service.

### 3.3. Les types Thrift

| Type Thrift      | Équivalent Java       | Équivalent JSON     |
|------------------|-----------------------|---------------------|
| `bool`           | `boolean`             | `true/false`        |
| `byte`           | `byte`                | nombre              |
| `i16`            | `short`               | nombre              |
| `i32`            | `int`                 | nombre              |
| `i64`            | `long`                | nombre              |
| `double`         | `double`              | nombre décimal      |
| `string`         | `String`              | `"texte"`           |
| `binary`         | `ByteBuffer`          | base64              |
| `list<T>`        | `List<T>`             | `[...]`             |
| `set<T>`         | `Set<T>`              | `[...]` (unique)    |
| `map<K,V>`       | `Map<K,V>`            | `{...}`             |
| `struct`         | Classe Java générée   | Objet JSON `{}`     |
| `exception`      | Exception Java générée| Erreur              |

### 3.4. Le protocole de communication (TBinaryProtocol / TCP)

Quand sgesapi appelle Topaze, voici ce qui se passe au niveau réseau :

```
sgesapi (client)                                      Topaze (serveur)
     │                                                      │
     │  1. Ouvrir une connexion TCP (socket)                │
     │ ─────────────────────────────────────────────────────►│
     │                                                      │
     │  2. Envoyer la requête en binaire (TBinaryProtocol)  │
     │ ─────────────────────────────────────────────────────►│
     │     ┌──────────────────────────────────┐              │
     │     │ Nom méthode (string)             │              │
     │     │ Type message (CALL)              │              │
     │     │ Sequence ID                      │              │
     │     │ Champs struct (field_id + value) │              │
     │     └──────────────────────────────────┘              │
     │                                                      │
     │  3. Recevoir la réponse en binaire                   │
     │ ◄─────────────────────────────────────────────────────│
     │     ┌──────────────────────────────────┐              │
     │     │ Type message (REPLY)             │              │
     │     │ Sequence ID                      │              │
     │     │ Champs struct réponse            │              │
     │     └──────────────────────────────────┘              │
     │                                                      │
     │  4. Fermer la connexion TCP                          │
     │ ─────────────────────────────────────────────────────►│
```

**Pourquoi du binaire et pas du JSON ?** Le format binaire est plus compact et plus rapide à sérialiser/désérialiser. Sur des systèmes legacy traitant des millions de transactions bancaires, chaque milliseconde compte.

**Pourquoi TCP et pas HTTP ?** TCP est la couche en dessous de HTTP. En supprimant la couche HTTP (headers, statuts, etc.), on gagne en performance. Le compromis est qu'on perd la compatibilité directe avec les navigateurs web (d'où le besoin de sgesapi comme traducteur).

### 3.5. Génération de code Java

Le compilateur Thrift prend le fichier `.thrift` en entrée et génère des classes Java :

```bash
# Commande de génération
thrift --gen java -out build/generated-sources/thrift src/main/thrift/topaze-personnes.thrift
```

Le compilateur génère automatiquement :

| Fichier généré | Rôle |
|---|---|
| `PersonneMinimale.java` | Classe Java pour le struct `PersonneMinimale` — avec getters, setters, sérialisation binaire |
| `GetInformationsMinimalesPersonnesTopazeRequest.java` | Classe pour la struct de requête |
| `GetInformationsMinimalesPersonnesTopazeResponse.java` | Classe pour la struct de réponse |
| `TopazeMetierException.java` | Exception Java correspondant à l'exception Thrift |
| `TopazeServiceException.java` | Idem pour l'exception service |
| `DonneesGeneriquesTopaze.java` | Classe contenant `Client` (pour appeler) et `Iface` (pour implémenter) |

Le code le plus important est la classe `DonneesGeneriquesTopaze.Client` :

```java
// Code GÉNÉRÉ automatiquement — NE PAS MODIFIER
public class DonneesGeneriquesTopaze {

    // Interface serveur (implémentée par Topaze)
    public interface Iface {
        GetInformationsMinimalesPersonnesTopazeResponse
            getInformationsMinimalesPersonnesTopaze(
                GetInformationsMinimalesPersonnesTopazeRequest request
            ) throws TopazeServiceException, TopazeMetierException, TException;
    }

    // Client (utilisé par sgesapi pour appeler Topaze)
    public static class Client implements Iface {
        public Client(TProtocol protocol) { ... }

        @Override
        public GetInformationsMinimalesPersonnesTopazeResponse
            getInformationsMinimalesPersonnesTopaze(
                GetInformationsMinimalesPersonnesTopazeRequest request
            ) throws TopazeServiceException, TopazeMetierException, TException {
            // Sérialise la requête en binaire, l'envoie via TCP,
            // reçoit la réponse binaire, la désérialise en objet Java
            ...
        }
    }
}
```

---

## 4. OpenAPI — Les fondamentaux

### 4.1. Qu'est-ce qu'OpenAPI ?

OpenAPI (anciennement Swagger) est une **spécification** pour décrire des API REST. C'est un fichier YAML ou JSON qui définit les endpoints, les paramètres, les réponses, et les modèles de données.

Son rôle dans sgesapi : définir le **contrat HTTP** entre le frontend Vue.js et le backend Spring Boot.

**Analogie** : si le fichier `.thrift` est le "contrat" entre sgesapi et Topaze, le fichier OpenAPI est le "contrat" entre le frontend et sgesapi. Même concept, protocoles différents.

```
Frontend ◄── OpenAPI (contrat HTTP/JSON) ──► sgesapi ◄── Thrift (contrat TCP/binaire) ──► Topaze
```

### 4.2. Swagger 2.0 vs OpenAPI 3.0.2

Le projet sgesapi utilise **deux spécifications** OpenAPI. Voici pourquoi et les différences :

| Aspect | Swagger 2.0 (`openapi.json`) | OpenAPI 3.0.2 (`sigac-prets.yaml`) |
|--------|------------------------------|-------------------------------------|
| Fichier | `src/main/resources/specs/openapi.json` | `openapi/sigac-prets.yaml` |
| Format | JSON | YAML |
| Rôle | Spec legacy (endpoints Topaze REST + recherche) | Spec Loans API (endpoints prêts simplifiés) |
| Endpoints | `POST /api/v1/recherche/dossiers`, `GET /api/v1/dossiers/{numeroPret}`, `POST /rested/DonneesGeneriquesTopazeService/...` | `GET /loans/{id}` |
| Packages générés | `com.arkea.sgesapi.dao.api.opentopazeservice` + `com.arkea.sgesapi.dao.model.opentopazeservice` | `com.arkea.sgesapi.api.sigac` + `com.arkea.sgesapi.model.sigac` |
| Delegate généré | `DonneesGeneriquesTopaze*ApiDelegate` + `ConsultationApiDelegate` | `LoansApiDelegate` |

**Pourquoi deux specs ?**

- La spec **Swagger 2.0** (`openapi.json`) reflète l'API originale existante, proche du système legacy. Elle documente les anciens endpoints REST.
- La spec **OpenAPI 3.0.2** (`sigac-prets.yaml`) est la nouvelle API simplifiée Loans API utilisée par le frontend Vue.js. C'est celle que le frontend consomme.

### 4.3. Le pattern Delegate

Le **delegate pattern** est une stratégie de génération de code OpenAPI. Voici comment ça fonctionne :

**Sans delegate** (classique) :
```
OpenAPI YAML → Génère un Controller Java → Vous modifiez le controller directement
                                            (problème : si vous regénérez, vos modifications sont écrasées !)
```

**Avec delegate** (notre cas) :
```
OpenAPI YAML → Génère un Controller + une Interface Delegate
                                       │
                                       ▼
                         Vous implémentez le Delegate
                         (vos modifications survivent à la regénération !)
```

Concrètement, le générateur crée :

```java
// FICHIER GÉNÉRÉ (ne pas toucher) — PretsApi.java
@RestController
@RequestMapping("/")
public class LoansApiController implements LoansApi {

    private final LoansApiDelegate delegate;

    // Injecte automatiquement votre implémentation
    public LoansApiController(LoansApiDelegate delegate) {
        this.delegate = delegate;
    }

    @GetMapping("/loans/{id}")
    public ResponseEntity<CommonLoan> getLoan(@PathVariable String id) {
        return delegate.getLoan(id);  // ← délègue à VOTRE code
    }
}

// FICHIER GÉNÉRÉ (ne pas toucher) — LoansApiDelegate.java
public interface LoansApiDelegate {
    default ResponseEntity<CommonLoan> getLoan(String id) {
        return ResponseEntity.status(501).build();  // Not Implemented par défaut
    }
}
```

```java
// VOTRE FICHIER (à modifier librement) — LoansApiDelegateImpl.java
@Service
public class LoansApiDelegateImpl implements LoansApiDelegate {

    @Override
    public ResponseEntity<CommonLoan> getLoan(String id) {
        // id = numéro contrat souscrit prêt (ex: "PRT-2024-08-1547")
        DossierConsultationDto dto = dossierService.consulterDossierParContratSouscrit(id);
        return ResponseEntity.ok(toCommonLoan(dto));
    }
}
```

**Avantage clé** : le controller REST, les modèles de requête/réponse, et le routage Spring sont tous générés automatiquement. Vous n'écrivez que la logique métier dans le delegate.

### 4.4. Génération de code Spring Boot

Le plugin `openapi-generator` de Gradle génère :

| Fichier généré | Rôle |
|---|---|
| `LoansApiController.java` | Controller Spring avec `@GetMapping`, `@PostMapping`, etc. |
| `LoansApi.java` | Interface du controller avec les annotations OpenAPI |
| `LoansApiDelegate.java` | Interface delegate que vous implémentez |
| `CommonLoan.java` | Modèle Java pour le schema `CommonLoan` (avec getters/setters) |
| `Participant.java` | Modèle Java pour le schema `Participant` |
| `LoanType.java` | Modèle Java pour le schema `LoanType` |
| `LoanState.java` | Modèle Java pour le schema `LoanState` |
| `CurrentPayment.java` | Modèle Java pour le schema `CurrentPayment` |
| `APIError.java` | Modèle Java pour les erreurs |
| `APIMessage.java` | Modèle Java pour les messages |
| `DatesPret.java` | Modèle Java pour le schema `DatesPret` |
| `ObjectCode.java` | Modèle Java pour le schema `ObjectCode` |
| ... | Tous les schemas définis dans `components/schemas` |

---

## 5. Le cycle de vie d'une requête complète

Prenons l'exemple concret d'un utilisateur qui consulte un dossier de prêt. Voici chaque étape, de la saisie au résultat affiché :

```
ÉTAPE 1 — L'utilisateur clique sur un dossier dans l'écran de recherche
──────────────────────────────────────────────────────────────────────────
Le navigateur envoie :
    GET http://localhost:9088/loans/PRT-2024-08-1547
    Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

ÉTAPE 2 — Le Controller généré (OpenAPI) reçoit la requête HTTP
──────────────────────────────────────────────────────────────────────────
LoansApiController.getLoan("PRT-2024-08-1547")
    → Délègue à LoansApiDelegateImpl.getLoan("PRT-2024-08-1547")

ÉTAPE 3 — Le Delegate appelle le Service métier
──────────────────────────────────────────────────────────────────────────
LoansApiDelegateImpl.getLoan("PRT-2024-08-1547")
    → dossierService.consulterDossierParContratSouscrit("PRT-2024-08-1547")

ÉTAPE 4 — DossierService consulte le DAO
──────────────────────────────────────────────────────────────────────────
DossierService.consulterDossierParContratSouscrit("PRT-2024-08-1547")
    → dossierDao.consulterDossierParContratSouscrit("PRT-2024-08-1547")
    → Retourne DossierConsultationDto avec :
        noEmprunteur = "PP-001547-E"      (identifiant Topaze)
        noCoEmprunteur = "PP-003892-C"    (identifiant Topaze)
        emprunteur = null                 (pas encore résolu)

ÉTAPE 5 — DossierService résout les noms via PersonnesService
──────────────────────────────────────────────────────────────────────────
DossierService.enrichirNomPersonnes(dossier)
    → personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-003892-C")
        → personnesDao.getInformationsMinimalesPersonnes(["PP-001547-E", "PP-003892-C"])

ÉTAPE 5a — EN DEV (@Profile "dev") : PersonnesMockDao répond (pas de connexion Topaze)
    → Retourne Map {"PP-001547-E" → PersonneMinimaleDto("MARTIN", "Jean-Pierre", "PP"),
                    "PP-003892-C" → PersonneMinimaleDto("DURAND", "Marie", "PP")}

ÉTAPE 5b — EN VAL/REC/HML/PROD (@Profile "!dev") : PersonnesThriftDao appelle Topaze via TCP/Thrift
    → Emprunte un client du TServiceClientPool (Commons Pool2)
    → Envoie requête binaire getInformationsMinimalesPersonnesTopaze
    → Reçoit réponse binaire avec les PersonneMinimale
    → Retourne le client au pool (ou invalide si erreur transport)
    → Convertit les struct Thrift en PersonneMinimaleDto

ÉTAPE 6 — DossierService enrichit le DTO
──────────────────────────────────────────────────────────────────────────
dossier.setEmprunteur("MARTIN Jean-Pierre")
dossier.setCoEmprunteur("DURAND Marie")

ÉTAPE 7 — Le Delegate convertit en modèle OpenAPI généré (CommonLoan)
──────────────────────────────────────────────────────────────────────────
LoansApiDelegateImpl.toCommonLoan(dossier)
    → Crée CommonLoan depuis DossierConsultationDto :
        loan.setId(dto.getNumeroContratSouscritPret())
        loan.setMasterContractId(dto.getNumeroContratSouscritProjet())
        loan.setDuration(dto.getDureePret())
        loan.setBorrowedAmount(dto.getMontantPret())
    → Crée LoanType (codeNature + libelleNature)
    → Crée LoanState (codeEtat + libelleEtat)
    → Crée ObjectCode (codeObjet + libelleObjet)
    → Crée Participant (emprunteur EMP + co-emprunteur COE)
        splitNomPrenom("MARTIN Jean-Pierre") → lastName="MARTIN", firstName="Jean-Pierre"
    → Retourne ResponseEntity.ok(commonLoan)

ÉTAPE 8 — Spring Boot sérialise en JSON et renvoie la réponse HTTP
──────────────────────────────────────────────────────────────────────────
HTTP 200 OK
Content-Type: application/json

{
  "id": "PRT-2024-08-1547",
  "masterContractId": "PRJ-2024-08-1547",
  "label": "Prêt à l'Accession à la Propriété",
  "typeCode": "PAP",
  "duration": 240,
  "borrowedAmount": 250000.0,
  "rate": 3.45,
  "periodicity": "M",
  "loanType": { "code": "PAP", "label": "Prêt à l'Accession à la Propriété" },
  "loanState": { "code": "40", "label": "En gestion" },
  "objectCode": { "code": "01", "label": "Acquisition ancien" },
  "participants": [
    { "roleCode": "EMP", "personNumber": "PP-001547-E", "lastName": "MARTIN", "firstName": "Jean-Pierre" },
    { "roleCode": "COE", "personNumber": "PP-001547-C", "lastName": "MARTIN", "firstName": "Catherine" }
  ]
}
```

---

## 6. Thrift dans sgesapi — Implémentation détaillée

### 6.1. Le fichier IDL du projet

Le fichier `src/main/thrift/topaze-personnes.thrift` définit le contrat avec Topaze :

```thrift
namespace java com.arkea.sgesapi.thrift.gen

// Requête : on envoie une liste d'identifiants
struct GetInformationsMinimalesPersonnesTopazeRequest {
    1: required list<string> identifiantsPersonnes
}

// Chaque personne retournée
struct PersonneMinimale {
    1: optional string identifiant
    2: optional string nom
    3: optional string prenom
    4: optional string typePersonne    // PP (Personne Physique) ou PM (Personne Morale)
    5: optional string dateNaissance
}

// Réponse : liste de personnes
struct GetInformationsMinimalesPersonnesTopazeResponse {
    1: optional list<PersonneMinimale> personnes
}

// Erreurs
exception TopazeMetierException {
    1: required i32    code
    2: required string message
}

exception TopazeServiceException {
    1: required i32    code
    2: required string message
}

// Service distant
service DonneesGeneriquesTopaze {
    GetInformationsMinimalesPersonnesTopazeResponse
        getInformationsMinimalesPersonnesTopaze(
            1: GetInformationsMinimalesPersonnesTopazeRequest request)
        throws (1: TopazeServiceException serviceEx,
                2: TopazeMetierException   metierEx)
}
```

### 6.2. Architecture Catalyst — Pool, Callback, AbstractDAO

L'accès à Topaze suit le **pattern Catalyst Arkea**, une architecture en couches reproduite de l'écosystème RPL interne. Voici la hiérarchie complète :

```
PersonnesThriftDao
  extends AbstractThriftDAO<DonneesGeneriquesTopaze.Client>
    extends AbstractCatalystThriftDAO<DonneesGeneriquesTopaze.Client>
  implements IPersonnesDao
```

#### a) TServiceClientPool — Pool Apache Commons Pool2

Au lieu d'ouvrir/fermer une connexion TCP à chaque appel, on utilise un **pool de clients réutilisables** basé sur Apache Commons Pool2. Le pool gère automatiquement la création, la validation et la destruction des clients Thrift.

```java
public class TServiceClientPool<T extends TServiceClient> {
    private final GenericObjectPool<T> internalPool;

    // Emprunter un client prêt à l'emploi
    public T borrowObject() throws Exception {
        return internalPool.borrowObject();
    }

    // Retourner un client valide au pool
    public void returnObject(T client) {
        internalPool.returnObject(client);
    }

    // Invalider un client défectueux (erreur transport)
    public void invalidateObject(T client) {
        internalPool.invalidateObject(client);
    }
}
```

La **factory** `ThriftClientFactory` crée les clients via réflexion :

```java
public class ThriftClientFactory<T extends TServiceClient>
        extends BasePooledObjectFactory<T> {

    @Override
    public T create() throws Exception {
        TTransport transport = new TSocket(host, port, timeout);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        Constructor<T> ctor = clientClass.getConstructor(TProtocol.class);
        return ctor.newInstance(protocol);
    }
}
```

#### b) ThriftDaoCallbackIface — Interface fonctionnelle callback

```java
@FunctionalInterface
public interface ThriftDaoCallbackIface<T extends TServiceClient> {
    Object doInConnection(T client) throws DAOException, TException;
}
```

C'est le contrat pour les appels Thrift. Chaque DAO concret passe un lambda :
`super.execute(client -> client.getInformationsMinimalesPersonnesTopaze(request))`

#### c) AbstractCatalystThriftDAO — Gestion du pool

Classe de base qui encapsule le pool et fournit `getClient()` / `finalizeClient()` :

```java
public abstract class AbstractCatalystThriftDAO<T extends TServiceClient> {
    private final TServiceClientPool<T> pool;

    protected T getClient() throws DAOException {
        try { return pool.borrowObject(); }
        catch (Exception e) { throw new DAOException("Erreur pool", e); }
    }

    protected void finalizeClient(T client, boolean invalidate) throws DAOException {
        if (invalidate) pool.invalidateObject(client);
        else pool.returnObject(client);
    }
}
```

#### d) AbstractThriftDAO — La méthode execute() avec gestion des erreurs

C'est le cœur du pattern. La méthode `execute()` gère le cycle de vie complet :

```java
public abstract class AbstractThriftDAO<T extends TServiceClient>
        extends AbstractCatalystThriftDAO<T> {

    protected abstract String getFunctionnalContextId();

    protected Object execute(ThriftDaoCallbackIface<T> clientCallback) throws DAOException {
        boolean invalidateClient = false;
        T client = null;

        try {
            // 1. Emprunter un client du pool
            client = getClient();

            // 2. Exécuter le callback (appel Thrift)
            Object resp = clientCallback.doInConnection(client);

            // 3. Analyser le ResponseContext (erreurs métier)
            Object ctx = PropertyUtils.getProperty(resp, "responseContext");
            if (ctx instanceof ResponseContext rc) {
                if (rc.containsKey(ResponseType.ERROR)) {
                    throw new DAOException(rc.getFirstError());
                }
            }
            return resp;

        } catch (TTransportException e) {
            invalidateClient = true;   // Connexion cassée → invalider
            throw new DAOException("Erreur pour " + getFunctionnalContextId(), e);
        } catch (DAOException e) {
            throw e;                   // Erreur métier → propager
        } catch (TException e) {
            throw new DAOException("Erreur pour " + getFunctionnalContextId(), e);
        } catch (Exception e) {
            invalidateClient = true;   // Erreur inattendue → invalider
            throw new DAOException("Erreur pour " + getFunctionnalContextId(), e);
        } finally {
            // 4. TOUJOURS retourner/invalider le client dans le pool
            finalizeClient(client, invalidateClient);
        }
    }
}
```

**Points clés** :
- `TTransportException` (connexion perdue) → on **invalide** le client dans le pool
- `TException` (erreur protocole) → on ne l'invalide pas (erreur fonctionnelle possible)
- `DAOException` (erreur métier) → on la propage telle quelle
- Le bloc `finally` garantit que le client est **toujours** retourné ou invalidé

#### e) Configuration Spring (ThriftPoolConfig)

Le pool est configuré via `application.yml` et instancié par Spring :

Les valeurs par défaut sont dans `application.yml`, surchargées par environnement dans `application-{profil}.yml` :

```yaml
# application.yml (défauts communs)
thrift:
  client:
    topaze:
      host: ${TOPAZE_THRIFT_HOST:localhost}
      port: ${TOPAZE_THRIFT_PORT:9090}
      timeout: ${TOPAZE_THRIFT_TIMEOUT:5000}
      pool:
        max-total: ${TOPAZE_POOL_MAX_TOTAL:8}
        max-idle: ${TOPAZE_POOL_MAX_IDLE:4}
        min-idle: ${TOPAZE_POOL_MIN_IDLE:1}
        test-on-borrow: true
        test-while-idle: true
```

| Profil | Host Topaze | Pool max-total | Logging |
|--------|-------------|----------------|---------|
| **dev** | *(pas de Thrift — Mock DAOs)* | — | DEBUG |
| **val** | `topaze-val.arkea.local` | 4 | DEBUG |
| **rec** | `topaze-rec.arkea.local` | 8 | INFO |
| **hml** | `topaze-hml.arkea.local` | 8 | INFO |
| **prod** | `topaze.arkea.local` | 16 | WARN |

```java
@Configuration
@Profile("!dev")   // ← NON chargé en dev (pas besoin de pool sans Thrift)
public class ThriftPoolConfig {
    @Bean
    public TServiceClientPool<TServiceClient> topazeClientPool() {
        ThriftClientFactory<TServiceClient> factory =
            new ThriftClientFactory<>(TServiceClient.class, host, port, timeout);
        GenericObjectPoolConfig<TServiceClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        // ...
        return new TServiceClientPool<>(factory, config);
    }
}
```

### 6.3. PersonnesThriftDao — L'implémentation DAO (pattern Catalyst)

Le `PersonnesThriftDao` suit le pattern Catalyst en étendant `AbstractThriftDAO` et en utilisant `super.execute()` pour chaque appel Thrift :

```java
@Repository
@Profile("!dev")   // ← actif sur val, rec, hml, prod
public class PersonnesThriftDao
        extends AbstractThriftDAO<TServiceClient>       // ou DonneesGeneriquesTopaze.Client
        implements IPersonnesDao {

    public PersonnesThriftDao(TServiceClientPool<TServiceClient> pool) {
        super(pool);    // Injecte le pool configuré par ThriftPoolConfig
    }

    @Override
    protected String getFunctionnalContextId() {
        return "WsDonneesGeneriquesTopaze";   // Pour les logs et messages d'erreur
    }

    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
            List<String> identifiantsPersonnes) throws DAOException {

        // 1. Construire la requête Thrift (struct générée)
        GetInformationsMinimalesPersonnesTopazeRequest thriftReq =
            new GetInformationsMinimalesPersonnesTopazeRequest(identifiantsPersonnes);

        // 2. Appeler Topaze via le pattern execute/callback Catalyst
        GetInformationsMinimalesPersonnesTopazeResponse thriftResp =
            (GetInformationsMinimalesPersonnesTopazeResponse)
                super.execute(client ->
                    client.getInformationsMinimalesPersonnesTopaze(thriftReq));

        // 3. Convertir la réponse Thrift (struct) → DTO interne
        Map<String, PersonneMinimaleDto> result = new HashMap<>();
        if (thriftResp.getPersonnes() != null) {
            for (var p : thriftResp.getPersonnes()) {
                result.put(p.getIdentifiant(),
                    new PersonneMinimaleDto(
                        p.getIdentifiant(),
                        p.getNom(),
                        p.getPrenom(),
                        p.getTypePersonne()));
            }
        }
        return result;
    }
}
```

**Différences clés avec l'ancien pattern** :
- On **hérite** de `AbstractThriftDAO` au lieu de composer avec un `ThriftClientPool` simple
- L'appel passe par `super.execute(callback)` qui gère automatiquement le pool, les erreurs, et le `ResponseContext`
- `getFunctionnalContextId()` identifie le service pour les logs (comme dans le code RPL Arkea)
- `DAOException` (checked) remplace `ThriftDaoException` (runtime) — forçant la gestion explicite des erreurs

**Pourquoi convertir en DTO ?** Le code généré par Thrift (`PersonneMinimale` struct) contient des dépendances lourdes sur `libthrift` (sérialisation binaire, etc.). En convertissant en `PersonneMinimaleDto` (une classe Java simple), on **isole** le reste de l'application de Thrift. Le Service et le Delegate ne savent même pas que Thrift existe.

### 6.4. Mapping Thrift struct ↔ DTO Java

```
  Thrift struct (GÉNÉRÉ)                     DTO Java (NOTRE CODE)
┌──────────────────────────┐            ┌───────────────────────────┐
│ PersonneMinimale         │            │ PersonneMinimaleDto       │
│ (com.arkea...thrift.gen) │            │ (com.arkea...dao.model)   │
│                          │            │                           │
│ + getIdentifiant()       │ ────────►  │ - identifiant: String     │
│ + getNom()               │ ────────►  │ - nom: String             │
│ + getPrenom()            │ ────────►  │ - prenom: String          │
│ + getTypePersonne()      │ ────────►  │ - typePersonne: String    │
│ + getDateNaissance()     │     ✗      │                           │
│                          │ (ignoré)   │ + getLibelleComplet()     │
│ + read(TProtocol)        │            │   → "NOM Prénom"          │
│ + write(TProtocol)       │            │                           │
│ (méthodes sérialisation) │            │ (POJO simple, pas de      │
│                          │            │  dépendance Thrift)        │
└──────────────────────────┘            └───────────────────────────┘
```

---

## 7. OpenAPI dans sgesapi — Implémentation détaillée

### 7.1. La spécification Loans API (OpenAPI 3.0.2)

Le fichier `openapi/sigac-prets.yaml` définit l'API consommée par le frontend :

```yaml
openapi: 3.0.2
info:
  title: Loans API
  version: 1.0.0

servers:
  - url: /

paths:
  /loans/{id}:
    get:
      summary: Récupérer les détails d'un prêt
      operationId: getDossier
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Détails complets du prêt
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/CommonLoan'
        '404':
          description: Prêt introuvable
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/APIError'

components:
  schemas:
    CommonLoan:
      type: object
      properties:
        id:
          type: string
        loanType:
          $ref: '#/components/schemas/LoanType'
        participants:
          type: array
          items:
            $ref: '#/components/schemas/Participant'
        currentPayment:
          $ref: '#/components/schemas/CurrentPayment'
        loanState:
          $ref: '#/components/schemas/LoanState'

    Participant:
      type: object
      properties:
        name:
          type: string
        role:
          type: string

    LoanType:
      type: object
      properties:
        code:
          type: string
        label:
          type: string

    ObjectCode:
      type: object
      properties:
        code:
          type: string

    LoanState:
      type: object
      properties:
        status:
          type: string

    CurrentPayment:
      type: object
      properties:
        amount:
          type: number
        dueDate:
          type: string
          format: date

    APIError:
      type: object
      properties:
        error:
          $ref: '#/components/schemas/APIMessage'

    APIMessage:
      type: object
      properties:
        code:
          type: string
        message:
          type: string
```

**Points clés :**

- `operationId: getDossier` → devient le nom de la méthode Java `getDossier(String id)`.
- `$ref: '#/components/schemas/CommonLoan'` → référence un schéma réutilisable (comme un import).
- `required: true` sur un paramètre → Spring renvoie automatiquement 400 si absent.
- Les schémas `CommonLoan`, `Participant`, `LoanType`, `LoanState`, `CurrentPayment` définissent la structure de la réponse.

### 7.2. La spécification sgesapi legacy (Swagger 2.0)

Le fichier `src/main/resources/specs/openapi.json` est l'ancienne spec (format Swagger 2.0 / JSON) :

```json
{
  "swagger": "2.0",
  "info": {
    "title": "5A02 - sgesapi",
    "version": "1.0.0"
  },
  "paths": {
    "/api/v1/recherche/dossiers": {
      "post": {
        "operationId": "rechercherDossiers",
        "parameters": [{ "in": "body", "name": "RechercheDossierRequest", ... }],
        "responses": { "200": { "schema": { "$ref": "#/definitions/RechercheDossierResponse" } } }
      }
    },
    "/rested/DonneesGeneriquesTopazeService/getInformationsMinimalesPersonnesTopaze": {
      "post": {
        "operationId": "getInformationsMinimalesPersonnesTopaze",
        ...
      }
    }
  }
}
```

**Différences clés entre Swagger 2.0 et OpenAPI 3.0.2 :**

| Aspect | Swagger 2.0 | OpenAPI 3.0.2 |
|--------|-------------|---------------|
| Mot-clé version | `"swagger": "2.0"` | `openapi: 3.0.2` |
| Schemas | `definitions` | `components/schemas` |
| Body params | `"in": "body"` | `requestBody` avec `content` |
| Format réponse | `schema` direct | `content: application/json: schema` |
| Serveurs | `basePath` / `host` | `servers: [{ url: ... }]` |

### 7.3. LoansApiDelegateImpl — Le delegate

C'est le seul fichier OpenAPI que vous écrivez à la main. Tout le reste est généré :

```java
@Service
public class LoansApiDelegateImpl implements LoansApiDelegate {

    private static final Logger log = LoggerFactory.getLogger(LoansApiDelegateImpl.class);
    private final DossierService dossierService;

    public LoansApiDelegateImpl(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    // GET /loans/{id} — id = numéro contrat souscrit prêt
    @Override
    public ResponseEntity<CommonLoan> getLoan(String id) {
        log.info("Loans API — getLoan id={}", id);
        try {
            DossierConsultationDto dto = dossierService.consulterDossierParContratSouscrit(id);
            return ResponseEntity.ok(toCommonLoan(dto));
        } catch (DossierNotFoundException e) {
            log.warn("Dossier non trouvé : {}", id);
            return ResponseEntity.notFound().build();   // HTTP 404
        } catch (DAOException e) {
            log.error("Erreur DAO lors de la consultation du dossier {}", id, e);
            return ResponseEntity.internalServerError().build();  // HTTP 500
        }
    }

    // Conversion DossierConsultationDto → CommonLoan (modèle OpenAPI généré)
    CommonLoan toCommonLoan(DossierConsultationDto dto) {
        CommonLoan loan = new CommonLoan();

        // Identifiant = numéro contrat souscrit prêt
        loan.setId(dto.getNumeroContratSouscritPret());
        loan.setMasterContractId(dto.getNumeroContratSouscritProjet());

        // Durée et montant
        loan.setDuration(dto.getDureePret());
        loan.setBorrowedAmount(toBigDecimal(dto.getMontantPret()));
        loan.setRate(toBigDecimal(dto.getTauxRemboursement()));
        loan.setAvailableAmount(toBigDecimal(dto.getMontantDisponible()));

        // Périodicité — par défaut mensuelle
        loan.setPeriodicity(CommonLoan.PeriodicityEnum.M);

        // Libellé et type
        loan.setLabel(dto.getLibelleNature());
        loan.setTypeCode(dto.getCodeNature());

        // LoanType, ObjectCode, LoanState
        LoanType loanType = new LoanType();
        loanType.setCode(dto.getCodeNature());
        loanType.setLabel(dto.getLibelleNature());
        loan.setLoanType(loanType);

        ObjectCode objectCode = new ObjectCode();
        objectCode.setCode(dto.getCodeObjet());
        objectCode.setLabel(dto.getLibelleObjet());
        loan.setObjectCode(objectCode);

        LoanState loanState = new LoanState();
        loanState.setCode(dto.getCodeEtat());
        loanState.setLabel(dto.getLibelleEtat());
        loan.setLoanState(loanState);

        // Participants (emprunteur + co-emprunteur éventuel)
        List<Participant> participants = new ArrayList<>();
        if (dto.getNoEmprunteur() != null) {
            Participant emp = new Participant();
            emp.setPersonNumber(dto.getNoEmprunteur());
            emp.setPersonFederation(dto.getEfs());
            emp.setRoleCode("EMP");
            splitNomPrenom(dto.getEmprunteur(), emp);
            participants.add(emp);
        }
        if (dto.getNoCoEmprunteur() != null) {
            Participant coe = new Participant();
            coe.setPersonNumber(dto.getNoCoEmprunteur());
            coe.setPersonFederation(dto.getEfs());
            coe.setRoleCode("COE");
            splitNomPrenom(dto.getCoEmprunteur(), coe);
            participants.add(coe);
        }
        loan.setParticipants(participants);

        return loan;
    }

    // Sépare "NOM Prénom" en lastName/firstName
    void splitNomPrenom(String fullName, Participant participant) {
        if (fullName == null || fullName.isBlank()) return;
        String trimmed = fullName.trim();
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace > 0) {
            participant.setLastName(trimmed.substring(0, firstSpace));
            participant.setFirstName(trimmed.substring(firstSpace + 1));
        } else {
            participant.setLastName(trimmed);
        }
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}
```

### 7.4. Mapping DTO interne → modèle généré

Le delegate effectue une **double conversion** :

```
   Thrift struct         DTO interne              Modèle OpenAPI généré         JSON HTTP
  (côté Topaze)        (notre code)              (code généré)               (côté frontend)
┌───────────────┐   ┌──────────────────┐   ┌───────────────────────┐   ┌──────────────────┐
│PersonneMinimale│→  │PersonneMinimaleDto│   │                       │   │                  │
│(binaire Thrift)│   │(POJO Java)       │   │                       │   │                  │
└───────────────┘   └──────────────────┘   │                       │   │                  │
                                           │                       │   │                  │
┌───────────────┐   ┌──────────────────┐   │  Participant          │   │ "participants":  │
│ Thrift struct  │→  │DossierConsultation│→  │  (modèle généré)      │→  │   roleCode: EMP  │
│ (dossier)      │   │Dto               │   │                       │   │   lastName:      │
└───────────────┘   └──────────────────┘   │  CommonLoan           │   │   'MARTIN'       │
                                           │  (modèle généré)      │   │ id: 'PRT-2024..' │
                         PersonnesThriftDao │  + LoanType, etc.      │   │ loanType: {...}  │
                         fait la 1ère      │                       │   │                  │
                         conversion        └───────────────────────┘   └──────────────────┘
                                           LoansApiDelegateImpl         Jackson (auto)
                                           fait la 2ème conversion
```

---

## 8. Configuration Gradle — Génération de code

### 8.1. Tâche generateThrift

```groovy
def thriftSrcDir = "${projectDir}/src/main/thrift"
def thriftOutDir = "${buildDir}/generated-sources/thrift"

task generateThrift {
    description = 'Génère les classes Java depuis les fichiers .thrift'
    group = 'build'

    inputs.dir thriftSrcDir     // ne re-génère que si les .thrift changent
    outputs.dir thriftOutDir    // dossier de sortie

    doLast {
        mkdir thriftOutDir
        fileTree(thriftSrcDir).matching { include '**/*.thrift' }.each { file ->
            exec {
                // Appelle le binaire 'thrift' installé sur la machine
                commandLine 'thrift', '--gen', 'java', '-out', thriftOutDir, file.absolutePath
            }
        }
    }
}
```

**Prérequis** : le binaire `thrift` doit être installé sur la machine :

```bash
# macOS
brew install thrift

# Ubuntu / Debian
sudo apt-get install thrift-compiler

# Vérification
thrift --version    # Apache Thrift version 0.20.0
```

### 8.2. Tâches generateOpentopazeservice et generateSigacPrets

```groovy
// Génération depuis openapi.json (Swagger 2.0)
task generateOpentopazeservice(type: org.openapitools.generator.gradle.plugin.tasks.GenerateTask) {
    generatorName = "spring"                    // génère du Spring Boot
    library       = "spring-boot"               // librairie Spring Boot 3
    inputSpec     = "${projectDir}/src/main/resources/specs/openapi.json"
    outputDir     = "${buildDir}/java/generatedOpentopazeservice"
    validateSpec  = false                       // ne pas valider (Swagger 2.0 tolère des écarts)
    configOptions = [
        apiPackage              : "com.arkea.sgesapi.dao.api.opentopazeservice",
        modelPackage            : "com.arkea.sgesapi.dao.model.opentopazeservice",
        "delegatePattern"       : "true",       // ← active le pattern delegate !
        "useSpringBoot3"        : "true",       // utilise jakarta.* au lieu de javax.*
        "interfaceOnly"         : "false",      // génère controllers + delegates
        "dateLibrary"           : "java8",      // utilise java.time.*
        "hideGenerationTimestamp": "true"        // pas de timestamp dans les commentaires
    ]
}

// Génération depuis sigac-prets.yaml (OpenAPI 3.0.2)
task generateSigacPrets(type: org.openapitools.generator.gradle.plugin.tasks.GenerateTask) {
    generatorName = "spring"
    library       = "spring-boot"
    inputSpec     = "${projectDir}/openapi/sigac-prets.yaml"
    outputDir     = "${buildDir}/java/generatedSigacPrets"
    validateSpec  = false
    configOptions = [
        apiPackage              : "com.arkea.sgesapi.api.sigac",
        modelPackage            : "com.arkea.sgesapi.model.sigac",
        "delegatePattern"       : "true",
        "useSpringBoot3"        : "true",
        "interfaceOnly"         : "false",
        "dateLibrary"           : "java8",
        "hideGenerationTimestamp": "true"
    ]
}
```

### 8.3. Nettoyage des doublons

Quand on génère deux specs OpenAPI, chacune produit des classes utilitaires identiques (dans `org.openapitools`). Il faut les supprimer d'une des deux :

```groovy
// Exclusion des doublons via sourceSets (nouveau mode)
sourceSets {
    main {
        java {
            srcDirs 'src/main/java',
                    "${buildDir}/generated-sources/thrift",
                    "${buildDir}/java/generatedOpentopazeservice/src/main/java",
                    "${buildDir}/java/generatedSigacPrets/src/main/java"
            // Exclure les classes utilitaires en doublon d'une des deux générations
            exclude 'org/openapitools/**'
        }
    }
}
```

### 8.4. Ordre de compilation

```groovy
// Avant de compiler, on génère le code depuis les deux specs
compileJava.dependsOn generateOpentopazeservice, generateSigacPrets
```

**Chaîne de build complète :**

```
gradle build
    │
    ├── generateOpentopazeservice   (OpenAPI → Java, spec Swagger 2.0)
    ├── generateSigacPrets          (OpenAPI → Java, spec OpenAPI 3.0.2)
    │       │
    │       ▼
    ├── compileJava                  (compile avec sourceSets exclude pour éviter les doublons)
    │       │
    │       ▼
    ├── test                         (tests unitaires)
    │       │
    │       ▼
    └── bootJar                      (crée le .jar exécutable)
```

---

## 9. Le pattern DAO — Isoler Thrift du reste

### 9.1. Interface DAO

L'interface DAO est la **frontière** entre le monde Thrift et le reste de l'application :

```java
public interface IPersonnesDao {
    Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
        List<String> identifiantsPersonnes);
}
```

Tout le code au-dessus (Service, Delegate, Controller) ne connaît **que cette interface**. Il ne sait pas si les données viennent de Thrift, d'une base de données, ou d'un fichier statique.

### 9.2. Mock DAO (`@Profile("dev")`)

En développement, le mock DAO est le seul bean `IPersonnesDao` chargé par Spring :

```java
@Repository
@Profile("dev")   // ← chargé uniquement avec le profil "dev"
public class PersonnesMockDao implements IPersonnesDao {

    private final Map<String, PersonneMinimaleDto> personnesStore = new HashMap<>();

    @PostConstruct
    void initMockData() {
        personnesStore.put("PP-001547-E", new PersonneMinimaleDto(
            "PP-001547-E", "MARTIN", "Jean-Pierre", "PP"));
        personnesStore.put("PP-002891-E", new PersonneMinimaleDto(
            "PP-002891-E", "DUPONT", "Marie", "PP"));
        // ... 8 personnes mock au total
    }

    @Override
    public Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
            List<String> identifiantsPersonnes) throws DAOException {
        return identifiantsPersonnes.stream()
            .filter(id -> id != null && personnesStore.containsKey(id))
            .collect(Collectors.toMap(id -> id, personnesStore::get));
    }
}
```

**`@Profile("dev")` expliqué** : Spring ne crée ce bean que si le profil actif est `dev`. Sur les autres profils (val, rec, hml, prod), ce bean n'existe pas du tout — `PersonnesThriftDao` prend le relais.

### 9.3. Thrift DAO (`@Profile("!dev")` — pattern Catalyst)

Sur tous les environnements hors dev, le DAO Thrift hérite de `AbstractThriftDAO` et utilise le pool Catalyst :

```java
@Repository
@Profile("!dev")   // ← actif sur val, rec, hml, prod (tout sauf dev)
public class PersonnesThriftDao
        extends AbstractThriftDAO<TServiceClient>   // → DonneesGeneriquesTopaze.Client
        implements IPersonnesDao {

    public PersonnesThriftDao(TServiceClientPool<TServiceClient> pool) {
        super(pool);
    }

    @Override
    protected String getFunctionnalContextId() {
        return "WsDonneesGeneriquesTopaze";
    }

    // Chaque méthode métier appelle super.execute(client -> client.xxx(request))
}
```

La configuration du pool est également conditionnée :

```java
@Configuration
@Profile("!dev")   // ← pas de pool Thrift instancié en dev
public class ThriftPoolConfig {
    @Bean
    public TServiceClientPool<TServiceClient> topazeClientPool() { ... }
}
```

### 9.4. Les 5 environnements

| Profil | DAO actif | ThriftPoolConfig | Host Topaze | Fichier |
|--------|-----------|------------------|-------------|---------|
| **dev** | `PersonnesMockDao` | Non chargé | — | `application-dev.yml` |
| **val** | `PersonnesThriftDao` | Chargé | `topaze-val.arkea.local` | `application-val.yml` |
| **rec** | `PersonnesThriftDao` | Chargé | `topaze-rec.arkea.local` | `application-rec.yml` |
| **hml** | `PersonnesThriftDao` | Chargé | `topaze-hml.arkea.local` | `application-hml.yml` |
| **prod** | `PersonnesThriftDao` | Chargé | `topaze.arkea.local` | `application-prod.yml` |

### 9.5. Basculer entre environnements

```bash
# Développement local (mock par défaut — pas besoin de Topaze)
./gradlew bootRun
# → profil "dev" activé via application.yml : spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# Validation
SPRING_PROFILES_ACTIVE=val ./gradlew bootRun

# Recette
SPRING_PROFILES_ACTIVE=rec java -jar sgesapi.jar

# Homologation
java -jar sgesapi.jar -Dspring.profiles.active=hml

# Production
SPRING_PROFILES_ACTIVE=prod java -jar sgesapi.jar
```

---

## 10. La résolution des personnes — Cas concret complet

Ce schéma montre le flux complet de résolution des noms dans le contexte réel :

```
                         DONNÉE EN BASE (ou mock)
                     ┌──────────────────────────────┐
                     │ DossierConsultationDto        │
                     │   numeroPret: "2024-PAP-001547│
                     │   noEmprunteur: "PP-001547-E" │  ← identifiant Topaze brut
                     │   noCoEmprunteur: "PP-003892-C│  ← identifiant Topaze brut
                     │   emprunteur: null             │  ← pas encore résolu
                     │   coEmprunteur: null           │
                     └──────────────┬───────────────┘
                                    │
                                    ▼
                     ┌──────────────────────────────┐
                     │ DossierService                │
                     │   .enrichirNomPersonnes()     │
                     │                               │
                     │ Extrait les identifiants :     │
                     │   ["PP-001547-E","PP-003892-C"]│
                     └──────────────┬───────────────┘
                                    │
                                    ▼
                     ┌──────────────────────────────┐
                     │ PersonnesService              │
                     │   .resoudreEmprunteurCoEmpr() │
                     │                               │
                     │ Appel groupé (1 seul appel    │
                     │ pour les 2 identifiants)       │
                     └──────────────┬───────────────┘
                                    │
                                    ▼
                     ┌──────────────────────────────┐
                     │ IPersonnesDao                 │
                     │   .getInformationsMinimales   │
                     │      Personnes(identifiants)  │
                     └──────────────┬───────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
              DEV (mock)      PROD (Thrift)         │
                    │               │               │
                    ▼               ▼               │
             HashMap.get()    TCP → Topaze          │
                    │          → Réponse            │
                    │               │               │
                    └───────┬───────┘               │
                            │                       │
                            ▼                       │
              ┌────────────────────────────┐        │
              │ Map<String, PersonneDto>   │        │
              │ "PP-001547-E" → MARTIN JP  │        │
              │ "PP-003892-C" → DURAND M   │        │
              └────────────┬───────────────┘        │
                           │                        │
                           ▼                        │
              ┌────────────────────────────┐        │
              │ DossierConsultationDto      │        │
              │   emprunteur:              │        │
              │     "MARTIN Jean-Pierre"   │  ← résolu !
              │   coEmprunteur:            │        │
              │     "DURAND Marie"         │  ← résolu !
              └────────────────────────────┘
```

**Point important** : la résolution est **non-bloquante**. Si Topaze est indisponible, le dossier est quand même renvoyé avec les identifiants bruts comme fallback :

```java
try {
    String[] libelles = personnesService.resoudreEmprunteurCoEmprunteur(noEmp, noCoEmp);
    if (libelles[0] != null) dossier.setEmprunteur(libelles[0]);
    if (libelles[1] != null) dossier.setCoEmprunteur(libelles[1]);
} catch (Exception e) {
    log.warn("Impossible de résoudre les personnes : {}", e.getMessage());
    // Le dossier est retourné avec emprunteur = noEmprunteur (identifiant brut)
}
```

---

## 11. Gestion des erreurs Thrift (pattern Catalyst)

Le pattern Catalyst gère les erreurs à deux niveaux : dans `AbstractThriftDAO.execute()` (multi-catch) et via le `ResponseContext` des réponses Thrift.

### Tableau des erreurs

```
Type d'erreur                  Cause typique                   Traitement dans AbstractThriftDAO
─────────────────────────────────────────────────────────────────────────────────────────────────
TTransportException            Connexion perdue, timeout        → invalidateClient = true
                               (réseau, port fermé)                → DAOException → HTTP 500

ResponseContext.ERROR          Erreur métier Topaze             → DAOException (message métier)
                               (personne introuvable, etc.)        → HTTP 500 ou traitement spécifique

TException                     Erreur de protocole Thrift       → DAOException (pas d'invalidation)
(libthrift)                    (protocole incompatible)            → HTTP 500

Exception (générique)          Erreur pool (borrowObject)       → invalidateClient = true
                               ou erreur inattendue                → DAOException → HTTP 500
```

### DAOException — Exception checked

La classe `DAOException` remplace l'ancienne `ThriftDaoException` (runtime). C'est une **exception checked** (`extends Exception`) qui force la gestion explicite dans la couche service :

```java
public class DAOException extends Exception {
    public DAOException(String message) { super(message); }
    public DAOException(String message, Throwable cause) { super(message, cause); }
    public DAOException(Throwable cause) { super(cause); }
}
```

### Gestion au niveau du delegate (controller)

Le `GlobalExceptionHandler` intercepte `DAOException` et la convertit en HTTP 500 :

```java
@ExceptionHandler(DAOException.class)
public ResponseEntity<Map<String, Object>> handleDAOException(DAOException ex) {
    log.error("Erreur DAO : {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "status", 500,
        "message", "Erreur d'accès au système Topaze"
    ));
}
```

### Invalidation vs retour au pool

Le choix entre invalider ou retourner le client est critique :
- **Invalider** (`invalidateClient = true`) : le client est détruit. Le pool en créera un nouveau au besoin. Utilisé quand la connexion TCP est probablement cassée (`TTransportException`, `Exception` générique).
- **Retourner** (`invalidateClient = false`) : le client est remis dans le pool pour réutilisation. Utilisé quand l'erreur est fonctionnelle (`TException`, `DAOException`) et que la connexion est toujours valide.

---

## 12. Tests sans serveur Thrift

Le projet dispose de **170 tests unitaires** avec **98% de couverture d'instructions** (JaCoCo), tous exécutables sans connexion Topaze.

### 12.1. Tests des services (couche métier)

Le mock DAO permet de tester la logique métier sans serveur Topaze :

```java
@ExtendWith(MockitoExtension.class)
class DossierServiceTest {

    @Mock
    private IDossierDao dossierDao;

    @Mock
    private PersonnesService personnesService;

    @InjectMocks
    private DossierService dossierService;

    @Test
    void consulterDossier_avecEmprunteurEtCoEmprunteur() {
        // 1. Simuler le DAO
        DossierConsultationDto dto = DossierConsultationDto.builder()
            .numeroPret("2024-PAP-001547")
            .noEmprunteur("PP-001547-E")
            .noCoEmprunteur("PP-003892-C")
            .build();
        when(dossierDao.consulterDossier("2024-PAP-001547"))
            .thenReturn(Optional.of(dto));

        // 2. Simuler PersonnesService (qui aurait appelé Thrift)
        when(personnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-003892-C"))
            .thenReturn(new String[]{"MARTIN Jean-Pierre", "DURAND Marie"});

        // 3. Exécuter
        DossierConsultationDto result = dossierService.consulterDossier("2024-PAP-001547");

        // 4. Vérifier — les noms sont résolus
        assertEquals("MARTIN Jean-Pierre", result.getEmprunteur());
        assertEquals("DURAND Marie", result.getCoEmprunteur());
    }
}
```

### 12.2. Tests de l'infrastructure Thrift (sans réseau)

La couche Thrift est testée via des classes internes de test et des mocks Mockito :

- **AbstractThriftDAOTest** (11 tests) : utilise une `TestThriftDAO` interne qui expose `execute()`. Teste tous les chemins du multi-catch (TTransportException, TException, DAOException, RuntimeException), la branche ResponseContext (erreur métier, succès, absence), et l'échec de `finalizeClient`.
- **AbstractCatalystThriftDAOTest** (6 tests) : vérifie `getClient()`, `finalizeClient()` (retour/invalidation/erreur), `getPool()`.
- **TServiceClientPoolTest** (7 tests) : utilise une `TestableFactory` qui crée des mock clients sans connexion réseau. Teste le cycle borrow/return, invalidation, null-safety, et fermeture du pool.
- **ThriftClientFactoryTest** (10 tests) : teste `wrap()`, `destroyObject()` (transport ouvert/fermé/null, client null), `validateObject()` (ouvert/fermé/null, client null).

**On ne teste jamais le code Thrift généré lui-même** (c'est la responsabilité d'Apache Thrift). On teste notre logique métier avec le DAO mocké, et l'infrastructure Thrift (pool, factory, execute) avec des mocks réseau.

---

## 13. Guide pas à pas : ajouter un nouvel appel Thrift

Supposons qu'on doive ajouter un nouvel appel vers Topaze pour récupérer l'historique d'un dossier.

**Étape 1 — Modifier le fichier .thrift**

```thrift
// src/main/thrift/topaze-personnes.thrift — ajouter :

struct HistoriqueDossierRequest {
    1: required string numeroPret
}

struct EvenementDossier {
    1: optional string dateEvenement
    2: optional string typeEvenement
    3: optional string description
}

struct HistoriqueDossierResponse {
    1: optional list<EvenementDossier> evenements
}

// Ajouter la méthode au service existant :
service DonneesGeneriquesTopaze {
    // ... méthode existante ...

    HistoriqueDossierResponse getHistoriqueDossier(
        1: HistoriqueDossierRequest request)
    throws (1: TopazeServiceException serviceEx,
            2: TopazeMetierException metierEx)
}
```

**Étape 2 — Régénérer le code Thrift**

```bash
./gradlew generateThrift
```

**Étape 3 — Créer le DTO interne**

```java
// src/main/java/com/arkea/sgesapi/dao/model/EvenementDossierDto.java
public class EvenementDossierDto {
    private String dateEvenement;
    private String typeEvenement;
    private String description;
    // constructeur, getters, setters
}
```

**Étape 4 — Ajouter la méthode à l'interface DAO**

```java
public interface IDossierDao {
    // ... méthodes existantes ...
    List<EvenementDossierDto> getHistoriqueDossier(String numeroPret);
}
```

**Étape 5 — Implémenter dans le Mock DAO**

```java
@Override
public List<EvenementDossierDto> getHistoriqueDossier(String numeroPret) {
    return List.of(
        new EvenementDossierDto("2024-01-15", "CREATION", "Dossier créé"),
        new EvenementDossierDto("2024-02-01", "DEBLOCAGE", "Fonds débloqués")
    );
}
```

**Étape 6 — Implémenter dans le Thrift DAO (pattern Catalyst)**

Le DAO Thrift étend `AbstractThriftDAO` et utilise `super.execute()` :

```java
// Dans DossierThriftDao extends AbstractThriftDAO<DonneesGeneriquesTopaze.Client>

@Override
public List<EvenementDossierDto> getHistoriqueDossier(String numeroPret) throws DAOException {
    HistoriqueDossierRequest thriftReq = new HistoriqueDossierRequest(numeroPret);

    // Appel via le pattern callback Catalyst
    HistoriqueDossierResponse thriftResp =
        (HistoriqueDossierResponse) super.execute(client ->
            client.getHistoriqueDossier(thriftReq));

    return thriftResp.getEvenements().stream()
        .map(e -> new EvenementDossierDto(
            e.getDateEvenement(), e.getTypeEvenement(), e.getDescription()))
        .collect(Collectors.toList());
}
```

Note : `super.execute()` gère automatiquement le pool (borrow/return/invalidate), le `ResponseContext`, et toutes les exceptions Thrift.

**Étape 7 — Ajouter la logique métier dans le Service**

```java
public List<EvenementDossierDto> getHistoriqueDossier(String numeroPret) {
    return dossierDao.getHistoriqueDossier(numeroPret);
}
```

**Étape 8 — Écrire les tests**

```java
@Test
void getHistoriqueDossier_retourneEvenements() {
    when(dossierDao.getHistoriqueDossier("2024-PAP-001547"))
        .thenReturn(List.of(new EvenementDossierDto("2024-01-15", "CREATION", "Créé")));

    var result = dossierService.getHistoriqueDossier("2024-PAP-001547");

    assertEquals(1, result.size());
    assertEquals("CREATION", result.get(0).getTypeEvenement());
}
```

---

## 14. Guide pas à pas : ajouter un nouvel endpoint OpenAPI

Supposons qu'on doive exposer l'historique du dossier via un nouvel endpoint REST.

**Étape 1 — Modifier la spec OpenAPI**

```yaml
# openapi/sigac-prets.yaml — ajouter dans paths:

  /prets/{id}/historique:
    get:
      summary: Historique d'un dossier de pret
      operationId: getHistoriqueDossier
      tags:
        - Prets
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Liste des evenements du dossier
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ServiceResponseHistorique'

# Ajouter dans components/schemas:

    EvenementDossier:
      type: object
      properties:
        dateEvenement:
          type: string
        typeEvenement:
          type: string
        description:
          type: string

    ServiceResponseHistorique:
      type: object
      properties:
        data:
          type: array
          items:
            $ref: '#/components/schemas/EvenementDossier'
        success:
          type: boolean
        message:
          type: string
```

**Étape 2 — Régénérer le code OpenAPI**

```bash
./gradlew generateSigacPrets
```

Le générateur met à jour `LoansApiDelegate` avec la nouvelle méthode :

```java
// GÉNÉRÉ automatiquement dans LoansApiDelegate.java
default ResponseEntity<LoanHistory> getHistoriqueDossier(String id) {
    return ResponseEntity.status(501).build();
}
```

**Étape 3 — Implémenter dans le delegate**

```java
// LoansApiDelegateImpl.java — ajouter :

@Override
public ResponseEntity<LoanHistory> getHistoriqueDossier(String id) {
    log.info("Loans API — getHistoriqueDossier id={}", id);

    List<EvenementDossierDto> evenements = dossierService.getHistoriqueDossier(id);

    List<EvenementDossier> sigacEvenements = evenements.stream()
        .map(e -> {
            EvenementDossier ev = new EvenementDossier();
            ev.setDateEvenement(e.getDateEvenement());
            ev.setTypeEvenement(e.getTypeEvenement());
            ev.setDescription(e.getDescription());
            return ev;
        })
        .collect(Collectors.toList());

    ServiceResponseHistorique response = new ServiceResponseHistorique();
    response.setData(sigacEvenements);
    response.setSuccess(true);
    response.setMessage("OK");

    return ResponseEntity.ok(response);
}
```

**Étape 4 — Tester**

```bash
# Rebuild complet
./gradlew clean build

# Tester l'endpoint
curl http://localhost:9088/loans/DOSS-2024-001/historique
```

**Résumé de la chaîne complète :**

```
sigac-prets.yaml     →  gradle generateSigacPrets  →  LoansApiDelegate (généré)
(vous modifiez ici)                                    LoansApiController (généré)
                                                       CommonLoan (modèle généré)

                                                       LoansApiDelegateImpl (vous implémentez ici)
                                                            │
                                                            ▼
                                                       DossierService (votre logique)
                                                            │
                                                            ▼
                                                       IDossierDao → MockDao / ThriftDao
```

---

## 15. Glossaire

| Terme | Définition |
|-------|-----------|
| **IDL** | Interface Definition Language — langage de description d'interface utilisé par Thrift |
| **RPC** | Remote Procedure Call — appel de procédure distante, comme si on appelait une méthode locale |
| **TBinaryProtocol** | Protocole binaire Thrift — format compact de sérialisation des données |
| **TSocket** | Transport TCP Thrift — connexion socket brute (pas de HTTP) |
| **struct** | Structure de données Thrift — équivalent d'une classe Java ou d'un objet JSON |
| **service** | Service Thrift — interface distante exposant des méthodes appelables via RPC |
| **OpenAPI** | Spécification pour décrire des API REST (anciennement Swagger) |
| **Swagger 2.0** | Ancienne version de la spécification OpenAPI (format JSON historique) |
| **OpenAPI 3.0.2** | Version moderne de la spécification (format YAML, plus expressif) |
| **Delegate Pattern** | Stratégie de génération où le controller généré délègue à une interface que vous implémentez |
| **DAO** | Data Access Object — couche d'abstraction pour l'accès aux données |
| **DTO** | Data Transfer Object — objet de transfert entre couches (sans logique métier) |
| **@Profile** | Annotation Spring — active un bean uniquement pour un ou plusieurs profils donnés (dev, val, rec, hml, prod). `@Profile("!dev")` = actif sur tout sauf dev |
| **BFF** | Backend For Frontend — backend intermédiaire dédié au frontend |
| **Topaze** | Système legacy interne exposant des services via Thrift |
| **SIGAC** | Système d'Information de Gestion et d'Administration des Crédits |
| **PP** | Personne Physique (individu) |
| **PM** | Personne Morale (entreprise, association) |
| **Catalyst** | Framework interne Arkea fournissant les classes abstraites pour les DAO Thrift (pool, callback, execute) |
| **TServiceClientPool** | Pool Apache Commons Pool2 de clients Thrift réutilisables |
| **ThriftDaoCallbackIface** | Interface fonctionnelle callback pour les appels Thrift (doInConnection) |
| **AbstractCatalystThriftDAO** | Classe de base Catalyst gérant le pool (getClient/finalizeClient) |
| **AbstractThriftDAO** | Classe abstraite avec execute() — gestion complète du cycle de vie et des erreurs |
| **ResponseContext** | Objet d'analyse des réponses Thrift — contient les messages par type (ERROR, WARNING, etc.) |
| **DAOException** | Exception checked levée par la couche DAO — remplace ThriftDaoException |
| **Commons Pool2** | Apache Commons Pool2 — bibliothèque de pooling d'objets (GenericObjectPool) |

---

> **Document rédigé pour le projet SaphirGestion (5A02) — sgesapi**
> Stack : Java 21, Spring Boot 3.3.0, Apache Thrift 0.20.0, OpenAPI Generator 7.6.0
> Architecture DAO Thrift : Pattern Catalyst Arkea (Commons Pool2, Callback, AbstractThriftDAO)
