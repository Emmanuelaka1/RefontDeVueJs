describe('Store - Actions et état', () => {
  // ═══════════════════════════════════
  // Chargement du dossier (chargerDossier)
  // ═══════════════════════════════════
  describe('Chargement du dossier', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait charger et afficher les données du premier dossier', () => {
      cy.getByTestId('field-emprunteur').should('be.visible')
    })

    it('devrait afficher les données financières chargées dans Données Prêt', () => {
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-pret').should('contain', 'Montant prêt')
    })

    it('devrait afficher les données de dates chargées', () => {
      cy.getByTestId('section-body-dates').should('be.visible')
      cy.getByTestId('section-body-dates').should('contain', "Date d'acceptation")
    })

    it('devrait ne pas afficher le bandeau d\'erreur par défaut', () => {
      cy.get('.error-banner').should('not.exist')
    })

    it('devrait ne pas afficher l\'indicateur de chargement une fois terminé', () => {
      cy.get('.loading-indicator').should('not.exist')
    })
  })

  // ═══════════════════════════════════
  // État des sections après navigation
  // ═══════════════════════════════════
  describe('Persistance état sections lors de la navigation', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait conserver les sections fermées après navigation onglet → retour', () => {
      cy.toggleSection('pret')
      cy.getByTestId('section-body-pret').should('not.be.visible')

      cy.clickTab('donnees-financieres')
      cy.clickTab('donnees-generales')

      // La section reste fermée car le store est partagé
      cy.getByTestId('section-body-pret').should('not.be.visible')
    })

    it('devrait conserver les sections ouvertes après navigation sidebar', () => {
      cy.getByTestId('section-body-general').should('be.visible')

      cy.clickSidebarItem('deblocage')
      cy.visitConsultation('DOSS-2024-001')

      cy.getByTestId('section-body-general').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Dark mode (état global)
  // ═══════════════════════════════════
  describe('Dark mode - persistance état', () => {
    beforeEach(() => {
      cy.visit('/recherche')
    })

    it('devrait conserver le dark mode après navigation sidebar', () => {
      cy.toggleDarkMode()
      cy.get('html').should('have.class', 'dark-mode')

      cy.clickSidebarItem('deblocage')
      cy.get('html').should('have.class', 'dark-mode')

      cy.clickSidebarItem('recherche')
      cy.get('html').should('have.class', 'dark-mode')
    })

    it('devrait conserver le dark mode après changement d\'onglet', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.toggleDarkMode()
      cy.clickTab('paliers')
      cy.get('html').should('have.class', 'dark-mode')
      cy.clickTab('donnees-generales')
      cy.get('html').should('have.class', 'dark-mode')
    })
  })

  // ═══════════════════════════════════
  // Sidebar collapse - persistance état
  // ═══════════════════════════════════
  describe('Sidebar collapse - persistance état', () => {
    beforeEach(() => {
      cy.visit('/recherche')
    })

    it('devrait conserver la sidebar réduite après navigation', () => {
      cy.toggleSidebar()
      cy.getByTestId('sidebar').should('have.class', 'collapsed')

      cy.clickSidebarItem('deblocage')
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })

    it('devrait conserver la sidebar réduite après changement d\'onglet', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.toggleSidebar()
      cy.clickTab('paliers')
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })
  })
})
