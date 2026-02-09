# Gestion des Prêts - SIGAC

Application Vue 3 de gestion des dossiers de prêts bancaires.

## Stack technique

| Technologie | Rôle |
|---|---|
| **Vue 3** (Composition API + `<script setup>`) | Framework frontend |
| **TypeScript** | Typage statique |
| **Vue Router 4** | Navigation SPA |
| **Pinia** | Gestion d'état |
| **PrimeVue 3** | Librairie UI |
| **PrimeIcons** | Icônes |
| **Vite 5** | Build tool |
| **Vitest** | Tests unitaires |
| **Cypress 13** | Tests E2E |
| **SCSS** | Styles (variables, nesting) |

## Structure du projet

```
RefontDeVueJs/
├── cypress/                      # Tests E2E
│   ├── e2e/
│   │   └── RefontDeVueJs.cy.ts  # Scénarios E2E complets
│   ├── fixtures/
│   │   └── dossier-pret.json    # Données de test
│   └── support/
│       └── e2e.ts               # Custom commands Cypress
├── src/
│   ├── assets/
│   │   ├── global.scss          # Styles globaux
│   │   └── variables.scss       # Design tokens SCSS
│   ├── components/
│   │   ├── AppSidebar.vue       # Sidebar gauche
│   │   ├── AppTabs.vue          # Barre d'onglets
│   │   ├── CollapsibleSection.vue  # Section dépliable réutilisable
│   │   ├── FormField.vue        # Champ de formulaire (label + input)
│   │   ├── SectionDonneesGenerales.vue
│   │   ├── SectionDonneesPret.vue
│   │   └── SectionDates.vue
│   ├── composables/
│   │   └── useNavigation.ts     # Logique de navigation (tabs + sidebar)
│   ├── layouts/
│   │   └── MainLayout.vue       # Layout principal (sidebar + tabs + content)
│   ├── router/
│   │   └── index.ts             # Configuration Vue Router
│   ├── stores/
│   │   └── pretStore.ts         # Store Pinia (données prêt + état UI)
│   ├── types/
│   │   └── index.ts             # Interfaces TypeScript du domaine
│   ├── views/
│   │   ├── ConsultationView.vue
│   │   ├── DonneesDetailleesView.vue
│   │   ├── DonneesComptablesView.vue
│   │   ├── PreavisView.vue
│   │   └── RecouvrementView.vue
│   ├── App.vue
│   ├── env.d.ts
│   └── main.ts                  # Point d'entrée + config PrimeVue
├── tests/
│   └── unit/
│       ├── pretStore.test.ts          # Tests store Pinia
│       ├── CollapsibleSection.test.ts # Tests composant section
│       └── useNavigation.test.ts      # Tests composable navigation
├── cypress.config.ts
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.node.json
├── vite.config.ts
└── vitest.config.ts
```

## Installation

```bash
# Cloner le projet
git clone <repo-url>
cd RefontDeVueJs

# Installer les dépendances
npm install
```

## Scripts disponibles

```bash
# Développement
npm run dev              # Lancer le serveur de dev (port 3000)

# Build
npm run build            # Build de production
npm run preview          # Prévisualiser le build

# Tests unitaires (Vitest)
npm run test:unit        # Lancer les tests une fois
npm run test:unit:watch  # Mode watch
npm run test:unit:coverage # Avec couverture de code

# Tests E2E (Cypress)
npm run test:e2e         # Lancer en headless
npm run test:e2e:open    # Ouvrir l'interface Cypress

# Qualité
npm run lint             # ESLint
npm run type-check       # Vérification TypeScript
```

## Architecture

### Composables
- **`useNavigation()`** — Centralise la logique de navigation (onglets, sidebar, routing)

### Store Pinia (`pretStore`)
- État des sections dépliables (general, pret, dates)
- Données du formulaire (donneesGenerales, donneesPret, datesPret)
- Actions : toggle sections, chargement dossier, reset formulaire

### Routing
| Route | Vue | Onglet |
|---|---|---|
| `/dossier/consultation` | ConsultationView | Consultation |
| `/dossier/donnees-detaillees` | DonneesDetailleesView | Données détaillées |
| `/dossier/donnees-comptables` | DonneesComptablesView | Données comptables |
| `/dossier/preavis` | PreavisView | Préavis |
| `/dossier/recouvrement` | RecouvrementView | Recouvrement |

### Tests
- **Unitaires** : Store Pinia, composable `useNavigation`, composant `CollapsibleSection`
- **E2E** : Navigation complète, sidebar, onglets, sections dépliables, affichage formulaire
