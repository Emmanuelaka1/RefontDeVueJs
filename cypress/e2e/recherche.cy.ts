describe('Recherche de dossiers', () => {
  beforeEach(() => {
    cy.visit('/recherche')
  })

  // ═══════════════════════════════════
  // État initial
  // ═══════════════════════════════════
  describe('État initial', () => {
    it('devrait afficher la vue de recherche', () => {
      cy.getByTestId('recherche-view').should('be.visible')
    })

    it('devrait afficher le titre "Recherche de dossiers"', () => {
      cy.get('.view-title').should('contain', 'Recherche de dossiers')
    })

    it('devrait afficher le sous-titre explicatif', () => {
      cy.get('.view-subtitle').should('contain', 'numéro de contrat souscrit')
    })

    it('devrait afficher le formulaire de recherche', () => {
      cy.get('.search-form').should('be.visible')
      cy.get('.field-input').should('be.visible')
    })

    it('devrait afficher le placeholder du champ de saisie', () => {
      cy.get('.field-input').should(
        'have.attr',
        'placeholder',
        'Saisissez un numéro de contrat souscrit — Ex: DD04063627',
      )
    })

    it('devrait avoir le bouton Rechercher désactivé', () => {
      cy.get('.btn-search').should('be.disabled')
    })

    it("devrait afficher le message d'état initial", () => {
      cy.get('.initial-state').should('be.visible')
      cy.get('.initial-title').should('contain', 'Recherchez un dossier de prêt')
    })

    it('ne devrait pas afficher de résultats', () => {
      cy.get('.resultats-section').should('not.exist')
    })

    it("ne devrait pas afficher d'erreur", () => {
      cy.get('.error-banner').should('not.exist')
    })
  })

  // ═══════════════════════════════════
  // Saisie et activation du bouton
  // ═══════════════════════════════════
  describe('Saisie du numéro de prêt', () => {
    it('devrait activer le bouton Rechercher après saisie', () => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').should('not.be.disabled')
    })

    it('devrait désactiver le bouton si le champ est vidé', () => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').should('not.be.disabled')
      cy.get('.field-input').clear()
      cy.get('.btn-search').should('be.disabled')
    })

    it('devrait garder le bouton désactivé avec uniquement des espaces', () => {
      cy.get('.field-input').type('   ')
      cy.get('.btn-search').should('be.disabled')
    })
  })

  // ═══════════════════════════════════
  // Recherche avec résultats
  // ═══════════════════════════════════
  describe('Recherche réussie', () => {
    beforeEach(() => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').click()
      // Attendre les résultats async (mock 400ms + Transition fade)
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
    })

    it("devrait masquer l'état initial après la recherche", () => {
      cy.get('.initial-state').should('not.exist')
    })

    it('devrait afficher la section résultats', () => {
      cy.get('.resultats-section').should('be.visible')
    })

    it('devrait afficher le compteur de résultats', () => {
      cy.get('.resultats-count').should('be.visible')
      cy.get('.resultats-count').should('contain', 'dossier')
    })

    it('devrait afficher les en-têtes du tableau', () => {
      cy.get('.resultats-table th').should('contain', 'N° Prêt')
      cy.get('.resultats-table th').should('contain', 'Emprunteur')
      cy.get('.resultats-table th').should('contain', 'Montant')
      cy.get('.resultats-table th').should('contain', 'État')
      cy.get('.resultats-table th').should('contain', 'Action')
    })

    it('devrait afficher les lignes de résultats', () => {
      cy.get('.result-row').should('have.length.greaterThan', 0)
    })

    it('devrait afficher le bouton "Ouvrir" sur chaque résultat', () => {
      cy.get('.btn-consulter').first().should('be.visible')
      cy.get('.btn-consulter-label').first().should('contain', 'Ouvrir')
    })
  })

  // ═══════════════════════════════════
  // Recherche par touche Entrée
  // ═══════════════════════════════════
  describe('Recherche par touche Entrée', () => {
    it('devrait lancer la recherche en appuyant sur Entrée', () => {
      cy.get('.field-input').type('DOSS-2024-001{enter}')
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Navigation vers consultation
  // ═══════════════════════════════════
  describe('Navigation vers consultation', () => {
    beforeEach(() => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').click()
      // Attendre les résultats async
      cy.get('.result-row', { timeout: 5000 }).should('be.visible')
    })

    it('devrait naviguer au clic sur une ligne de résultat', () => {
      cy.get('.result-row').first().click()
      cy.url().should('include', '/consultation/')
      cy.url().should('include', '/donnees-generales')
    })

    it('devrait naviguer au clic sur le bouton "Ouvrir"', () => {
      cy.get('.btn-consulter').first().click()
      cy.url().should('include', '/consultation/')
      cy.url().should('include', '/donnees-generales')
    })

    it('devrait afficher la vue consultation après navigation', () => {
      cy.get('.result-row').first().click()
      cy.getByTestId('consultation-view').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Réinitialisation
  // ═══════════════════════════════════
  describe('Réinitialisation', () => {
    it('devrait vider le champ et les résultats au clic sur reset', () => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').click()
      // Attendre les résultats async
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')

      cy.get('.btn-reset').click()
      cy.get('.field-input').should('have.value', '')
      cy.get('.resultats-section').should('not.exist')
      cy.get('.initial-state').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // État "aucun résultat" — UI vérification
  // Note: En mock mode, listerDossiers() retourne toujours des résultats.
  // Ces tests vérifient le comportement de l'UI avec le mock.
  // ═══════════════════════════════════
  describe('Recherche avec résultats (mock retourne toujours des dossiers)', () => {
    it('devrait toujours retourner des résultats en mode mock', () => {
      cy.get('.field-input').type('INEXISTANT-XYZ')
      cy.get('.btn-search').click()
      // Le mock ne filtre pas : listerDossiers() retourne tout
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
      cy.get('.result-row').should('have.length.greaterThan', 0)
    })

    it('devrait afficher la section résultats même avec un ID inconnu', () => {
      cy.get('.field-input').type('AUCUN-MATCH')
      cy.get('.btn-search').click()
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
    })

    it('devrait permettre de relancer une recherche après résultats', () => {
      cy.get('.field-input').type('TEST-123')
      cy.get('.btn-search').click()
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
      cy.get('.btn-reset').click()
      cy.get('.initial-state').should('be.visible')
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').click()
      cy.get('.resultats-section', { timeout: 5000 }).should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Persistance de l'état de recherche
  // ═══════════════════════════════════
  describe('Persistance état recherche', () => {
    it('devrait conserver les résultats après navigation consultation → retour', () => {
      cy.get('.field-input').type('DOSS-2024-001')
      cy.get('.btn-search').click()
      // Attendre les résultats async
      cy.get('.result-row', { timeout: 5000 }).should('be.visible')
      cy.get('.result-row').first().click()
      cy.getByTestId('consultation-view').should('be.visible')

      // Retour vers la recherche
      cy.contains('button', 'Retour recherche').click()
      cy.getByTestId('recherche-view').should('be.visible')

      // Les résultats sont toujours visibles
      cy.get('.resultats-section').should('be.visible')
      cy.get('.field-input').should('have.value', 'DOSS-2024-001')
    })
  })
})
