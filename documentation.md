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
12. [Integration du Dark Mode](#12-integration-du-dark-mode)

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

---

## 12. Integration du Dark Mode

Guide complet, etape par etape, pour integrer un dark mode dans un projet Vue 3 + SCSS existant. Cette approche repose sur des **CSS custom properties** (variables CSS) qui changent de valeur quand la classe `dark-mode` est ajoutee sur `<html>`.

### Sommaire du chapitre

- [12.1 Principe architectural](#121---principe-architectural)
- [12.2 Etape 1 : Ajouter l'etat dans le store Pinia](#122---etape-1--ajouter-letat-dans-le-store-pinia)
- [12.3 Etape 2 : Creer le watcher dans App.vue](#123---etape-2--creer-le-watcher-dans-appvue)
- [12.4 Etape 3 : Creer le fichier dark-theme.scss](#124---etape-3--creer-le-fichier-dark-themescss)
- [12.5 Etape 4 : Importer dans global.scss et ajouter la transition](#125---etape-4--importer-dans-globalscss-et-ajouter-la-transition)
- [12.6 Etape 5 : Ajouter le bouton de bascule dans la toolbar](#126---etape-5--ajouter-le-bouton-de-bascule-dans-la-toolbar)
- [12.7 Etape 6 : Migrer les composants vers var()](#127---etape-6--migrer-les-composants-vers-var)
- [12.8 Piege a eviter : specificite des scoped styles](#128---piege-a-eviter--specificite-des-scoped-styles)
- [12.9 Reference complete des variables CSS](#129---reference-complete-des-variables-css)
- [12.10 Checklist de verification](#1210---checklist-de-verification)
- [12.11 Ajouter de nouvelles variables](#1211---ajouter-de-nouvelles-variables)

---

### 12.1 - Principe architectural

Le dark mode fonctionne en 3 couches :

```
┌──────────────────────────────────────────────────────┐
│  1. Store Pinia (pretStore.ts)                       │
│     darkMode: ref<boolean>                           │
│     toggleDarkMode()                                 │
├──────────────────────────────────────────────────────┤
│  2. Watcher dans App.vue                             │
│     document.documentElement.classList.toggle(        │
│       'dark-mode', isDark                            │
│     )                                                │
├──────────────────────────────────────────────────────┤
│  3. CSS Custom Properties (dark-theme.scss)          │
│     :root          { --bg-body: #f5f6f8; }  (light) │
│     html.dark-mode { --bg-body: #0f172a; }  (dark)  │
├──────────────────────────────────────────────────────┤
│  4. Composants Vue                                   │
│     background: var(--bg-body);                      │
│     (valeur resolue automatiquement selon le mode)   │
└──────────────────────────────────────────────────────┘
```

**Pourquoi des CSS custom properties et pas du SCSS conditionnel ?**

| Approche | Avantage | Inconvenient |
|----------|----------|-------------|
| SCSS `@if` / mixin | Familier | Genere du CSS statique, ne change pas au runtime |
| Classes CSS dupliquees (`.dark .sidebar`) | Simple | Specificite difficile a gerer avec les scoped styles |
| **CSS custom properties (`var()`)** | **Change au runtime, cascade naturelle, compatible scoped styles** | Necessite de definir les variables en amont |

---

### 12.2 - Etape 1 : Ajouter l'etat dans le store Pinia

Dans le store principal (`src/stores/pretStore.ts`), ajouter un `ref` booleen et une action de bascule.

```ts
// src/stores/pretStore.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePretStore = defineStore('pret', () => {
  // ... autres etats ...

  const darkMode = ref<boolean>(false)

  function toggleDarkMode() {
    darkMode.value = !darkMode.value
  }

  return {
    // ... autres exports ...
    darkMode,
    toggleDarkMode,
  }
})
```

| Element | Role |
|---------|------|
| `darkMode` | Booleen reactif : `false` = light, `true` = dark |
| `toggleDarkMode()` | Inverse la valeur de `darkMode` |

> **Optionnel** : Pour persister le choix de l'utilisateur, sauvegarder dans `localStorage` :
> ```ts
> const darkMode = ref<boolean>(localStorage.getItem('dark-mode') === 'true')
>
> function toggleDarkMode() {
>   darkMode.value = !darkMode.value
>   localStorage.setItem('dark-mode', String(darkMode.value))
> }
> ```

---

### 12.3 - Etape 2 : Creer le watcher dans App.vue

Le composant racine `App.vue` observe `darkMode` et bascule la classe CSS sur l'element `<html>`.

```vue
<!-- src/App.vue -->
<template>
  <router-view />
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()

// Dark mode : bascule la classe sur <html> pour activer les CSS custom properties
watch(() => store.darkMode, (isDark) => {
  document.documentElement.classList.toggle('dark-mode', isDark)
}, { immediate: true })
</script>
```

**Detail du fonctionnement :**

| Etape | Ce qui se passe |
|-------|-----------------|
| 1 | L'utilisateur clique sur le bouton dark mode |
| 2 | `store.toggleDarkMode()` passe `darkMode` a `true` |
| 3 | Le `watch` detecte le changement |
| 4 | `classList.toggle('dark-mode', true)` ajoute la classe sur `<html>` |
| 5 | Le selecteur CSS `html.dark-mode` s'active |
| 6 | Toutes les CSS custom properties sont redefinies |
| 7 | Tous les `var(--xxx)` dans les composants sont resolus avec les nouvelles valeurs |

> **Pourquoi `{ immediate: true }` ?** Pour appliquer le bon etat des l'initialisation (utile si le choix est persiste dans `localStorage`).

---

### 12.4 - Etape 3 : Creer le fichier dark-theme.scss

Creer le fichier `src/assets/dark-theme.scss`. Ce fichier definit toutes les CSS custom properties dans `:root` (mode light) et les redefinit dans `html.dark-mode` (mode dark).

**Important** : Ce fichier est un fichier SCSS (pas CSS pur) car il utilise les variables SCSS (`$primary`, `$gray-300`, etc.) definies dans `variables.scss` qui est injecte automatiquement par Vite.

```scss
// src/assets/dark-theme.scss
// ═══════════════════════════════════════
// Dark Theme - CSS Custom Properties
// ═══════════════════════════════════════

// ── MODE LIGHT (valeurs par defaut) ──
:root {
  // Backgrounds
  --bg-body: #{$gray-100};          // Fond de la page
  --bg-surface: #{$white};          // Fond des cartes/panneaux
  --bg-content: #{$gray-100};       // Fond de la zone de contenu
  --bg-elevated: #{$gray-50};       // Fond legerement sureleve
  --bg-input: #{$white};            // Fond des champs de formulaire
  --bg-chevron: #{$gray-200};       // Fond des icones chevron

  // Text
  --text-primary: #{$gray-800};     // Texte principal
  --text-secondary: #{$gray-600};   // Texte secondaire
  --text-muted: #{$gray-500};       // Texte attenue
  --text-label: #{$gray-600};       // Labels de formulaire

  // Borders
  --border-main: #{$gray-300};      // Bordures principales
  --border-light: #{$gray-200};     // Bordures legeres
  --border-card: #{$gray-900};      // Bordures de cartes

  // Components
  --toolbar-bg: #{$primary};        // Fond de la toolbar
  --toolbar-shadow: #{$shadow-toolbar};
  --sidebar-bg: #{$white};          // Fond de la sidebar
  --tabs-bg: #{$white};             // Fond de la barre d'onglets
  --section-line: #{$gray-900};     // Lignes des sections repliables
  --content-shadow: #{$shadow-sm};
  --blue-bar-bg: #{$primary-ciel};  // Barre bleue decorative

  // Hover (etats survol)
  --hover-bg: #{$primary-light};    // Fond au survol
  --hover-text: #{$primary-dark};   // Texte au survol

  // Sidebar
  --sidebar-border: #{$primary-ciel};       // Bordure droite sidebar
  --sidebar-active-bg: #{$primary-apptabs}; // Fond element actif
  --sidebar-active-text: #{$white};         // Texte element actif

  // Tabs
  --tab-active-bg: #{$primary-apptabs};     // Fond onglet actif
  --tab-active-text: #{$white};             // Texte onglet actif
  --tab-active-border: #{$gray-900};        // Bordure onglet actif

  // Badges
  --badge-info-bg: #{$info-light};          // Fond badge info
  --badge-info-text: #{$info};              // Texte badge info
  --badge-warn-bg: #{$warning-light};       // Fond badge warning
  --badge-warn-text: #{$warning};           // Texte badge warning
}

// ── MODE DARK ──
html.dark-mode {
  // Backgrounds — palette Slate (Tailwind)
  --bg-body: #0f172a;              // slate-900
  --bg-surface: #1e293b;           // slate-800
  --bg-content: #1e293b;
  --bg-elevated: #273548;          // entre slate-800 et slate-700
  --bg-input: #273548;
  --bg-chevron: #{$gray-100};

  // Text
  --text-primary: #e2e8f0;         // slate-200
  --text-secondary: #94a3b8;       // slate-400
  --text-muted: #64748b;           // slate-500
  --text-label: #94a3b8;

  // Borders
  --border-main: #475569;          // slate-600
  --border-light: #334155;         // slate-700
  --border-card: #475569;

  // Components
  --toolbar-bg: #0f172a;           // Meme fond que la page (effet immersif)
  --toolbar-shadow: 0 1px 4px rgba(0, 0, 0, 0.4);
  --sidebar-bg: #1e293b;
  --tabs-bg: #1e293b;
  --section-line: #64748b;
  --content-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  --blue-bar-bg: #{darken($primary-ciel, 10%)};

  // Hover — bleu clair semi-transparent
  --hover-bg: rgba(96, 165, 250, 0.15);   // blue-400 a 15%
  --hover-text: #60a5fa;                   // blue-400

  // Sidebar — fond subtil au lieu de couleur opaque
  --sidebar-border: rgba(29, 176, 255, 0.4);
  --sidebar-active-bg: rgba(110, 204, 241, 0.2);
  --sidebar-active-text: #93c5fd;          // blue-300

  // Tabs
  --tab-active-bg: rgba(110, 204, 241, 0.2);
  --tab-active-text: #e2e8f0;             // slate-200
  --tab-active-border: #60a5fa;           // blue-400

  // Badges — fond rgba pour lisibilite sur fond sombre
  --badge-info-bg: rgba(37, 99, 235, 0.2);  // blue-600 a 20%
  --badge-info-text: #60a5fa;                // blue-400
  --badge-warn-bg: rgba(180, 83, 9, 0.2);   // amber-700 a 20%
  --badge-warn-text: #fbbf24;                // amber-400

  // Appliquer le fond et texte sur <html> directement
  background: var(--bg-body);
  color: var(--text-primary);
}
```

**Points importants :**

| Point | Explication |
|-------|-------------|
| `#{$variable}` | Interpolation SCSS : insere la valeur de la variable SCSS dans la CSS custom property |
| `rgba(...)` en dark | Les fonds semi-transparents s'integrent mieux sur fond sombre qu'une couleur opaque |
| `background` / `color` sur `html.dark-mode` | Force le fond et le texte de base quand on bascule en dark |
| Palette Slate | Palette de gris bleutee de Tailwind, adaptee au dark mode |

---

### 12.5 - Etape 4 : Importer dans global.scss et ajouter la transition

Modifier `src/assets/global.scss` pour importer le fichier dark-theme et ajouter une transition douce lors de la bascule.

```scss
// src/assets/global.scss
@import './dark-theme';   // <-- Ajouter cet import en haut du fichier

// ...

html,
body {
  height: 100%;
  font-family: $font-family;
  font-size: $font-size-base;
  color: var(--text-primary);
  background: var(--bg-body);
  transition: background-color 0.3s ease, color 0.3s ease;  // <-- Transition douce
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
```

| Modification | Effet |
|-------------|-------|
| `@import './dark-theme'` | Charge les CSS custom properties definies dans `dark-theme.scss` |
| `transition: background-color 0.3s ease, color 0.3s ease` | Le passage light/dark se fait en 0.3s au lieu d'un changement instantane |
| `color: var(--text-primary)` | Le texte de base utilise la variable (deja en place) |
| `background: var(--bg-body)` | Le fond de la page utilise la variable (deja en place) |

> **Note** : La transition ne porte que sur `background-color` et `color`. Les composants internes changent instantanement, ce qui donne un effet propre sans decalage visible.

---

### 12.6 - Etape 5 : Ajouter le bouton de bascule dans la toolbar

Dans `AppToolbar.vue`, ajouter un bouton qui affiche une lune (light) ou un soleil (dark).

**Template** (dans `.toolbar-right`) :
```html
<button class="theme-toggle" @click="store.toggleDarkMode()">
  <i class="pi" :class="store.darkMode ? 'pi-sun' : 'pi-moon'" />
</button>
```

**Style** (scoped SCSS) :
```scss
.theme-toggle {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: $white;
  font-size: 14px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.25);
    border-color: rgba(255, 255, 255, 0.5);
  }
}
```

| Element | Detail |
|---------|--------|
| `pi-moon` / `pi-sun` | Icones PrimeIcons (deja installe dans le projet) |
| `:class` dynamique | Affiche le soleil en dark (pour revenir au light) et la lune en light (pour passer au dark) |
| `rgba(255,255,255,...)` | Couleurs blanches semi-transparentes : fonctionnent sur la toolbar quelque soit le mode |

---

### 12.7 - Etape 6 : Migrer les composants vers var()

C'est l'etape la plus importante. Il faut remplacer les couleurs SCSS hardcodees dans les styles scoped des composants par des appels `var(--xxx)`.

#### Methode generale

Pour chaque composant, reperer les couleurs SCSS et les remplacer :

```scss
// ❌ AVANT (ne s'adapte pas au dark mode)
.sidebar-item {
  &:hover {
    background: $primary-light;
    color: $primary-dark;
  }
  &.active {
    background: $primary-apptabs;
    color: $white;
  }
}

// ✅ APRES (s'adapte automatiquement)
.sidebar-item {
  &:hover {
    background: var(--hover-bg);
    color: var(--hover-text);
  }
  &.active {
    background: var(--sidebar-active-bg);
    color: var(--sidebar-active-text);
  }
}
```

#### Composants migres et correspondances exactes

**AppSidebar.vue** — 3 modifications

| Propriete CSS | Avant (SCSS) | Apres (var) |
|--------------|-------------|------------|
| `.app-sidebar` `border-right` | `$primary-ciel` | `var(--sidebar-border)` |
| `.sidebar-item:hover` `background` | `$primary-light` | `var(--hover-bg)` |
| `.sidebar-item:hover` `color` | `$primary-dark` | `var(--hover-text)` |
| `.sidebar-item.active` `background` | `$primary-apptabs` | `var(--sidebar-active-bg)` |
| `.sidebar-item.active` `color` | `$white` | `var(--sidebar-active-text)` |

**AppTabs.vue** — 3 modifications

| Propriete CSS | Avant (SCSS) | Apres (var) |
|--------------|-------------|------------|
| `.tab-item:hover` `background` | `$primary-light` | `var(--hover-bg)` |
| `.tab-item:hover` `color` | `$primary-dark` | `var(--hover-text)` |
| `.tab-item.active` `color` | `$white` | `var(--tab-active-text)` |
| `.tab-item.active` `background` | `$primary-apptabs` | `var(--tab-active-bg)` |
| `.tab-item.active` `border-color` | `var(--border-card)` | `var(--tab-active-border)` |

**CollapsibleSection.vue** — 1 modification

| Propriete CSS | Avant (SCSS) | Apres (var) |
|--------------|-------------|------------|
| `.section-header:hover .chevron-wrapper` `background` | `$gray-300` | `var(--hover-bg)` |

**ConsultationView.vue** — 4 modifications

| Propriete CSS | Avant (SCSS) | Apres (var) |
|--------------|-------------|------------|
| `.header-icon` `color` | `$primary` | `var(--hover-text)` |
| `.header-icon` `background` | `$gray-100` | `var(--bg-elevated)` |
| `.badge-status` `color` | `$info` | `var(--badge-info-text)` |
| `.badge-status` `background` | `$gray-100` | `var(--badge-info-bg)` |
| `.btn-action` `color` | `$gray-600` | `var(--text-secondary)` |
| `.btn-action` `background` | `$gray-100` | `var(--bg-surface)` |
| `.btn-action` `border` | `$gray-300` | `var(--border-main)` |
| `.btn-action:hover` `color` | `$primary` | `var(--hover-text)` |
| `.btn-action:hover` `border-color` | `$primary` | `var(--hover-text)` |
| `.btn-action:hover` `background` | `$primary-light` | `var(--hover-bg)` |

**Vues placeholder (5 fichiers)** — 1 modification par fichier

Fichiers concernes : `RbtAnticipesView.vue`, `DeblocageView.vue`, `DomiciliationView.vue`, `PaliersView.vue`, `DonneesFinancieresView.vue`.

| Propriete CSS | Avant (SCSS) | Apres (var) |
|--------------|-------------|------------|
| `.badge-coming` `color` | `$warning` | `var(--badge-warn-text)` |
| `.badge-coming` `background` | `$warning-light` | `var(--badge-warn-bg)` |

---

### 12.8 - Piege a eviter : specificite des scoped styles

**Probleme** : Vue scoped styles ajoutent un attribut `[data-v-xxx]` sur chaque selecteur CSS. Cet attribut a une specificite de (0,1,0) qui s'ajoute a celle du selecteur :

```css
/* Vue genere ceci : */
.sidebar-item[data-v-abc123] { background: blue; }   /* specificite : 0,2,0 */

/* Un override global dark mode : */
html.dark-mode .sidebar-item { background: red; }     /* specificite : 0,1,1 */
```

Le scoped style (0,2,0) **gagne** sur le dark mode (0,1,1). Le dark mode n'a aucun effet.

**Solution** : Utiliser `var()` dans les styles scoped. La variable est resolue au runtime et prend la valeur definie par le selecteur `html.dark-mode` ou `:root`, sans conflit de specificite.

```scss
// Dans un composant scoped :
.sidebar-item {
  background: var(--hover-bg);   // Resolu dynamiquement
  // Vue genere : .sidebar-item[data-v-xxx] { background: var(--hover-bg); }
  // La valeur de --hover-bg est determinee par :root ou html.dark-mode
  // => Pas de conflit de specificite !
}
```

**Regle d'or** : Ne jamais utiliser de couleur SCSS hardcodee (`$gray-300`, `$primary`, etc.) dans un style scoped pour une propriete qui doit changer en dark mode. Toujours passer par `var(--xxx)`.

---

### 12.9 - Reference complete des variables CSS

Tableau exhaustif de toutes les CSS custom properties disponibles :

#### Backgrounds

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--bg-body` | `#f5f6f8` | `#0f172a` | Fond de la page |
| `--bg-surface` | `#ffffff` | `#1e293b` | Fond des cartes, panneaux |
| `--bg-content` | `#f5f6f8` | `#1e293b` | Zone de contenu principale |
| `--bg-elevated` | `#fafbfc` | `#273548` | Elements sureleves |
| `--bg-input` | `#ffffff` | `#273548` | Champs de formulaire |
| `--bg-chevron` | `#e8eaed` | `#f5f6f8` | Fond des icones chevron |

#### Texte

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--text-primary` | `#1f2937` | `#e2e8f0` | Texte principal |
| `--text-secondary` | `#4b5563` | `#94a3b8` | Texte secondaire |
| `--text-muted` | `#6b7280` | `#64748b` | Texte attenue |
| `--text-label` | `#4b5563` | `#94a3b8` | Labels |

#### Bordures

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--border-main` | `#d1d5db` | `#475569` | Bordures principales |
| `--border-light` | `#e8eaed` | `#334155` | Bordures legeres |
| `--border-card` | `#111827` | `#475569` | Bordures de cartes |

#### Composants

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--toolbar-bg` | `#3b5998` | `#0f172a` | Fond toolbar |
| `--toolbar-shadow` | `0 1px 3px ...` | `0 1px 4px ...` | Ombre toolbar |
| `--sidebar-bg` | `#ffffff` | `#1e293b` | Fond sidebar |
| `--tabs-bg` | `#ffffff` | `#1e293b` | Fond onglets |
| `--section-line` | `#111827` | `#64748b` | Lignes de sections |
| `--content-shadow` | `0 1px 2px ...` | `0 2px 4px ...` | Ombre contenu |
| `--blue-bar-bg` | `#1db0ff` | assombri 10% | Barre decorative |

#### Hover

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--hover-bg` | `#e8edf5` | `rgba(96,165,250,0.15)` | Fond au survol |
| `--hover-text` | `#3e96b9` | `#60a5fa` | Texte au survol |

#### Sidebar

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--sidebar-border` | `#1db0ff` | `rgba(29,176,255,0.4)` | Bordure droite |
| `--sidebar-active-bg` | `#6eccf1` | `rgba(110,204,241,0.2)` | Fond element actif |
| `--sidebar-active-text` | `#ffffff` | `#93c5fd` | Texte element actif |

#### Tabs

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--tab-active-bg` | `#6eccf1` | `rgba(110,204,241,0.2)` | Fond onglet actif |
| `--tab-active-text` | `#ffffff` | `#e2e8f0` | Texte onglet actif |
| `--tab-active-border` | `#111827` | `#60a5fa` | Bordure onglet actif |

#### Badges

| Variable | Light | Dark | Usage |
|----------|-------|------|-------|
| `--badge-info-bg` | `#dbeafe` | `rgba(37,99,235,0.2)` | Fond badge info |
| `--badge-info-text` | `#2563eb` | `#60a5fa` | Texte badge info |
| `--badge-warn-bg` | `#fef3c7` | `rgba(180,83,9,0.2)` | Fond badge warning |
| `--badge-warn-text` | `#b45309` | `#fbbf24` | Texte badge warning |

---

### 12.10 - Checklist de verification

Apres avoir applique toutes les etapes, verifier les points suivants :

| # | Verification | Comment tester |
|---|-------------|----------------|
| 1 | `npm run build` passe sans erreur | Lancer `npm run build` |
| 2 | Toggle light/dark fonctionne | Cliquer sur le bouton lune/soleil dans la toolbar |
| 3 | Transition douce (0.3s) | Le fond de la page change progressivement, pas instantanement |
| 4 | Toolbar : fond sombre immersif | En dark, la toolbar a le meme fond que la page (#0f172a) |
| 5 | Sidebar : hover bleu semi-transparent | Survoler un element de la sidebar en dark mode |
| 6 | Sidebar : actif subtil | L'element actif a un fond `rgba` et non une couleur opaque |
| 7 | Tabs : hover bleu semi-transparent | Survoler un onglet inactif en dark mode |
| 8 | Tabs : actif avec bordure bleue | L'onglet actif a une bordure `#60a5fa` en dark |
| 9 | Badges info lisibles | Le badge "Dossier en cours" est lisible dans les deux modes |
| 10 | Badges warning lisibles | Les badges "A venir" sont lisibles dans les deux modes |
| 11 | Boutons action lisibles | Les boutons "Tout ouvrir" / "Tout fermer" sont lisibles en dark |
| 12 | Sections repliables : chevron hover | Le fond du chevron change au survol dans les deux modes |
| 13 | Champs de formulaire | Les inputs ont un fond sombre et un texte clair en dark |
| 14 | Scrollbars | Verifier que les scrollbars restent visibles dans les deux modes |

---

### 12.11 - Ajouter de nouvelles variables

Pour ajouter une nouvelle couleur themeable a l'avenir, suivre ces 3 etapes :

**Etape A** — Ajouter la variable dans `dark-theme.scss` :
```scss
:root {
  --ma-nouvelle-var: #{$valeur-light};
}
html.dark-mode {
  --ma-nouvelle-var: #valeur-dark;
}
```

**Etape B** — Utiliser dans le composant :
```scss
// Dans le <style scoped> du composant
.ma-classe {
  color: var(--ma-nouvelle-var);
}
```

**Etape C** — Verifier dans les deux modes :
- `npm run dev`
- Tester en light et en dark
- `npm run build` pour verifier qu'il n'y a pas d'erreur

> **Convention de nommage** : `--categorie-element-propriete`
> Exemples : `--bg-body`, `--text-primary`, `--hover-bg`, `--badge-info-text`
