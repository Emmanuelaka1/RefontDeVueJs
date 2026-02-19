describe('Sidebar - Navigation entre sections', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  // ═══════════════════════════════════
  // Navigation vers Déblocage
  // ═══════════════════════════════════
  describe('Navigation vers Déblocage', () => {
    it('devrait naviguer vers /deblocage au clic sur "Déblocage"', () => {
      cy.clickSidebarItem('deblocage')
      cy.url().should('include', '/deblocage')
    })

    it('devrait afficher la vue Déblocage', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('deblocage-view').should('be.visible')
    })

    it('devrait marquer "Déblocage" comme item actif', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('sidebar-item-deblocage').should('have.class', 'active')
    })

    it('devrait afficher le titre "Déblocage" dans la vue', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('deblocage-view').should('contain', 'Déblocage')
    })

    it('devrait désactiver "Consultation" quand "Déblocage" est actif', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('sidebar-item-consultation').should('not.have.class', 'active')
    })
  })

  // ═══════════════════════════════════
  // Navigation vers Remboursements anticipés
  // ═══════════════════════════════════
  describe('Navigation vers Remboursements anticipés', () => {
    it('devrait naviguer vers /rbt-anticipes au clic', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.url().should('include', '/rbt-anticipes')
    })

    it('devrait afficher la vue Remboursements anticipés', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.getByTestId('rbt-anticipes-view').should('be.visible')
    })

    it('devrait marquer "Rbt anticipés" comme item actif', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.getByTestId('sidebar-item-rbt-anticipes').should('have.class', 'active')
    })

    it('devrait afficher le titre "Remboursements anticipés"', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.getByTestId('rbt-anticipes-view').should('contain', 'Remboursements anticipés')
    })
  })

  // ═══════════════════════════════════
  // Retour à Consultation
  // ═══════════════════════════════════
  describe('Retour à Consultation', () => {
    it('devrait revenir à /consultation après Déblocage', () => {
      cy.clickSidebarItem('deblocage')
      cy.clickSidebarItem('consultation')
      cy.url().should('include', '/consultation')
    })

    it('devrait ré-afficher la vue Consultation après retour', () => {
      cy.clickSidebarItem('deblocage')
      cy.clickSidebarItem('consultation')
      cy.getByTestId('consultation-view').should('be.visible')
    })

    it('devrait ré-afficher les sections de consultation', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.clickSidebarItem('consultation')
      cy.getByTestId('section-general').should('be.visible')
      cy.getByTestId('section-pret').should('be.visible')
      cy.getByTestId('section-dates').should('be.visible')
    })

    it('devrait conserver le layout (toolbar + tabs) sur toutes les pages', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('tabs-nav').should('be.visible')

      cy.clickSidebarItem('rbt-anticipes')
      cy.getByTestId('toolbar').should('be.visible')
      cy.getByTestId('tabs-nav').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Sidebar mode réduit
  // ═══════════════════════════════════
  describe('Sidebar en mode réduit', () => {
    beforeEach(() => {
      cy.toggleSidebar()
    })

    it('devrait ajouter la classe "collapsed" à la sidebar', () => {
      cy.getByTestId('sidebar').should('have.class', 'collapsed')
    })

    it('devrait garder les icônes des items visibles', () => {
      cy.getByTestId('sidebar-item-consultation').should('be.visible')
      cy.getByTestId('sidebar-item-deblocage').should('be.visible')
      cy.getByTestId('sidebar-item-rbt-anticipes').should('be.visible')
    })

    it('devrait permettre la navigation vers Déblocage en mode réduit', () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('deblocage-view').should('be.visible')
    })

    it('devrait permettre la navigation vers Rbt anticipés en mode réduit', () => {
      cy.clickSidebarItem('rbt-anticipes')
      cy.getByTestId('rbt-anticipes-view').should('be.visible')
    })
  })
})
