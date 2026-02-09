import { defineConfig } from 'cypress'

// https://docs.cypress.io/guides/references/configuration
export default defineConfig({
  e2e: {
    // URL de base de l'application en dev (doit correspondre au port dans vite.config.ts)
    baseUrl: 'http://localhost:3000',

    // Pattern de recherche des fichiers de tests E2E
    specPattern: 'cypress/e2e/**/*.cy.{ts,tsx}',

    // Fichier de support charge avant chaque test (commandes custom, hooks globaux)
    supportFile: 'cypress/support/e2e.ts',

    // Taille de la fenetre du navigateur pendant les tests
    viewportWidth: 1440,
    viewportHeight: 900,

    // Desactive l'enregistrement video (accelere les tests en CI)
    video: false,

    // Capture un screenshot automatiquement en cas d'echec d'un test
    screenshotOnRunFailure: true,
  },
})
