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

// Type declarations
declare global {
  namespace Cypress {
    interface Chainable {
      getByTestId(testId: string): Chainable<JQuery<HTMLElement>>
      clickTab(tabId: string): Chainable<JQuery<HTMLElement>>
      clickSidebarItem(itemId: string): Chainable<JQuery<HTMLElement>>
      toggleSection(sectionId: string): Chainable<JQuery<HTMLElement>>
    }
  }
}

export {}
