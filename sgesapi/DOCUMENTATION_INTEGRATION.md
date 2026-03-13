# SGESAPI - Guide d'Intégration Backend

**Application** : SaphirGestion (5A02) - Backend REST API
**Version** : 1.0.0-SNAPSHOT
**Stack** : Java 21, Spring Boot 3.3.0, Gradle 8.7
**Date** : Mars 2026

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Prérequis](#2-prérequis)
3. [Étape 1 - Installation et build](#3-étape-1---installation-et-build)
4. [Étape 2 - Configuration](#4-étape-2---configuration)
5. [Étape 3 - Lancement du backend](#5-étape-3---lancement-du-backend)
6. [Étape 4 - Connexion du frontend](#6-étape-4---connexion-du-frontend)
7. [Architecture technique](#7-architecture-technique)
8. [Catalogue des API REST](#8-catalogue-des-api-rest)
9. [Sécurité JWT](#9-sécurité-jwt)
10. [Couche DAO - Accès aux données](#10-couche-dao---accès-aux-données)
11. [Résolution des personnes (Topaze)](#11-résolution-des-personnes-topaze)
12. [Gestion des erreurs](#12-gestion-des-erreurs)
13. [Données mock de démonstration](#13-données-mock-de-démonstration)
14. [Environnements et profils Spring](#14-environnements-et-profils-spring)
15. [Tests unitaires et couverture](#15-tests-unitaires-et-couverture)
16. [Troubleshooting](#16-troubleshooting)

---

## 1. Vue d'ensemble

SGESAPI est le backend Java Spring Boot de l'application SaphirGestion. Il expose des API REST consommées par le frontend Vue.js (RefontDeVueJs) pour la gestion des dossiers de prêts immobiliers.

**Flux principal** :

```
[Frontend Vue.js]  ──HTTP/JSON──>  [SGESAPI Spring Boot]  ──Thrift/TCP──>  [Topaze]
     :3000                              :9088                              :9090
```

Le backend fournit trois types d'API :

- **API SIGAC** (OpenAPI 3.0.3 - `sigac-prets.yaml`) : `GET /api/v1/prets` et `GET /api/v1/prets/{id}` — utilisées par le frontend Vue.js
- **API Recherche** (contrôleurs manuels) : `POST /api/v1/recherche/dossiers` et `GET /api/v1/recherche/dossiers` — recherche multicritères
- **API Consultation** (contrôleur manuel) : `GET /api/v1/dossiers/{numeroPret}` — consultation détaillée
- **API Auth** : `POST /api/v1/auth/login` — authentification JWT

En mode développement, toutes les données proviennent de **mocks internes** (pas de connexion Topaze requise).

---

## 2. Prérequis

| Outil | Version | Vérification |
|-------|---------|-------------|
| JDK | 21+ | `java -version` |
| Gradle | 8.7+ (via wrapper) | `gradlew.bat --version` |
| Node.js | 18+ (pour le frontend) | `node --version` |
| Git | 2.x | `git --version` |

**Aucune base de données requise** en mode développement — les données sont en mémoire (mock).

**Aucune connexion Topaze requise** en mode développement — le `PersonnesMockDao` (`@Profile("dev")`) est utilisé par défaut.

---

## 3. Étape 1 - Installation et build

### 3.1 Cloner le projet

```
sgesapi/                  ← Backend Java Spring Boot
RefontDeVueJs/            ← Frontend Vue.js
```

Les deux projets sont côte à côte dans le même répertoire parent (`dev-gradle/`).

### 3.2 Build du backend

Depuis le répertoire `sgesapi/` :

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

**Ce que fait le build** :

1. Télécharge les dépendances (Maven Central)
2. Génère les classes Java depuis `openapi.json` (Swagger 2.0 — API Topaze)
3. Génère les classes Java depuis `sigac-prets.yaml` (OpenAPI 3.0.3 — API SIGAC)
4. Supprime les fichiers doublons entre les deux générateurs (`cleanDuplicateGeneratedFiles`)
5. Compile le code source
6. Exécute les 170 tests unitaires (JUnit 5)
7. Génère le rapport de couverture JaCoCo (`build/reports/jacoco/test/html/index.html`)

### 3.3 Vérifier le build

Le build doit afficher :

```
BUILD SUCCESSFUL in Xs
```

**Arborescence générée** :

```
build/
├── java/
│   ├── generatedOpentopazeservice/    ← Classes API Topaze (Swagger 2.0)
│   │   └── src/main/java/com/arkea/sgesapi/dao/...
│   └── generatedSigacPrets/           ← Classes API SIGAC (OpenAPI 3.0.3)
│       └── src/main/java/com/arkea/sgesapi/api/sigac/...
│                                       └── model/sigac/...
└── classes/
    └── ...                             ← Code compilé
```

---

## 4. Étape 2 - Configuration

### 4.1 Fichier principal : `src/main/resources/application.yml`

```yaml
server:
  port: 9088                          # Port du backend (modifié de 8080)
  servlet:
    context-path: /

spring:
  application:
    name: sgesapi
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
    # Profils disponibles : dev | val | rec | hml | prod

# Thrift Client Topaze (chargé uniquement hors profil "dev")
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

# JWT
jwt:
  secret: ${JWT_SECRET:bXlTdXBlclNlY3JldEtleUZvclNnZXNBcGlKd3RUb2tlbjIwMjQ=}
  expiration: 86400000                # 24 heures

# Swagger UI
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### 4.2 Variables d'environnement (optionnelles)

| Variable | Défaut | Description |
|----------|--------|-------------|
| `TOPAZE_THRIFT_HOST` | `192.168.1.100` | Hôte du serveur Thrift Topaze |
| `TOPAZE_THRIFT_PORT` | `9090` | Port du serveur Thrift Topaze |
| `JWT_SECRET` | (encodé en base64) | Clé secrète JWT |

**En mode développement, aucune de ces variables n'est requise.**

### 4.3 Port du serveur

Le backend écoute sur le port **9088**. Si vous devez changer :

1. Modifier `server.port` dans `application.yml`
2. Mettre à jour `VITE_API_TARGET` dans le frontend (`.env.development`)

---

## 5. Étape 3 - Lancement du backend

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

**Logs de démarrage attendus** :

```
Initialisation des données mock pour les personnes
8 personnes mock initialisées
Initialisation des données mock pour les dossiers de prêts
5 dossiers mock initialisés
Started SgesapiApplication in X.XXXs
```

### 5.1 Vérification

Une fois lancé, vérifier :

| URL | Attendu |
|-----|---------|
| `http://localhost:9088/swagger-ui.html` | Interface Swagger UI |
| `http://localhost:9088/api/v1/prets` | JSON avec 5 dossiers |
| `http://localhost:9088/api/v1/prets/2024-PAP-001547` | JSON du dossier MARTIN |
| `http://localhost:9088/actuator/health` | `{"status":"UP"}` |

### 5.2 Test rapide avec curl

```bash
# Lister tous les dossiers
curl http://localhost:9088/api/v1/prets

# Consulter un dossier spécifique
curl http://localhost:9088/api/v1/prets/2024-PAP-001547

# Recherche multicritères
curl -X POST http://localhost:9088/api/v1/recherche/dossiers \
  -H "Content-Type: application/json" \
  -d '{"nomEmprunteur":"MARTIN","page":0,"taille":20}'

# Recherche rapide
curl "http://localhost:9088/api/v1/recherche/dossiers?q=MARTIN&page=0&taille=20"

# Authentification JWT
curl -X POST http://localhost:9088/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

---

## 6. Étape 4 - Connexion du frontend

### 6.1 Configuration du frontend

Dans `RefontDeVueJs/.env.development` :

```env
VITE_API_MODE=http
VITE_API_BASE_URL=/api/v1
VITE_API_TARGET=http://localhost:9088
```

- `VITE_API_MODE=http` : active le service HTTP (au lieu du mock local)
- `VITE_API_TARGET=http://localhost:9088` : le proxy Vite redirige `/api` vers ce backend

### 6.2 Proxy Vite

Le fichier `vite.config.ts` configure un proxy automatique :

```typescript
proxy: {
  '/api': {
    target: env.VITE_API_TARGET || 'http://localhost:9088',
    changeOrigin: true,
    secure: false,
  },
},
```

Le frontend (port 3000) appelle `/api/v1/prets` qui est redirigé vers `http://localhost:9088/api/v1/prets`. Pas de problème CORS.

### 6.3 Lancement combiné

**Terminal 1** — Backend :
```bash
cd sgesapi
gradlew.bat bootRun
```

**Terminal 2** — Frontend :
```bash
cd RefontDeVueJs
npm run dev
```

**Flux** :
```
Navigateur (:3000)  →  Vite proxy (/api/*)  →  Spring Boot (:9088)
```

### 6.4 Flux utilisateur complet

1. Ouvrir `http://localhost:3000` → écran de **Recherche** (home)
2. Le frontend appelle `GET /api/v1/prets` → affiche la liste des 5 dossiers mock
3. Cliquer sur un dossier → navigation vers `/consultation/{id}/donnees-generales`
4. Le frontend appelle `GET /api/v1/prets/{id}` → affiche les données générales, prêt et dates

---

## 7. Architecture technique

### 7.1 Structure du projet

```
sgesapi/
├── build.gradle                       ← Config build + OpenAPI generators
├── settings.gradle
├── openapi/
│   └── sigac-prets.yaml               ← Spec OpenAPI 3.0.3 (API SIGAC)
├── src/
│   ├── main/
│   │   ├── java/com/arkea/sgesapi/
│   │   │   ├── SgesapiApplication.java         ← Point d'entrée
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java          ← Spring Security + JWT
│   │   │   │   ├── UserConfig.java              ← UserDetailsService + PasswordEncoder
│   │   │   │   └── OpenApiConfig.java           ← Config Swagger UI
│   │   │   ├── security/
│   │   │   │   ├── AuthController.java          ← POST /api/v1/auth/login
│   │   │   │   ├── JwtService.java              ← Génération/validation JWT
│   │   │   │   └── JwtAuthFilter.java           ← Filtre HTTP Bearer token
│   │   │   ├── controller/
│   │   │   │   ├── RechercheController.java     ← Écran recherche (home)
│   │   │   │   └── ConsultationController.java  ← Écran consultation
│   │   │   ├── delegate/
│   │   │   │   └── PretsApiDelegateImpl.java    ← Impl. OpenAPI delegate SIGAC
│   │   │   ├── service/
│   │   │   │   ├── DossierService.java          ← Logique métier dossiers
│   │   │   │   └── PersonnesService.java        ← Résolution noms personnes
│   │   │   ├── dao/
│   │   │   │   ├── api/
│   │   │   │   │   ├── IDossierDao.java         ← Interface DAO dossiers
│   │   │   │   │   └── IPersonnesDao.java       ← Interface DAO personnes
│   │   │   │   ├── impl/
│   │   │   │   │   ├── DossierMockDao.java      ← Mock 5 dossiers
│   │   │   │   │   ├── PersonnesMockDao.java    ← Mock 8 personnes (@Profile "dev")
│   │   │   │   │   └── PersonnesThriftDao.java  ← DAO Thrift Catalyst (@Profile "!dev")
│   │   │   │   └── model/
│   │   │   │       ├── DossierConsultationDto.java
│   │   │   │       ├── DossierResumeDto.java
│   │   │   │       ├── PersonneMinimaleDto.java
│   │   │   │       └── RechercheCriteria.java
│   │   │   ├── thrift/
│   │   │   │   ├── AbstractThriftDAO.java            ← execute() + multi-catch (Catalyst)
│   │   │   │   ├── spring/
│   │   │   │   │   └── AbstractCatalystThriftDAO.java ← Pool getClient/finalizeClient
│   │   │   │   ├── callback/
│   │   │   │   │   └── ThriftDaoCallbackIface.java   ← @FunctionalInterface
│   │   │   │   ├── pool/
│   │   │   │   │   ├── TServiceClientPool.java       ← Commons Pool2 wrapper
│   │   │   │   │   └── ThriftClientFactory.java      ← Factory clients Thrift
│   │   │   │   └── data/
│   │   │   │       ├── ResponseContext.java           ← Analyse réponses Thrift
│   │   │   │       └── ResponseType.java              ← Enum SUCCESS/ERROR/WARNING/INFO
│   │   │   ├── config/
│   │   │   │   └── ThriftPoolConfig.java         ← Bean pool Thrift (@Profile "!dev")
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── DossierNotFoundException.java
│   │   │       └── DAOException.java             ← Exception checked (extends Exception)
│   │   ├── resources/
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml               ← Profil dev (mock, pas de Thrift)
│   │   │   ├── application-val.yml               ← Profil validation
│   │   │   ├── application-rec.yml               ← Profil recette
│   │   │   ├── application-hml.yml               ← Profil homologation
│   │   │   ├── application-prod.yml              ← Profil production
│   │   │   └── specs/
│   │   │       └── openapi.json        ← Spec Swagger 2.0 (API Topaze)
│   │   └── thrift/
│   │       └── topaze-personnes.thrift  ← Définition service Thrift
│   └── test/
│       └── java/com/arkea/sgesapi/
│           ├── service/
│           │   ├── PersonnesServiceTest.java      ← 16 tests
│           │   └── DossierServiceTest.java        ← 13 tests
│           ├── delegate/
│           │   └── PretsApiDelegateImplTest.java   ← 11 tests
│           ├── controller/
│           │   ├── RechercheControllerTest.java    ← 5 tests (MockMvc standalone)
│           │   └── ConsultationControllerTest.java ← 3 tests (MockMvc standalone)
│           ├── security/
│           │   ├── JwtServiceTest.java             ← 6 tests
│           │   ├── JwtAuthFilterTest.java          ← 5 tests
│           │   └── AuthControllerTest.java         ← 2 tests
│           ├── dao/
│           │   ├── impl/
│           │   │   ├── DossierMockDaoTest.java     ← 21 tests
│           │   │   ├── PersonnesMockDaoTest.java   ← 8 tests
│           │   │   └── PersonnesThriftDaoTest.java  ← 2 tests
│           │   └── model/
│           │       ├── DossierConsultationDtoTest.java ← 6 tests
│           │       ├── DossierResumeDtoTest.java       ← 4 tests
│           │       ├── PersonneMinimaleDtoTest.java    ← 7 tests
│           │       └── RechercheCriteriaTest.java      ← 5 tests
│           ├── thrift/
│           │   ├── AbstractThriftDAOTest.java          ← 11 tests
│           │   ├── spring/
│           │   │   └── AbstractCatalystThriftDAOTest.java ← 6 tests
│           │   ├── pool/
│           │   │   ├── TServiceClientPoolTest.java     ← 7 tests
│           │   │   └── ThriftClientFactoryTest.java    ← 10 tests
│           │   └── data/
│           │       └── ResponseContextTest.java        ← 11 tests
│           └── exception/
│               ├── GlobalExceptionHandlerTest.java ← 5 tests
│               ├── DAOExceptionTest.java           ← 4 tests
│               └── DossierNotFoundExceptionTest.java ← 2 tests
```

### 7.2 Architecture en couches

```
┌─────────────────────────────────────────────────────────┐
│  Controllers / Delegates                                 │
│  (RechercheController, ConsultationController,           │
│   PretsApiDelegateImpl)                                  │
├─────────────────────────────────────────────────────────┤
│  Services                                                │
│  (DossierService, PersonnesService)                      │
│  → Résolution emprunteur/coEmprunteur via PersonnesService│
├─────────────────────────────────────────────────────────┤
│  DAO (Interfaces)                                        │
│  (IDossierDao, IPersonnesDao)                            │
├───────────────────────┬─────────────────────────────────┤
│  Mock  @Profile("dev")│  Thrift  @Profile("!dev")        │
│  DossierMockDao       │  PersonnesThriftDao              │
│  PersonnesMockDao     │    → AbstractThriftDAO (Catalyst)│
│                       │    → TServiceClientPool → Topaze │
└───────────────────────┴─────────────────────────────────┘
```

### 7.3 Approche API-First (OpenAPI Generator)

Le projet utilise deux specs OpenAPI pour générer automatiquement les interfaces et modèles :

| Spec | Format | Package généré | Usage |
|------|--------|---------------|-------|
| `openapi.json` | Swagger 2.0 | `dao.api.opentopazeservice` / `dao.model.opentopazeservice` | API Topaze interne |
| `sigac-prets.yaml` | OpenAPI 3.0.3 | `api.sigac` / `model.sigac` | API SIGAC (consommée par le frontend) |

Le pattern **delegate** est utilisé : le générateur crée l'interface `PretsApiDelegate`, et `PretsApiDelegateImpl` en fournit l'implémentation.

---

## 8. Catalogue des API REST

### 8.1 API SIGAC (frontend Vue.js)

Ces endpoints sont générés depuis `sigac-prets.yaml` et implémentés par `PretsApiDelegateImpl`.

#### `GET /api/v1/prets` — Lister les dossiers

**Réponse** (`ServiceResponseDossierResumeList`) :

```json
{
  "data": [
    {
      "id": "2024-PAP-001547",
      "noPret": "2024-PAP-001547",
      "emprunteur": "MARTIN Jean-Pierre",
      "montantPret": "250 000,00 €",
      "codeEtat": "40 - En gestion"
    }
  ],
  "success": true,
  "message": "OK"
}
```

#### `GET /api/v1/prets/{id}` — Consulter un dossier

**Paramètre** : `id` = identifiant du dossier (ex: `2024-PAP-001547`)

**Réponse** (`ServiceResponseDossierPret`) :

```json
{
  "data": {
    "id": "2024-PAP-001547",
    "donneesGenerales": {
      "emprunteur": "MARTIN Jean-Pierre",
      "coEmprunteur": "MARTIN Catherine",
      "noPret": "2024-PAP-001547",
      "noContratSouscritProjet": "PRJ-2024-08-1547",
      "noContratSouscritPret": "PRT-2024-08-1547",
      "efs": "13807",
      "structure": "CIF Île-de-France",
      "codeEtat": "40 - En gestion",
      "codeObjet": "01 - Acquisition ancien",
      "codeNature": "PAP - Prêt à l'Accession à la Propriété"
    },
    "donneesPret": {
      "montantPret": "250 000,00 €",
      "dureePret": "240 mois",
      "tauxRemboursement": "3,45 %",
      "tauxFranchise": "0,00 %",
      "tauxBonification": "0,00 %",
      "anticipation": "Non",
      "typeAmortissement": "Échéances constantes",
      "outilInstruction": "GIPSI",
      "montantDebloque": "250 000,00 €",
      "montantDisponible": "0,00 €",
      "montantRA": "0,00 €",
      "encours": "237 845,12 €",
      "teg": "3,72 %"
    },
    "dates": {
      "dateAcceptation": "",
      "dateAccord": "",
      "dateOuvertureCredit": "",
      "datePassageGestion": "",
      "dateEffet": "",
      "date1ereEcheance": "",
      "dateEffetRA": "",
      "dateEffetFP": "",
      "dateFinPret": "",
      "date1ereEcheance2": "",
      "datePrecedenteEcheance": "",
      "dateProchaineEcheance": "",
      "dateAbonnementPrecedent": "",
      "dateAbonnementSuivant": "",
      "dateTombeePrecedente": "",
      "dateTombeeSuivante": ""
    }
  },
  "success": true,
  "message": "OK"
}
```

### 8.2 API Recherche (écran home)

#### `POST /api/v1/recherche/dossiers` — Recherche multicritères

**Corps de la requête** :

```json
{
  "nomEmprunteur": "MARTIN",
  "prenomEmprunteur": null,
  "numeroPret": null,
  "efs": null,
  "structure": null,
  "codeEtat": "40",
  "codeNature": null,
  "page": 0,
  "taille": 20
}
```

**Réponse** :

```json
{
  "dossiers": [
    {
      "numeroPret": "2024-PAP-001547",
      "noEmprunteur": "PP-001547-E",
      "noCoEmprunteur": "PP-001547-C",
      "emprunteur": "MARTIN Jean-Pierre",
      "coEmprunteur": "MARTIN Catherine",
      "efs": "13807",
      "structure": "CIF Île-de-France",
      "codeEtat": "40",
      "libelleEtat": "En gestion",
      "codeNature": "PAP",
      "libelleNature": "Prêt à l'Accession à la Propriété",
      "montantPret": 250000.00,
      "tauxRemboursement": 3.45
    }
  ],
  "totalElements": 1,
  "page": 0,
  "taille": 20
}
```

Tous les critères sont optionnels. Si aucun critère n'est fourni, tous les dossiers sont retournés.

#### `GET /api/v1/recherche/dossiers?q=MARTIN&page=0&taille=20` — Recherche rapide

Recherche simplifiée par un terme unique (nom ou numéro de prêt). Même format de réponse que la recherche multicritères.

### 8.3 API Consultation

#### `GET /api/v1/dossiers/{numeroPret}` — Consultation détaillée

**Réponse** :

```json
{
  "donneesGenerales": {
    "emprunteur": "MARTIN Jean-Pierre",
    "coEmprunteur": "MARTIN Catherine",
    "numeroPret": "2024-PAP-001547",
    "numeroContratSouscritProjet": "PRJ-2024-08-1547",
    "numeroContratSouscritPret": "PRT-2024-08-1547",
    "efs": "13807",
    "structure": "CIF Île-de-France",
    "codeEtat": "40 - En gestion",
    "codeObjet": "01 - Acquisition ancien",
    "codeNature": "PAP - Prêt à l'Accession à la Propriété"
  },
  "donneesPret": {
    "montantPret": 250000.00,
    "dureePret": 240,
    "tauxRemboursement": 3.45,
    "tauxFranchise": 0.00,
    "tauxBonification": 0.00,
    "anticipation": false,
    "typeAmortissement": "Échéances constantes",
    "outilInstruction": "GIPSI",
    "montantDebloque": 250000.00,
    "montantDisponible": 0.00,
    "montantRA": 0.00,
    "encours": 237845.12,
    "teg": 3.72
  }
}
```

### 8.4 API Authentification

#### `POST /api/v1/auth/login` — Obtenir un token JWT

**Corps de la requête** :

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Réponse** :

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

**Utilisateurs disponibles (dev)** :

| Username | Password | Rôles |
|----------|----------|-------|
| `admin` | `admin` | ADMIN, USER |
| `user` | `user` | USER |

---

## 9. Sécurité JWT

### 9.1 Architecture

```
Client  →  JwtAuthFilter  →  SecurityFilterChain  →  Controller
                ↓
         JwtService.validateToken()
                ↓
         UserDetailsService.loadUser()
```

### 9.2 Configuration actuelle (mode dev)

**En développement, tous les endpoints `/api/v1/**` sont en `permitAll()`** — pas de token JWT requis. C'est configuré dans `SecurityConfig.java` :

```java
.requestMatchers("/api/v1/**").permitAll()
```

Pour activer la sécurité JWT, commenter cette ligne et utiliser :

```java
.requestMatchers("/api/v1/**").authenticated()
```

### 9.3 Utilisation du token JWT

```bash
# 1. Obtenir un token
TOKEN=$(curl -s -X POST http://localhost:9088/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.token')

# 2. Appeler une API avec le token
curl http://localhost:9088/api/v1/prets \
  -H "Authorization: Bearer $TOKEN"
```

### 9.4 CORS

Origines autorisées :
- `http://localhost:5173` (Vite par défaut)
- `http://localhost:3000` (Vite configuré)
- `http://localhost:8080`

Configurable dans `SecurityConfig.corsConfigurationSource()`.

---

## 10. Couche DAO - Accès aux données

### 10.1 Pattern Interface/Implémentation

Le service métier ne connaît que les interfaces DAO. L'injection Spring choisit l'implémentation :

```
DossierService  →  IDossierDao (interface)
                      ├── DossierMockDao    (@Repository, seul impl pour l'instant)
                      └── [DossierThriftDao] (futur, @Profile "!dev")

PersonnesService →  IPersonnesDao (interface)
                      ├── PersonnesMockDao   (@Repository @Profile "dev")
                      └── PersonnesThriftDao (@Repository @Profile "!dev" → val/rec/hml/prod)
```

### 10.2 IDossierDao

```java
public interface IDossierDao {
    List<DossierResumeDto> rechercherDossiers(RechercheCriteria criteria);
    long compterDossiers(RechercheCriteria criteria);
    Optional<DossierConsultationDto> consulterDossier(String numeroPret);
}
```

### 10.3 IPersonnesDao

```java
public interface IPersonnesDao {
    Map<String, PersonneMinimaleDto> getInformationsMinimalesPersonnes(
        List<String> identifiantsPersonnes);
}
```

### 10.4 DTOs

**DossierConsultationDto** — Dossier complet (26+ champs) :
- `noEmprunteur`, `noCoEmprunteur` : identifiants Topaze (résolus par PersonnesService)
- `emprunteur`, `coEmprunteur` : noms résolus ("NOM Prénom")
- `numeroPret`, `numeroContratSouscritProjet`, `numeroContratSouscritPret`
- `efs`, `structure`, `codeEtat`, `libelleEtat`
- `codeObjet`, `libelleObjet`, `codeNature`, `libelleNature`
- `montantPret`, `dureePret`, `tauxRemboursement`, `tauxFranchise`, `tauxBonification`
- `anticipation`, `typeAmortissement`, `outilInstruction`
- `montantDebloque`, `montantDisponible`, `montantRA`, `encours`, `teg`

**DossierResumeDto** — Résumé pour la liste de recherche :
- `numeroPret`, `noEmprunteur`, `noCoEmprunteur`, `emprunteur`, `coEmprunteur`
- `efs`, `structure`, `codeEtat`, `libelleEtat`
- `codeNature`, `libelleNature`, `montantPret`, `tauxRemboursement`

**PersonneMinimaleDto** — Infos minimales d'une personne :
- `identifiant`, `nom`, `prenom`, `typePersonne` (PP/PM)
- `getLibelleComplet()` → "NOM Prénom"

**RechercheCriteria** — Critères de recherche avec Builder :
- `nomEmprunteur`, `prenomEmprunteur`, `numeroPret`, `efs`
- `structure`, `codeEtat`, `codeNature`, `page`, `taille`

---

## 11. Résolution des personnes (Topaze)

### 11.1 Concept

Les dossiers stockent des **identifiants personnes Topaze** (`noEmprunteur`, `noCoEmprunteur`) et non des noms. La résolution se fait via `getInformationsMinimalesPersonnes()` :

```
DossierService.consulterDossier("2024-PAP-001547")
    │
    ├── IDossierDao → retourne dossier avec noEmprunteur="PP-001547-E"
    │
    └── PersonnesService.resoudreEmprunteurCoEmprunteur("PP-001547-E", "PP-001547-C")
            │
            └── IPersonnesDao.getInformationsMinimalesPersonnes(["PP-001547-E", "PP-001547-C"])
                    │
                    └── retourne Map { "PP-001547-E" → "MARTIN Jean-Pierre",
                                       "PP-001547-C" → "MARTIN Catherine" }
```

### 11.2 En mode mock

`PersonnesMockDao` (`@Profile("dev")`) fournit 8 personnes en mémoire. Correspondance identifiant → nom :

| Identifiant | Nom complet |
|-------------|-------------|
| `PP-001547-E` | MARTIN Jean-Pierre |
| `PP-001547-C` | MARTIN Catherine |
| `PP-002891-E` | DUPONT Marie |
| `PP-002891-C` | DUPONT François |
| `PP-000412-E` | LECLERC Sophie |
| `PP-003102-E` | BERNARD Alain |
| `PP-003102-C` | BERNARD Nathalie |
| `PP-001890-E` | NGUYEN Van Thi |

### 11.3 En production (Thrift Topaze)

`PersonnesThriftDao` appellera le service Thrift `DonneesGeneriquesTopaze.getInformationsMinimalesPersonnesTopaze()` défini dans `topaze-personnes.thrift` :

```thrift
service DonneesGeneriquesTopaze {
    GetInformationsMinimalesPersonnesTopazeResponse
        getInformationsMinimalesPersonnesTopaze(
            1: GetInformationsMinimalesPersonnesTopazeRequest request)
        throws (1: TopazeServiceException serviceEx,
                2: TopazeMetierException metierEx)
}
```

Activation automatique via profil Spring : `PersonnesThriftDao` est annoté `@Profile("!dev")` et se charge sur val, rec, hml, prod.

---

## 12. Gestion des erreurs

### 12.1 GlobalExceptionHandler

Toutes les erreurs sont capturées par `@RestControllerAdvice` et retournées en JSON structuré :

| Exception | Code HTTP | Message |
|-----------|-----------|---------|
| `DossierNotFoundException` | 404 | `Dossier 'XXX' non trouvé` |
| `DAOException` | 500 | `Erreur d'accès au système Topaze` |
| `IllegalArgumentException` | 400 | Message de l'exception |
| `Exception` (autre) | 500 | `Erreur technique inattendue` |

### 12.2 Format de réponse d'erreur

```json
{
  "timestamp": "2026-03-12T10:30:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Dossier '9999' non trouvé"
}
```

### 12.3 Résilience PersonnesService

Si la résolution des noms échoue (Topaze indisponible), le dossier est quand même retourné avec les champs `emprunteur`/`coEmprunteur` à `null`. Un warning est loggé mais l'erreur ne bloque pas la consultation.

---

## 13. Données mock de démonstration

### 13.1 Les 5 dossiers disponibles

| N° Prêt | Emprunteur | Nature | État | Montant |
|---------|-----------|--------|------|---------|
| `2024-PAP-001547` | MARTIN Jean-Pierre + Catherine | PAP | 40 - En gestion | 250 000 |
| `2024-PAS-002891` | DUPONT Marie + François | PAS | 30 - En déblocage | 180 000 |
| `2023-PAP-000412` | LECLERC Sophie | PAP | 40 - En gestion | 75 000 |
| `2024-PTZ-003102` | BERNARD Alain + Nathalie | PTZ | 20 - En instruction | 40 000 |
| `2023-PEL-001890` | NGUYEN Van Thi | PEL | 40 - En gestion | 92 000 |

### 13.2 Couverture des cas

- **Avec co-emprunteur** : MARTIN, DUPONT, BERNARD
- **Sans co-emprunteur** : LECLERC, NGUYEN
- **PTZ (taux 0%, différé partiel)** : BERNARD
- **PEL (bonification, anticipation)** : NGUYEN
- **En instruction** : BERNARD
- **En déblocage** : DUPONT
- **En gestion** : MARTIN, LECLERC, NGUYEN

---

## 14. Environnements et profils Spring

### 14.1 Les 5 profils

Le projet utilise des profils Spring pour basculer entre Mock (dev) et Thrift réel (val/rec/hml/prod) :

| Profil | DAO Personnes | ThriftPoolConfig | Host Topaze | Fichier config |
|--------|---------------|------------------|-------------|----------------|
| **dev** | `PersonnesMockDao` | Non chargé | — | `application-dev.yml` |
| **val** | `PersonnesThriftDao` | Chargé | `topaze-val.arkea.local` | `application-val.yml` |
| **rec** | `PersonnesThriftDao` | Chargé | `topaze-rec.arkea.local` | `application-rec.yml` |
| **hml** | `PersonnesThriftDao` | Chargé | `topaze-hml.arkea.local` | `application-hml.yml` |
| **prod** | `PersonnesThriftDao` | Chargé | `topaze.arkea.local` | `application-prod.yml` |

### 14.2 Mécanisme d'activation

Les beans sont conditionnés par `@Profile` :

- `PersonnesMockDao` → `@Profile("dev")` — chargé uniquement en dev
- `PersonnesThriftDao` → `@Profile("!dev")` — chargé sur tout sauf dev
- `ThriftPoolConfig` → `@Profile("!dev")` — pas de pool Thrift instancié en dev

Le profil par défaut est `dev` (défini dans `application.yml` : `spring.profiles.active: ${SPRING_PROFILES_ACTIVE:dev}`).

### 14.3 Lancement par environnement

```bash
# Développement local (mock — défaut, pas besoin de Topaze)
./gradlew bootRun

# Validation
SPRING_PROFILES_ACTIVE=val ./gradlew bootRun

# Recette
SPRING_PROFILES_ACTIVE=rec java -jar sgesapi.jar

# Homologation
java -jar sgesapi.jar -Dspring.profiles.active=hml

# Production
SPRING_PROFILES_ACTIVE=prod java -jar sgesapi.jar
```

### 14.4 Checklist mise en production

1. **Générer le code Thrift** : `gradlew generateThrift` (nécessite le binaire `thrift`)
2. **Remplacer `TServiceClient`** par `DonneesGeneriquesTopaze.Client` dans `PersonnesThriftDao` et `ThriftPoolConfig`
3. **Créer DossierThriftDao** : implémenter `IDossierDao` avec `extends AbstractThriftDAO` et `@Profile("!dev")`
4. **Thrift Topaze** : vérifier les hosts dans chaque `application-{profil}.yml`
5. **Sécurité JWT** : commenter `.requestMatchers("/api/v1/**").permitAll()` dans `SecurityConfig`
6. **Clé JWT** : définir une clé secrète forte via `JWT_SECRET`
7. **CORS** : restreindre les origines autorisées aux domaines de chaque environnement
8. **Dates du prêt** : brancher les `DatesPret` sur Topaze (actuellement vides dans `PretsApiDelegateImpl`)

---

## 15. Tests unitaires et couverture

### 15.1 Stack de test

| Outil | Version | Rôle |
|-------|---------|------|
| JUnit 5 | 5.10.x (BOM Spring Boot) | Framework de tests |
| Mockito | 5.x | Mocks et stubs |
| MockMvc | Spring Test | Tests contrôleurs HTTP (mode standalone) |
| JaCoCo | 0.8.12 | Mesure de couverture de code |

### 15.2 Lancer les tests

```bash
# Tests seuls
gradlew.bat test

# Tests + rapport de couverture JaCoCo
gradlew.bat clean build jacocoTestReport
```

Le rapport HTML est généré dans `build/reports/jacoco/test/html/index.html`.

### 15.3 Résultats actuels

- **170 tests** répartis dans **23 fichiers de tests**
- **98% de couverture d'instructions** (69 instructions manquées sur 3 742)
- **79% de couverture de branches**
- Toutes les couches couvertes : services, contrôleurs, delegates, DAOs, Thrift, sécurité, DTOs, exceptions

### 15.4 Organisation des tests par couche

| Couche | Fichiers de test | Nb tests | Technique |
|--------|-----------------|----------|-----------|
| Services | PersonnesServiceTest, DossierServiceTest | 29 | Mockito (@ExtendWith) |
| Delegates | PretsApiDelegateImplTest | 11 | Mockito |
| Contrôleurs | RechercheControllerTest, ConsultationControllerTest | 8 | MockMvc standalone |
| Sécurité | JwtServiceTest, JwtAuthFilterTest, AuthControllerTest | 13 | Mockito + MockMvc |
| DAO Mock | DossierMockDaoTest, PersonnesMockDaoTest | 29 | Instanciation directe |
| DAO Thrift | PersonnesThriftDaoTest | 2 | Instanciation directe |
| Thrift infra | AbstractThriftDAOTest, AbstractCatalystThriftDAOTest | 17 | Mockito (TestThriftDAO) |
| Pool Thrift | TServiceClientPoolTest, ThriftClientFactoryTest | 17 | Mockito + TestableFactory |
| Thrift data | ResponseContextTest | 11 | Instanciation directe |
| DTOs | *DtoTest (4 fichiers) | 22 | Instanciation directe |
| Exceptions | DAOExceptionTest, DossierNotFoundExceptionTest, GlobalExceptionHandlerTest | 11 | Instanciation directe |

### 15.5 Exclusions JaCoCo

Les classes suivantes sont exclues de la couverture car elles sont générées ou purement déclaratives :

- `com/arkea/sgesapi/model/**` — modèles OpenAPI générés (SIGAC)
- `com/arkea/sgesapi/api/**` — interfaces API générées (SIGAC)
- `com/arkea/sgesapi/dao/api/opentopazeservice/**` — modèles générés Topaze
- `com/arkea/sgesapi/dao/model/opentopazeservice/**` — modèles générés Topaze
- `org/openapitools/**` — utilitaires OpenAPI Generator
- `com/arkea/sgesapi/config/**` — classes de configuration Spring (SecurityConfig, ThriftPoolConfig, UserConfig, OpenApiConfig)
- `com/arkea/sgesapi/SgesapiApplication*` — point d'entrée Spring Boot

### 15.6 Approche technique des tests contrôleurs

Les tests de `RechercheController` et `ConsultationController` utilisent **MockMvc en mode standalone** plutôt que `@WebMvcTest`, afin d'éviter le chargement du contexte Spring Security complet (SecurityConfig → JwtAuthFilter → JwtService → propriété `jwt.secret`) :

```java
@BeforeEach
void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
}
```

Cette approche teste le mapping HTTP, la sérialisation JSON et la gestion d'erreurs sans charger l'intégralité du contexte Spring.

### 15.7 Test de la couche Thrift (AbstractThriftDAO)

Le test de `AbstractThriftDAO.execute()` couvre tous les chemins du pattern execute/callback :

- Succès avec retour du client au pool
- `TTransportException` → invalidation du client + `DAOException`
- `TException` → pas d'invalidation + `DAOException`
- `DAOException` → relancée directement
- `RuntimeException` → invalidation + `DAOException`
- `borrowObject` échoue → `DAOException`
- ResponseContext avec erreur métier → `DAOException` avec message Topaze
- ResponseContext sans erreur → retour normal
- `finalizeClient` échoue → `DAOException`

Un POJO `ResponseWithContext` est utilisé dans les tests pour simuler une réponse Thrift contenant un `responseContext` accessible via `PropertyUtils`.

---

## 16. Troubleshooting

### Erreur : `jakarta.validation-api:3.0.3 not found`

La version explicite n'existe pas. Le BOM Spring Boot gère la version automatiquement. Ne pas spécifier de version dans `build.gradle` :

```groovy
implementation 'jakarta.validation:jakarta.validation-api'  // sans version
```

### Erreur : `gradlew.bat n'est pas reconnu`

Le wrapper Gradle n'est pas présent. Générer :

```bash
gradle wrapper --gradle-version 8.7
```

### Erreur : 139 erreurs de compilation (Lombok)

Lombok a été retiré du projet. Tout le code utilise du Java pur (constructeurs manuels, getters/setters, Builder classes, champs `Logger` explicites).

### Erreur : `TFramedTransport` not found

`TFramedTransport` a été supprimé dans Thrift 0.20.0. Le code utilise `TSocket` directement :

```java
transport = new TSocket(host, port, timeout);
```

### Erreur : Classes dupliquées entre OpenAPI generators

Les deux taches OpenAPI Generator produisent des classes utilitaires identiques (`HomeController`, `SpringDocConfiguration`, etc.). La tache `cleanDuplicateGeneratedFiles` supprime le dossier `org/openapitools` des deux répertoires générés après la génération.

### Erreur : Circular dependency (JwtAuthFilter / SecurityConfig)

Résolu par la séparation de `UserDetailsService` et `PasswordEncoder` dans `UserConfig.java` (classe `@Configuration` séparée de `SecurityConfig`).

### Erreur : Two main classes found

`springBoot { mainClass = 'com.arkea.sgesapi.SgesapiApplication' }` est déclaré explicitement dans `build.gradle` pour éviter le conflit avec `OpenApiGeneratorApplication` généré.

### Le frontend affiche "Erreur réseau"

1. Vérifier que le backend est lancé (`gradlew.bat bootRun`)
2. Vérifier le port : `http://localhost:9088/actuator/health`
3. Vérifier `.env.development` : `VITE_API_MODE=http` et `VITE_API_TARGET=http://localhost:9088`
4. Relancer le frontend (`npm run dev`) — les changements `.env` nécessitent un redémarrage Vite
