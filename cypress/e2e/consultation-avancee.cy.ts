describe('Vue Consultation - Fonctionnalités avancées', () => {
  beforeEach(() => {
    cy.visitConsultation('DOSS-2024-001')
  })

  // ═══════════════════════════════════
  // En-tête de la vue
  // ═══════════════════════════════════
  describe('En-tête de consultation', () => {
    it('devrait afficher le titre "Consultation du dossier"', () => {
      cy.getByTestId('consultation-view').should('contain', 'Consultation du dossier')
    })

    it('devrait afficher le badge de statut', () => {
      cy.get('.badge-status').should('be.visible')
    })

    it('devrait afficher le bouton "Retour recherche"', () => {
      cy.contains('button', 'Retour recherche').should('be.visible')
    })

    it('devrait afficher le bouton "Tout ouvrir"', () => {
      cy.contains('button', 'Tout ouvrir').should('be.visible')
    })

    it('devrait afficher le bouton "Tout fermer"', () => {
      cy.contains('button', 'Tout fermer').should('be.visible')
    })

    it('devrait naviguer vers /recherche au clic sur "Retour recherche"', () => {
      cy.contains('button', 'Retour recherche').click()
      cy.url().should('include', '/recherche')
      cy.getByTestId('recherche-view').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Titres des sections
  // ═══════════════════════════════════
  describe('Titres des sections', () => {
    it('devrait afficher le titre "Données Générales" dans le header', () => {
      cy.getByTestId('section-header-general').should('contain', 'Données Générales')
    })

    it('devrait afficher le titre "Données Prêt" dans le header', () => {
      cy.getByTestId('section-header-pret').should('contain', 'Données Prêt')
    })

    it('devrait afficher le titre "Dates" dans le header', () => {
      cy.getByTestId('section-header-dates').should('contain', 'Dates')
    })
  })

  // ═══════════════════════════════════
  // Bouton "Tout ouvrir"
  // ═══════════════════════════════════
  describe('Bouton "Tout ouvrir"', () => {
    it('devrait ouvrir toutes les sections fermées', () => {
      cy.toggleSection('general')
      cy.toggleSection('pret')
      cy.getByTestId('section-body-general').should('not.be.visible')
      cy.getByTestId('section-body-pret').should('not.be.visible')

      cy.contains('button', 'Tout ouvrir').click()

      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait être idempotent si toutes les sections sont déjà ouvertes', () => {
      cy.contains('button', 'Tout ouvrir').click()
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Bouton "Tout fermer"
  // ═══════════════════════════════════
  describe('Bouton "Tout fermer"', () => {
    it('devrait fermer toutes les sections ouvertes', () => {
      cy.contains('button', 'Tout fermer').click()

      cy.getByTestId('section-body-general').should('not.be.visible')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-dates').should('not.be.visible')
    })

    it('devrait être idempotent si toutes les sections sont déjà fermées', () => {
      cy.contains('button', 'Tout fermer').click()
      cy.contains('button', 'Tout fermer').click()

      cy.getByTestId('section-body-general').should('not.be.visible')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-dates').should('not.be.visible')
    })
  })

  // ═══════════════════════════════════
  // Combinaisons Tout fermer / Tout ouvrir
  // ═══════════════════════════════════
  describe('Combinaisons ouverture/fermeture globale', () => {
    it('devrait rouvrir toutes les sections après "Tout fermer"', () => {
      cy.contains('button', 'Tout fermer').click()
      cy.getByTestId('section-body-general').should('not.be.visible')

      cy.contains('button', 'Tout ouvrir').click()
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait conserver le contenu des sections après toggle multiple', () => {
      cy.contains('button', 'Tout fermer').click()
      cy.contains('button', 'Tout ouvrir').click()
      cy.contains('button', 'Tout fermer').click()
      cy.contains('button', 'Tout ouvrir').click()

      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-general').should('contain', 'Emprunteur')
    })
  })

  // ═══════════════════════════════════
  // Champs avec test-id spécifiques
  // ═══════════════════════════════════
  describe('FormField - accès par test-id', () => {
    it('devrait trouver le champ Emprunteur par test-id', () => {
      cy.getByTestId('field-emprunteur').should('be.visible')
    })

    it('devrait trouver le champ Co-emprunteur par test-id', () => {
      cy.getByTestId('field-co-emprunteur').should('be.visible')
    })

    it('devrait trouver le champ N° prêt par test-id', () => {
      cy.getByTestId('field-no-pret').should('be.visible')
    })

    it('devrait trouver le champ EFS par test-id', () => {
      cy.getByTestId('field-efs').should('be.visible')
    })

    it('devrait trouver le champ Code état par test-id', () => {
      cy.getByTestId('field-code-etat').should('be.visible')
    })

    it('devrait masquer les champs quand la section est fermée', () => {
      cy.toggleSection('general')
      cy.getByTestId('field-emprunteur').should('not.be.visible')
    })
  })

  // ═══════════════════════════════════
  // Toggle indépendant des sections
  // ═══════════════════════════════════
  describe('Toggle indépendant des sections', () => {
    it('devrait fermer "Données Générales" sans affecter les autres', () => {
      cy.toggleSection('general')
      cy.getByTestId('section-body-general').should('not.be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait fermer "Données Prêt" sans affecter les autres', () => {
      cy.toggleSection('pret')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait fermer "Dates" sans affecter les autres', () => {
      cy.toggleSection('dates')
      cy.getByTestId('section-body-dates').should('not.be.visible')
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
    })

    it('devrait permettre de fermer toutes les sections individuellement', () => {
      cy.toggleSection('general')
      cy.toggleSection('pret')
      cy.toggleSection('dates')

      cy.getByTestId('section-body-general').should('not.be.visible')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-dates').should('not.be.visible')
    })
  })
})
