# Documentation - Configuration du projet RefontDeVueJs

Guide pas a pas de la configuration du projet Vue 3 + TypeScript + Vite.

---

## Table des matieres

1. [Initialisation du projet](#1-initialisation-du-projet)
2. [Configuration Vite](#2-configuration-vite---viteconfigts)
3. [Configuration TypeScript](#3-configuration-typescript)
4. [Configuration SCSS](#4-configuration-scss)
5. [Auto-import des composants PrimeVue](#5-auto-import-des-composants-primevue)
6. [Configuration ESLint](#6-configuration-eslint---eslintrccjs)
7. [Configuration des tests unitaires (Vitest)](#7-configuration-des-tests-unitaires-vitest---vitestconfigts)
8. [Configuration des tests E2E (Cypress)](#8-configuration-des-tests-e2e-cypress---cypressconfigts)
9. [Point d'entree de l'application](#9-point-dentree-de-lapplication---srcmaints)
10. [Scripts disponibles](#10-scripts-disponibles)
11. [Structure du projet](#11-structure-du-projet)

---

## 1. Initialisation du projet

### 1.1 - Creer le fichier `package.json`

Le fichier `package.json` definit le nom du projet, ses scripts et ses dependances.

```json
{
  "name": "RefontDeVueJs",
  "version": "1.0.0",
  "private": true,
  "type": "module"
}
```

| Propriete | Role |
|-----------|------|
| `name`    | Nom du projet |
| `version` | Version semantique du projet |
| `private` | Empeche la publication accidentelle sur npm |
| `type`    | `"module"` active les imports ES (`import/export`) dans Node.js |

### 1.2 - Installer les dependances de production

```bash
npm install vue vue-router pinia primevue primeicons primeflex
```

| Package      | Role |
|-------------|------|
| `vue`        | Framework frontend reactif (v3) |
| `vue-router` | Gestion de la navigation par URL (SPA) |
| `pinia`      | Store global pour la gestion d'etat (remplace Vuex) |
| `primevue`   | Bibliotheque de composants UI (InputText, Button, Calendar, etc.) |
| `primeicons` | Icones utilisees par PrimeVue |
| `primeflex`  | Classes CSS utilitaires (flexbox, spacing, grid) |

### 1.3 - Installer les dependances de developpement

```bash
npm install -D vite @vitejs/plugin-vue typescript vue-tsc @types/node
npm install -D sass-embedded unplugin-vue-components
npm install -D vitest @vue/test-utils jsdom
npm install -D cypress
npm install -D eslint eslint-plugin-vue @typescript-eslint/parser @typescript-eslint/eslint-plugin @vue/eslint-config-typescript
```

| Package | Role |
|---------|------|
| `vite` | Bundler et serveur de dev ultra-rapide |
| `@vitejs/plugin-vue` | Plugin Vite pour compiler les fichiers `.vue` |
| `typescript` | Compilateur TypeScript |
| `vue-tsc` | Verification de types TypeScript dans les fichiers `.vue` |
| `@types/node` | Types TypeScript pour les API Node.js |
| `sass-embedded` | Compilateur SCSS avec l'API moderne (remplace `sass`) |
| `unplugin-vue-components` | Auto-import des composants (PrimeVue, etc.) sans declaration manuelle |
| `vitest` | Framework de tests unitaires compatible Vite |
| `@vue/test-utils` | Utilitaires pour monter et tester des composants Vue |
| `jsdom` | Simule un DOM navigateur dans Node.js (pour les tests) |
| `cypress` | Framework de tests end-to-end dans un vrai navigateur |
| `eslint` | Linter JavaScript/TypeScript |
| `eslint-plugin-vue` | Regles ESLint specifiques a Vue (templates, directives) |
| `@typescript-eslint/parser` | Permet a ESLint de comprendre la syntaxe TypeScript |
| `@typescript-eslint/eslint-plugin` | Regles ESLint specifiques a TypeScript |
| `@vue/eslint-config-typescript` | Configuration ESLint pre-faite pour Vue + TypeScript |

### 1.4 - Initialiser Git

```bash
git init
```

Le fichier `.gitignore` exclut les fichiers generes et sensibles :

```
node_modules/          # Dependances (reinstallables via npm install)
dist/                  # Build de production (regenerable via npm run build)
coverage/              # Rapports de couverture de tests
cypress/videos/        # Videos enregistrees par Cypress
cypress/screenshots/   # Screenshots de Cypress
*.tsbuildinfo          # Cache TypeScript
components.d.ts        # Fichier auto-genere par unplugin-vue-components
.env / .env.local      # Variables d'environnement (secrets)
```

---

## 2. Configuration Vite - `vite.config.ts`

Vite est le bundler et serveur de developpement du projet.

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { PrimeVueResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [
    vue(),                          // 1. Compile les fichiers .vue
    Components({                    // 2. Auto-import des composants PrimeVue
      resolvers: [PrimeVueResolver()],
    }),
  ],

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),  // 3. Alias @ -> src/
    },
  },

  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler',     // 4. API Sass moderne (evite les warnings deprecated)
        additionalData: `@use "@/assets/variables.scss" as *;`,  // 5. Variables SCSS globales
      },
    },
  },

  server: {
    port: 3000,                     // 6. Port du serveur de dev
    open: true,                     // 7. Ouvre le navigateur automatiquement
  },
})
```

### Detail de chaque section

| # | Section | Explication |
|---|---------|-------------|
| 1 | `vue()` | Plugin qui transforme les fichiers `.vue` (template + script + style) en JavaScript |
| 2 | `Components({ resolvers })` | Detecte les composants PrimeVue dans les templates et les importe automatiquement. Genere `components.d.ts` pour le support TypeScript |
| 3 | `alias @` | Permet d'ecrire `import x from '@/stores/pretStore'` au lieu de `../../stores/pretStore` |
| 4 | `api: 'modern-compiler'` | Utilise la nouvelle API Sass (evite le warning `legacy-js-api deprecated`). Necessite `sass-embedded` au lieu de `sass` |
| 5 | `additionalData` | Injecte `@use variables.scss` dans chaque fichier SCSS. Les variables (`$primary`, `$gray-500`, etc.) sont disponibles partout sans import |
| 6 | `port: 3000` | L'application tourne sur `http://localhost:3000` |
| 7 | `open: true` | Lance le navigateur automatiquement a `npm run dev` |

---

## 3. Configuration TypeScript

Le projet utilise deux fichiers `tsconfig` :

### 3.1 - `tsconfig.json` (code source + tests)

Configuration principale pour tout le code dans `src/` et `tests/`.

```jsonc
{
  "compilerOptions": {
    // --- Cible et modules ---
    "target": "ES2020",                    // Version JavaScript en sortie
    "module": "ESNext",                    // Systeme de modules ES (import/export)
    "lib": ["ES2020", "DOM", "DOM.Iterable"], // APIs disponibles (JS + navigateur)

    // --- Resolution des modules (compatible Vite) ---
    "moduleResolution": "bundler",         // Resolution adaptee a Vite
    "allowImportingTsExtensions": true,    // Autorise les imports avec extension .ts
    "resolveJsonModule": true,             // Autorise l'import de fichiers .json
    "isolatedModules": true,               // Chaque fichier compile seul (requis par Vite)
    "noEmit": true,                        // Pas de fichiers JS generes (Vite s'en charge)
    "jsx": "preserve",                     // Laisse le JSX intact (transforme par Vite)
    "skipLibCheck": true,                  // Ignore les .d.ts des node_modules (plus rapide)

    // --- Mode strict ---
    "strict": true,                        // Toutes les verifications strictes activees
    "noUnusedLocals": true,                // Erreur si variable declaree mais non utilisee
    "noUnusedParameters": true,            // Erreur si parametre de fonction non utilise
    "noFallthroughCasesInSwitch": true,    // Erreur si case sans break dans un switch

    // --- Alias ---
    "baseUrl": ".",
    "paths": { "@/*": ["src/*"] },         // '@/components/X' -> 'src/components/X'

    // --- Types globaux ---
    "types": ["vitest/globals"]            // describe, it, expect disponibles sans import
  },

  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue", "tests/**/*.ts"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### 3.2 - `tsconfig.node.json` (fichiers de config)

Configuration separee pour les fichiers qui tournent dans **Node.js** (pas dans le navigateur).

```jsonc
{
  "compilerOptions": {
    "composite": true,                     // Requis pour etre reference par tsconfig.json
    "skipLibCheck": true,                  // Ignore les .d.ts externes
    "module": "ESNext",                    // Les configs Vite utilisent import/export
    "moduleResolution": "bundler",         // Resolution compatible Vite
    "allowSyntheticDefaultImports": true   // Autorise 'import x from ...' sans export default
  },
  "include": ["vite.config.ts", "vitest.config.ts", "cypress.config.ts"]
}
```

### Pourquoi deux fichiers tsconfig ?

| Fichier | Concerne | Environnement |
|---------|----------|---------------|
| `tsconfig.json` | `src/`, `tests/` | Navigateur (DOM, window, document) |
| `tsconfig.node.json` | `vite.config.ts`, `vitest.config.ts`, `cypress.config.ts` | Node.js (process, fs, path) |

Les fichiers de configuration Vite/Vitest/Cypress sont executes par Node.js, pas par le navigateur. Ils ont besoin d'options differentes (ex: `composite`, `allowSyntheticDefaultImports`).

---

## 4. Configuration SCSS

### 4.1 - Variables globales (`src/assets/variables.scss`)

Fichier contenant tous les design tokens du projet :

```scss
// Couleurs principales
$primary: #3b5998;
$primary-dark: #2d4373;

// Couleurs neutres
$gray-50: #fafbfc;
$gray-500: #6b7280;
// ...

// Dimensions
$sidebar-width: 120px;
$header-height: 40px;

// Typographie
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
$font-size-sm: 12px;
$font-size-base: 13px;
```

Ces variables sont **injectees automatiquement** dans tous les fichiers SCSS grace a `additionalData` dans `vite.config.ts`. Aucun `@use` ou `@import` necessaire dans les composants.

### 4.2 - Styles globaux (`src/assets/global.scss`)

Reset CSS, styles de base (`html`, `body`, `#app`) et classes de transition (`fade`, `slide`).
Importe dans `main.ts` via `import './assets/global.scss'`.

### 4.3 - Pourquoi `sass-embedded` au lieu de `sass` ?

| Package | API | Status |
|---------|-----|--------|
| `sass` | Legacy JS API | Deprecated (sera supprime dans Sass 2.0) |
| `sass-embedded` | Modern Compiler API | Recommande, plus performant |

Le passage a `sass-embedded` + `api: 'modern-compiler'` dans Vite elimine les warnings de deprecation.

---

## 5. Auto-import des composants PrimeVue

### Avant (import manuel)

```ts
// main.ts - Il fallait importer et enregistrer chaque composant
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Calendar from 'primevue/calendar'
// ...
app.component('InputText', InputText)
app.component('Button', Button)
app.component('Calendar', Calendar)
```

### Apres (auto-import)

```ts
// vite.config.ts
Components({
  resolvers: [PrimeVueResolver()],
})
```

```html
<!-- Dans n'importe quel composant .vue, utiliser directement : -->
<template>
  <InputText v-model="value" />
  <Button label="Sauvegarder" />
  <Calendar v-model="date" />
</template>
<!-- Aucun import necessaire ! -->
```

### Avantages

| Aspect | Avant | Apres |
|--------|-------|-------|
| Import dans `main.ts` | 1 ligne par composant | Aucun |
| Enregistrement global | `app.component(...)` x N | Aucun |
| Taille du bundle (`index.js`) | 402 kB | 171 kB |
| Nouveaux composants PrimeVue | Modifier `main.ts` | Juste utiliser dans le template |

Le plugin genere automatiquement un fichier `components.d.ts` a la racine pour que TypeScript reconnaisse les composants auto-importes.

---

## 6. Configuration ESLint - `.eslintrc.cjs`

ESLint analyse le code pour detecter les erreurs et appliquer des conventions.

```js
/* eslint-env node */
module.exports = {
  root: true,          // Ne pas chercher de config dans les dossiers parents

  extends: [
    'plugin:vue/vue3-essential',        // Regles Vue 3 essentielles (erreurs critiques)
    'eslint:recommended',               // Regles JS recommandees
    '@vue/eslint-config-typescript',    // Support TypeScript dans les .vue
  ],

  parserOptions: {
    ecmaVersion: 'latest',             // Syntaxe JS la plus recente
  },

  rules: {
    'vue/multi-word-component-names': 'off',       // Autorise les noms en 1 mot (App, Button)
    'vue/no-reserved-component-names': 'off',      // Autorise Button (nom HTML reserve)
    'no-extra-semi': 'warn',                       // Warning pour les ; superflus
  },
}
```

### Choix du niveau de regles Vue

| Preset | Niveau | Contient |
|--------|--------|----------|
| `vue3-essential` | Minimum | Erreurs de syntaxe, v-if + v-for, clefs manquantes |
| `vue3-strongly-recommended` | Moyen | + Casse des attributs, self-closing tags |
| `vue3-recommended` | Strict | + Ordre des attributs, multi-line, indentation |

Le projet utilise `vue3-essential` pour eviter les contraintes de formatage trop strictes.

---

## 7. Configuration des tests unitaires (Vitest) - `vitest.config.ts`

Vitest est le framework de tests unitaires, nativement compatible avec Vite.

```ts
export default defineConfig({
  plugins: [vue()],                 // Compile les .vue dans les tests

  resolve: {
    alias: { '@': '...' },          // Meme alias que vite.config.ts
  },

  test: {
    globals: true,                  // describe, it, expect sans import
    environment: 'jsdom',           // Simule un DOM navigateur

    include: ['tests/unit/**/*.{test,spec}.{ts,tsx}'],  // Fichiers de test

    coverage: {
      provider: 'v8',              // Moteur de couverture V8 (rapide, natif Node.js)
      reporter: ['text', 'json', 'html'],  // Rapports terminal + JSON + HTML
      include: ['src/**/*.{ts,vue}'],      // Fichiers sources a analyser
      exclude: ['src/main.ts', 'src/types/**'],  // Exclut le bootstrap et les types
    },
  },
})
```

### Ou placer les tests ?

```
tests/
  unit/
    pretStore.test.ts              # Test du store Pinia
    CollapsibleSection.test.ts     # Test d'un composant Vue
    useNavigation.test.ts          # Test d'un composable
```

Convention : `nomDuFichier.test.ts` ou `nomDuFichier.spec.ts`.

---

## 8. Configuration des tests E2E (Cypress) - `cypress.config.ts`

Cypress lance un vrai navigateur pour tester l'application de bout en bout.

```ts
export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:3000',          // URL de l'app (meme port que Vite)
    specPattern: 'cypress/e2e/**/*.cy.{ts,tsx}', // Fichiers de test E2E
    supportFile: 'cypress/support/e2e.ts',     // Commandes custom (getByTestId, etc.)
    viewportWidth: 1440,                       // Largeur fenetre (desktop)
    viewportHeight: 900,                       // Hauteur fenetre
    video: false,                              // Pas de video (plus rapide en CI)
    screenshotOnRunFailure: true,              // Screenshot auto en cas d'echec
  },
})
```

### Structure Cypress

```
cypress/
  e2e/
    gestion-prets.cy.ts       # Scenarios de test end-to-end
  fixtures/
    dossier-pret.json          # Donnees de test (mock)
  support/
    e2e.ts                     # Commandes custom (getByTestId, clickTab, etc.)
  tsconfig.json                # Config TS dediee a Cypress
```

### Prerequis pour lancer les tests E2E

L'application doit tourner (`npm run dev`) avant de lancer Cypress :

```bash
# Terminal 1 : demarrer l'app
npm run dev

# Terminal 2 : lancer les tests
npm run test:e2e        # Mode headless (CI)
npm run test:e2e:open   # Mode interactif (navigateur visible)
```

---

## 9. Point d'entree de l'application - `src/main.ts`

Ce fichier initialise Vue et enregistre tous les plugins.

```ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'

// Styles PrimeVue
import 'primevue/resources/themes/lara-light-blue/theme.css'  // Theme visuel
import 'primevue/resources/primevue.min.css'                   // Styles de base
import 'primeicons/primeicons.css'                             // Icones

import App from './App.vue'
import router from './router'
import './assets/global.scss'                                   // Styles globaux

const app = createApp(App)

app.use(createPinia())                  // Pinia  : gestion d'etat globale
app.use(router)                         // Router : navigation SPA
app.use(PrimeVue, { ripple: true })     // PrimeVue : composants UI (ripple = effet clic)

// Les composants PrimeVue sont auto-importes (voir vite.config.ts)

app.mount('#app')
```

### Ordre d'execution

1. Vue cree l'instance de l'application
2. Les plugins sont enregistres (Pinia, Router, PrimeVue)
3. Les styles CSS/SCSS sont charges
4. L'application est montee dans `<div id="app">` de `index.html`

---

## 10. Scripts disponibles

Tous les scripts sont definis dans `package.json` et se lancent avec `npm run <script>`.

| Commande | Action |
|----------|--------|
| `npm run dev` | Demarre le serveur de dev Vite sur `http://localhost:3000` |
| `npm run build` | Verifie les types TypeScript puis genere le build de production dans `dist/` |
| `npm run preview` | Sert le build de production localement (pour tester avant deploiement) |
| `npm run test:unit` | Lance les tests unitaires une seule fois |
| `npm run test:unit:watch` | Lance les tests en mode watch (re-execute a chaque modification) |
| `npm run test:unit:coverage` | Lance les tests avec rapport de couverture de code |
| `npm run test:e2e` | Lance les tests Cypress en mode headless (CI) |
| `npm run test:e2e:open` | Ouvre l'interface Cypress (mode interactif) |
| `npm run lint` | Analyse et corrige automatiquement le code avec ESLint |
| `npm run type-check` | Verifie les types TypeScript sans generer de fichiers |

---

## 11. Structure du projet

```
RefontDeVueJs/
├── index.html                  # Page HTML unique (point d'entree du navigateur)
├── package.json                # Dependances et scripts npm
├── vite.config.ts              # Configuration Vite (bundler + dev server)
├── vitest.config.ts            # Configuration Vitest (tests unitaires)
├── cypress.config.ts           # Configuration Cypress (tests E2E)
├── tsconfig.json               # Configuration TypeScript (code source)
├── tsconfig.node.json          # Configuration TypeScript (fichiers de config)
├── .eslintrc.cjs               # Configuration ESLint (linter)
├── .gitignore                  # Fichiers exclus de Git
│
├── src/
│   ├── main.ts                 # Point d'entree : initialise Vue + plugins
│   ├── App.vue                 # Composant racine
│   ├── env.d.ts                # Declarations de types globales (.vue, Vite)
│   │
│   ├── assets/
│   │   ├── variables.scss      # Design tokens (couleurs, tailles, polices)
│   │   └── global.scss         # Reset CSS et styles globaux
│   │
│   ├── router/
│   │   └── index.ts            # Definition des routes (Vue Router)
│   │
│   ├── stores/
│   │   └── pretStore.ts        # Store Pinia (gestion d'etat des prets)
│   │
│   ├── composables/
│   │   └── useNavigation.ts    # Logique reutilisable de navigation
│   │
│   ├── components/
│   │   ├── AppSidebar.vue      # Barre laterale de navigation
│   │   ├── AppTabs.vue         # Onglets de navigation
│   │   ├── CollapsibleSection.vue  # Section repliable
│   │   ├── FormField.vue       # Champ de formulaire reutilisable
│   │   ├── SectionDates.vue    # Section des dates du pret
│   │   ├── SectionDonneesGenerales.vue  # Section donnees generales
│   │   └── SectionDonneesPret.vue       # Section donnees du pret
│   │
│   ├── layouts/
│   │   └── MainLayout.vue      # Layout principal (sidebar + tabs + contenu)
│   │
│   ├── views/
│   │   ├── ConsultationView.vue       # Vue Consultation
│   │   ├── DonneesDetailleesView.vue  # Vue Donnees detaillees
│   │   ├── DonneesComptablesView.vue  # Vue Donnees comptables
│   │   ├── PreavisView.vue            # Vue Preavis
│   │   └── RecouvrementView.vue       # Vue Recouvrement
│   │
│   └── types/
│       └── index.ts            # Interfaces et types TypeScript
│
├── tests/
│   └── unit/                   # Tests unitaires (Vitest)
│
└── cypress/
    ├── e2e/                    # Tests end-to-end
    ├── fixtures/               # Donnees de test
    └── support/                # Commandes custom Cypress
```
