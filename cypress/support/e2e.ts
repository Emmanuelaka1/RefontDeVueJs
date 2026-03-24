// Couverture de code : collecte les données Istanbul après chaque test
import '@cypress/code-coverage/support'

// Cypress E2E support file

// Custom commands
Cypress.Commands.add('getByTestId', (testId: string) => {
  return cy.get(`[data-testid="${testId}"]`)
})

Cypress.Commands.add('clickTab', (tabId: string) => {
  return cy.getByTestId(`tab-${tabId}`).click()
})

Cypress.Commands.add('clickSidebarItem', (itemId: string) => {
  return cy.getByTestId(`sidebar-item-${itemId}`).click()
})

Cypress.Commands.add('toggleSection', (sectionId: string) => {
  return cy.getByTestId(`section-header-${sectionId}`).click()
})

Cypress.Commands.add('toggleSidebar', () => {
  return cy.getByTestId('hamburger-btn').click()
})

Cypress.Commands.add('toggleDarkMode', () => {
  return cy.getByTestId('dark-mode-toggle').click()
})

/**
 * Recherche un dossier par son ID via la vue Recherche
 * et navigue vers la consultation (donnees-generales).
 */
Cypress.Commands.add('rechercherEtConsulter', (dossierId: string) => {
  cy.visit('/recherche')
  cy.get('.field-input').type(dossierId)
  cy.get('.btn-search').click()
  // Attendre les résultats async (mock 400ms + Transition fade)
  cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
  cy.get('.result-row').first().click()
  cy.getByTestId('consultation-view').should('be.visible')
  cy.get('.loading-indicator').should('not.exist')
  cy.getByTestId('section-general').should('exist')
})

/**
 * Navigue directement vers la consultation d'un dossier par URL.
 * Utilise le mock service qui charge le dossier onMounted.
 */
Cypress.Commands.add('visitConsultation', (dossierId: string, tab = 'donnees-generales') => {
  cy.visit(`/consultation/${dossierId}/${tab}`)
  // Attendre que le chargement async (mock 400ms) soit terminé
  cy.getByTestId('consultation-view').should('be.visible')
  cy.get('.loading-indicator').should('not.exist')
  // Attendre que les sections soient rendues (v-if sur store.dossierCourant)
  cy.getByTestId('section-general').should('exist')
})

// Type declarations
declare global {
  namespace Cypress {
    interface Chainable {
      getByTestId(testId: string): Chainable<JQuery<HTMLElement>>
      clickTab(tabId: string): Chainable<JQuery<HTMLElement>>
      clickSidebarItem(itemId: string): Chainable<JQuery<HTMLElement>>
      toggleSection(sectionId: string): Chainable<JQuery<HTMLElement>>
      toggleSidebar(): Chainable<JQuery<HTMLElement>>
      toggleDarkMode(): Chainable<JQuery<HTMLElement>>
      rechercherEtConsulter(dossierId: string): Chainable<void>
      visitConsultation(dossierId: string, tab?: string): Chainable<void>
    }
  }
}

export {}
