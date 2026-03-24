describe('Toolbar', () => {
  beforeEach(() => {
    cy.visit('/recherche')
  })

  // ═══════════════════════════════════
  // Présence des éléments
  // ═══════════════════════════════════
  describe('Éléments visibles', () => {
    it('devrait afficher la toolbar', () => {
      cy.getByTestId('toolbar').should('be.visible')
    })

    it('devrait afficher le bouton hamburger', () => {
      cy.getByTestId('hamburger-btn').should('be.visible')
    })

    it('devrait afficher le bouton dark mode', () => {
      cy.getByTestId('dark-mode-toggle').should('be.visible')
    })

    it("devrait afficher le nom de l'utilisateur (Jean Dupont)", () => {
      cy.getByTestId('toolbar').should('contain', 'Jean')
      cy.getByTestId('toolbar').should('contain', 'Dupont')
    })

    it('devrait afficher le badge DEV', () => {
      cy.getByTestId('toolbar').should('contain', 'DEV')
    })

    it('devrait afficher le titre de l\'application', () => {
      cy.getByTestId('toolbar').should('contain', 'Template')
    })
  })

  // ═══════════════════════════════════
  // Sidebar toggle (hamburger)
  // ═══════════════════════════════════
  describe('Sidebar toggle (hamburger)', () => {
    it('devrait réduire la sidebar au clic sur le hamburger', () => {
      cy.getByTestId('hamburger-btn').click()
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })

    it('devrait étendre la sidebar au second clic', () => {
      cy.getByTestId('hamburger-btn').click()
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
      cy.getByTestId('hamburger-btn').click()
      cy.getByTestId('sidebar').should('not.have.class', 'collapsed')
    })

    it('devrait masquer les labels sidebar quand réduite', () => {
      cy.getByTestId('hamburger-btn').click()
      cy.getByTestId('sidebar').find('.sidebar-label').should('not.exist')
    })

    it('devrait garder les icônes sidebar visibles après collapse', () => {
      cy.getByTestId('hamburger-btn').click()
      cy.getByTestId('sidebar-item-recherche').should('be.visible')
    })

    it('devrait afficher le header sidebar "Menu" par défaut', () => {
      cy.getByTestId('sidebar').should('contain', 'Menu')
    })

    it('devrait masquer le header sidebar après collapse', () => {
      cy.getByTestId('hamburger-btn').click()
      cy.get('.sidebar-header-label').should('not.exist')
    })
  })

  // ═══════════════════════════════════
  // Dark mode toggle
  // ═══════════════════════════════════
  describe('Dark mode toggle', () => {
    it('ne devrait pas avoir le dark mode actif par défaut', () => {
      cy.get('html').should('not.have.class', 'dark-mode')
    })

    it('devrait activer le dark mode au clic', () => {
      cy.toggleDarkMode()
      cy.get('html').should('have.class', 'dark-mode')
    })

    it('devrait désactiver le dark mode au second clic', () => {
      cy.toggleDarkMode()
      cy.get('html').should('have.class', 'dark-mode')
      cy.toggleDarkMode()
      cy.get('html').should('not.have.class', 'dark-mode')
    })

    it('devrait conserver le dark mode lors de la navigation', () => {
      cy.toggleDarkMode()
      cy.get('html').should('have.class', 'dark-mode')
      cy.clickSidebarItem('deblocage')
      cy.get('html').should('have.class', 'dark-mode')
    })
  })

  // ═══════════════════════════════════
  // Toolbar en consultation
  // ═══════════════════════════════════
  describe('Toolbar en consultation', () => {
    it('devrait rester visible en consultation', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('toolbar').should('contain', 'Template')
    })
  })
})
