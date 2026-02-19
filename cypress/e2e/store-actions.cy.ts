describe('Store - Actions et état', () => {
  beforeEach(() => {
    cy.visit('/consultation/donnees-generales')
  })

  // ═══════════════════════════════════
  // Chargement du dossier (chargerDossier)
  // ═══════════════════════════════════
  describe('Chargement du dossier', () => {
    it('devrait charger et afficher les données du premier dossier', () => {
      // Le dossier se charge automatiquement onMounted via le mock service
      cy.getByTestId('field-emprunteur').should('be.visible')
    })

    it('devrait afficher les données financières chargées dans Données Prêt', () => {
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-pret').should('contain', 'Montant prêt')
    })

    it('devrait afficher les données de dates chargées', () => {
      cy.getByTestId('section-body-dates').should('be.visible')
      cy.getByTestId('section-body-dates').should("contain", "Date d'acceptation")
    })

    it('devrait afficher le select de dossiers', () => {
      cy.get('.dossier-select').should('be.visible')
    })

    it('devrait ne pas afficher le bandeau d\'erreur par défaut', () => {
      cy.get('.error-banner').should('not.exist')
    })

    it('devrait ne pas afficher l\'indicateur de chargement une fois terminé', () => {
      cy.get('.loading-indicator').should('not.exist')
    })
  })

  // ═══════════════════════════════════
  // Sélecteur de dossier
  // ═══════════════════════════════════
  describe('Sélecteur de dossier', () => {
    it('devrait changer de dossier au changement de select', () => {
      cy.get('.dossier-select').then(($select) => {
        const options = $select.find('option')
        if (options.length > 1) {
          // Sélectionner le deuxième dossier
          cy.get('.dossier-select').select(1)
          cy.get('.loading-indicator').should('not.exist')
          cy.getByTestId('section-body-general').should('be.visible')
        }
      })
    })
  })

  // ═══════════════════════════════════
  // État des sections après navigation
  // ═══════════════════════════════════
  describe('Persistance état sections lors de la navigation', () => {
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
      cy.clickSidebarItem('consultation')

      cy.getByTestId('section-body-general').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Dark mode (état global)
  // ═══════════════════════════════════
  describe('Dark mode - persistance état', () => {
    it('devrait conserver le dark mode après navigation', () => {
      cy.toggleDarkMode()
      cy.get('html').should('have.class', 'dark-mode')

      cy.clickSidebarItem('deblocage')
      cy.get('html').should('have.class', 'dark-mode')

      cy.clickSidebarItem('consultation')
      cy.get('html').should('have.class', 'dark-mode')
    })

    it('devrait conserver le dark mode après changement d\'onglet', () => {
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
    it('devrait conserver la sidebar réduite après navigation', () => {
      cy.toggleSidebar()
      cy.getByTestId('sidebar').should('have.class', 'collapsed')

      cy.clickSidebarItem('deblocage')
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })

    it('devrait conserver la sidebar réduite après changement d\'onglet', () => {
      cy.toggleSidebar()
      cy.clickTab('paliers')
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })
  })
})
