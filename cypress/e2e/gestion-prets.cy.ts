describe('Gestion des Prêts - Interface principale', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  // ═══════════════════════════════════
  // Navigation & Layout
  // ═══════════════════════════════════
  describe('Layout général', () => {
    it('devrait afficher le layout principal', () => {
      cy.getByTestId('main-layout').should('be.visible')
    })

    it('devrait afficher la sidebar', () => {
      cy.getByTestId('sidebar').should('be.visible')
    })

    it('devrait afficher les onglets de navigation', () => {
      cy.getByTestId('tabs-nav').should('be.visible')
    })

    it('devrait afficher la zone de contenu', () => {
      cy.getByTestId('content-area').should('be.visible')
    })

    it('devrait rediriger vers /dossier/consultation par défaut', () => {
      cy.url().should('include', '/dossier/consultation')
    })
  })

  // ═══════════════════════════════════
  // Sidebar
  // ═══════════════════════════════════
  describe('Sidebar', () => {
    it('devrait avoir "Dossier" comme item actif par défaut', () => {
      cy.getByTestId('sidebar-item-dossier').should('have.class', 'active')
    })

    it('devrait afficher les 2 items de sidebar', () => {
      cy.getByTestId('sidebar-item-dossier').should('contain', 'Dossier')
      cy.getByTestId('sidebar-item-nul-entierge').should('contain', 'Nul entièrge')
    })

    it('devrait changer l\'item actif au clic', () => {
      cy.clickSidebarItem('nul-entierge')
      cy.getByTestId('sidebar-item-nul-entierge').should('have.class', 'active')
    })
  })

  // ═══════════════════════════════════
  // Onglets
  // ═══════════════════════════════════
  describe('Onglets de navigation', () => {
    it('devrait avoir "Consultation" comme onglet actif par défaut', () => {
      cy.getByTestId('tab-consultation').should('have.class', 'active')
    })

    it('devrait afficher les 5 onglets', () => {
      const tabLabels = [
        'Consultation',
        'Données détaillées',
        'Données comptables',
        'Préavis',
        'Recouvrement',
      ]

      tabLabels.forEach((label) => {
        cy.getByTestId('tabs-nav').should('contain', label)
      })
    })

    it('devrait naviguer vers "Données détaillées" au clic', () => {
      cy.clickTab('donnees-detaillees')
      cy.url().should('include', '/dossier/donnees-detaillees')
      cy.getByTestId('tab-donnees-detaillees').should('have.class', 'active')
    })

    it('devrait naviguer vers "Données comptables" au clic', () => {
      cy.clickTab('donnees-comptables')
      cy.url().should('include', '/dossier/donnees-comptables')
      cy.getByTestId('tab-donnees-comptables').should('have.class', 'active')
    })

    it('devrait naviguer vers "Préavis" au clic', () => {
      cy.clickTab('preavis')
      cy.url().should('include', '/dossier/preavis')
      cy.getByTestId('tab-preavis').should('have.class', 'active')
    })

    it('devrait naviguer vers "Recouvrement" au clic', () => {
      cy.clickTab('recouvrement')
      cy.url().should('include', '/dossier/recouvrement')
      cy.getByTestId('tab-recouvrement').should('have.class', 'active')
    })

    it('devrait revenir à "Consultation" au clic', () => {
      cy.clickTab('donnees-detaillees')
      cy.clickTab('consultation')
      cy.url().should('include', '/dossier/consultation')
      cy.getByTestId('tab-consultation').should('have.class', 'active')
    })
  })

  // ═══════════════════════════════════
  // Vue Consultation - Sections
  // ═══════════════════════════════════
  describe('Vue Consultation - Sections dépliables', () => {
    it('devrait afficher les 3 sections', () => {
      cy.getByTestId('section-general').should('be.visible')
      cy.getByTestId('section-pret').should('be.visible')
      cy.getByTestId('section-dates').should('be.visible')
    })

    it('devrait avoir les sections ouvertes par défaut', () => {
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait fermer "Données Générales" au clic sur le header', () => {
      cy.toggleSection('general')
      cy.getByTestId('section-body-general').should('not.be.visible')
    })

    it('devrait ré-ouvrir "Données Générales" au second clic', () => {
      cy.toggleSection('general')
      cy.getByTestId('section-body-general').should('not.be.visible')

      cy.toggleSection('general')
      cy.getByTestId('section-body-general').should('be.visible')
    })

    it('devrait fermer/ouvrir "Données Prêt" indépendamment', () => {
      cy.toggleSection('pret')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Données Générales
  // ═══════════════════════════════════
  describe('Formulaire - Données Générales', () => {
    it('devrait afficher les champs de la colonne gauche', () => {
      const labels = [
        'Libéllation',
        'Co-emprunteur',
        'N° Prêt',
        'N° Contrat source groupe',
        'N° Contrat source prêt',
      ]

      labels.forEach((label) => {
        cy.getByTestId('section-body-general').should('contain', label)
      })
    })

    it('devrait afficher les champs de la colonne droite', () => {
      const labels = ['DPR', 'Structure', 'Code filial', 'Code objet', 'Code nature']

      labels.forEach((label) => {
        cy.getByTestId('section-body-general').should('contain', label)
      })
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Données Prêt
  // ═══════════════════════════════════
  describe('Formulaire - Données Prêt', () => {
    it('devrait afficher tous les champs financiers', () => {
      const labels = [
        'Montant prêt',
        'Périodicité',
        'Montant anticipé',
        'Durée prêt',
        'Taux d\'amortissement',
        'Montant disponible',
        'Encours RA',
        'Encours',
        'TEG',
      ]

      labels.forEach((label) => {
        cy.getByTestId('section-body-pret').should('contain', label)
      })
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Dates
  // ═══════════════════════════════════
  describe('Formulaire - Dates', () => {
    it('devrait afficher toutes les dates', () => {
      const labels = [
        'Date d\'acceptation',
        'Date d\'atout',
        'Date d\'effet RA',
        'Date crédit',
        'Date d\'effet',
      ]

      labels.forEach((label) => {
        cy.getByTestId('section-body-dates').should('contain', label)
      })
    })
  })

  // ═══════════════════════════════════
  // Navigation complète
  // ═══════════════════════════════════
  describe('Parcours de navigation complet', () => {
    it('devrait permettre de parcourir tous les onglets et revenir', () => {
      // Consultation → Données détaillées
      cy.clickTab('donnees-detaillees')
      cy.getByTestId('donnees-detaillees-view').should('be.visible')

      // → Données comptables
      cy.clickTab('donnees-comptables')
      cy.getByTestId('donnees-comptables-view').should('be.visible')

      // → Préavis
      cy.clickTab('preavis')
      cy.getByTestId('preavis-view').should('be.visible')

      // → Recouvrement
      cy.clickTab('recouvrement')
      cy.getByTestId('recouvrement-view').should('be.visible')

      // Retour Consultation
      cy.clickTab('consultation')
      cy.getByTestId('consultation-view').should('be.visible')
      cy.getByTestId('section-general').should('be.visible')
    })
  })
})
