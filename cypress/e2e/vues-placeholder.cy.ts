describe('Vues Placeholder', () => {
  // ═══════════════════════════════════
  // Données Financières
  // ═══════════════════════════════════
  describe('Onglet Données Financières', () => {
    beforeEach(() => {
      cy.visit('/consultation/donnees-financieres')
    })

    it('devrait afficher la vue "Données financières"', () => {
      cy.getByTestId('donnees-financieres-view').should('be.visible')
    })

    it('devrait afficher le titre "Données financières"', () => {
      cy.getByTestId('donnees-financieres-view').should('contain', 'Données financières')
    })

    it('devrait afficher le badge "À venir"', () => {
      cy.getByTestId('donnees-financieres-view').should('contain', 'À venir')
    })

    it('devrait marquer l\'onglet "Données financières" comme actif', () => {
      cy.getByTestId('tab-donnees-financieres').should('have.class', 'active')
    })

    it('devrait afficher le layout complet (toolbar, sidebar, tabs)', () => {
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('sidebar').should('be.visible')
      cy.getByTestId('tabs-nav').should('be.visible')
    })

    it('devrait mettre à jour le titre de page', () => {
      cy.title().should('include', 'Données financières')
    })
  })

  // ═══════════════════════════════════
  // Paliers
  // ═══════════════════════════════════
  describe('Onglet Paliers', () => {
    beforeEach(() => {
      cy.visit('/consultation/paliers')
    })

    it('devrait afficher la vue "Paliers"', () => {
      cy.getByTestId('paliers-view').should('be.visible')
    })

    it('devrait afficher le titre "Paliers"', () => {
      cy.getByTestId('paliers-view').should('contain', 'Paliers')
    })

    it('devrait afficher le badge "À venir"', () => {
      cy.getByTestId('paliers-view').should('contain', 'À venir')
    })

    it('devrait marquer l\'onglet "Paliers" comme actif', () => {
      cy.getByTestId('tab-paliers').should('have.class', 'active')
    })

    it('devrait mettre à jour le titre de page', () => {
      cy.title().should('include', 'Paliers')
    })
  })

  // ═══════════════════════════════════
  // Domiciliation
  // ═══════════════════════════════════
  describe('Onglet Domiciliation', () => {
    beforeEach(() => {
      cy.visit('/consultation/domiciliation')
    })

    it('devrait afficher la vue "Domiciliation"', () => {
      cy.getByTestId('domiciliation-view').should('be.visible')
    })

    it('devrait afficher le titre "Domiciliation"', () => {
      cy.getByTestId('domiciliation-view').should('contain', 'Domiciliation')
    })

    it('devrait afficher le badge "À venir"', () => {
      cy.getByTestId('domiciliation-view').should('contain', 'À venir')
    })

    it('devrait marquer l\'onglet "Domiciliation" comme actif', () => {
      cy.getByTestId('tab-domiciliation').should('have.class', 'active')
    })

    it('devrait mettre à jour le titre de page', () => {
      cy.title().should('include', 'Domiciliation')
    })
  })

  // ═══════════════════════════════════
  // Déblocage (sidebar)
  // ═══════════════════════════════════
  describe('Section Déblocage', () => {
    beforeEach(() => {
      cy.visit('/deblocage')
    })

    it('devrait afficher la vue "Déblocage"', () => {
      cy.getByTestId('deblocage-view').should('be.visible')
    })

    it('devrait afficher le titre "Déblocage"', () => {
      cy.getByTestId('deblocage-view').should('contain', 'Déblocage')
    })

    it('devrait afficher le badge "À venir"', () => {
      cy.getByTestId('deblocage-view').should('contain', 'À venir')
    })

    it('devrait afficher le layout complet', () => {
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('sidebar').should('be.visible')
    })

    it('devrait afficher la description de la vue', () => {
      cy.getByTestId('deblocage-view').should('contain', 'gestion des déblocages')
    })
  })

  // ═══════════════════════════════════
  // Remboursements anticipés (sidebar)
  // ═══════════════════════════════════
  describe('Section Remboursements anticipés', () => {
    beforeEach(() => {
      cy.visit('/rbt-anticipes')
    })

    it('devrait afficher la vue "Remboursements anticipés"', () => {
      cy.getByTestId('rbt-anticipes-view').should('be.visible')
    })

    it('devrait afficher le titre "Remboursements anticipés"', () => {
      cy.getByTestId('rbt-anticipes-view').should('contain', 'Remboursements anticipés')
    })

    it('devrait afficher le badge "À venir"', () => {
      cy.getByTestId('rbt-anticipes-view').should('contain', 'À venir')
    })

    it('devrait afficher le layout complet', () => {
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('sidebar').should('be.visible')
    })

    it('devrait afficher la description de la vue', () => {
      cy.getByTestId('rbt-anticipes-view').should('contain', 'remboursements anticipés')
    })
  })

  // ═══════════════════════════════════
  // Routage
  // ═══════════════════════════════════
  describe('Routage', () => {
    it('devrait rediriger une route inconnue vers la page d\'accueil', () => {
      cy.visit('/route-inexistante')
      cy.url().should('include', '/consultation/donnees-generales')
      cy.getByTestId('consultation-view').should('be.visible')
    })

    it('devrait rediriger /consultation vers /consultation/donnees-generales', () => {
      cy.visit('/consultation')
      cy.url().should('include', '/consultation/donnees-generales')
    })

    it('devrait rediriger la racine / vers /consultation/donnees-generales', () => {
      cy.visit('/')
      cy.url().should('include', '/consultation/donnees-generales')
    })
  })
})
