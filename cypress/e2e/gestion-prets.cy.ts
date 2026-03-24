describe('Gestion des Prêts - Interface principale', () => {
  // ═══════════════════════════════════
  // Layout général (depuis la recherche)
  // ═══════════════════════════════════
  describe('Layout général', () => {
    beforeEach(() => {
      cy.visit('/recherche')
    })

    it('devrait afficher le layout principal', () => {
      cy.getByTestId('main-layout').should('be.visible')
    })

    it('devrait afficher la toolbar', () => {
      cy.getByTestId('toolbar').should('be.visible')
    })

    it('devrait afficher la sidebar', () => {
      cy.getByTestId('sidebar').should('be.visible')
    })

    it('devrait afficher la zone de contenu', () => {
      cy.getByTestId('content-area').should('be.visible')
    })

    it('devrait rediriger / vers /recherche', () => {
      cy.visit('/')
      cy.url().should('include', '/recherche')
    })

    it('devrait afficher la vue Recherche par défaut', () => {
      cy.getByTestId('recherche-view').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Sidebar
  // ═══════════════════════════════════
  describe('Sidebar', () => {
    beforeEach(() => {
      cy.visit('/recherche')
    })

    it('devrait avoir "Recherche" comme section active sur /recherche', () => {
      cy.getByTestId('sidebar-item-recherche').should('have.class', 'active')
    })

    it('devrait afficher les 4 items de navigation', () => {
      cy.getByTestId('sidebar-item-recherche').should('be.visible')
      cy.getByTestId('sidebar-item-consultation').should('be.visible')
      cy.getByTestId('sidebar-item-deblocage').should('be.visible')
      cy.getByTestId('sidebar-item-rbt-anticipes').should('be.visible')
    })

    it('devrait afficher les libellés', () => {
      cy.getByTestId('sidebar-item-recherche').should('contain', 'Recherche')
      cy.getByTestId('sidebar-item-consultation').should('contain', 'Consultation')
      cy.getByTestId('sidebar-item-deblocage').should('contain', 'Déblocage')
      cy.getByTestId('sidebar-item-rbt-anticipes').should('contain', 'Rbt anticipés')
    })

    it("devrait changer l'item actif au clic sur Déblocage", () => {
      cy.clickSidebarItem('deblocage')
      cy.getByTestId('sidebar-item-deblocage').should('have.class', 'active')
    })

    it('devrait rediriger vers /recherche au clic sur Consultation sans dossier courant', () => {
      cy.clickSidebarItem('consultation')
      cy.url().should('include', '/recherche')
    })
  })

  // ═══════════════════════════════════
  // Onglets (en consultation)
  // ═══════════════════════════════════
  describe('Onglets de navigation', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait afficher les onglets en consultation', () => {
      cy.getByTestId('tabs-nav').should('be.visible')
    })

    it('devrait avoir "Données générales" comme onglet actif par défaut', () => {
      cy.getByTestId('tab-donnees-generales').should('have.class', 'active')
    })

    it('devrait afficher les 4 onglets', () => {
      const tabLabels = ['Données générales', 'Données financières', 'Paliers', 'Domiciliation']
      tabLabels.forEach((label) => {
        cy.getByTestId('tabs-nav').should('contain', label)
      })
    })

    it('devrait naviguer vers "Données financières" au clic', () => {
      cy.clickTab('donnees-financieres')
      cy.url().should('include', '/donnees-financieres')
      cy.getByTestId('tab-donnees-financieres').should('have.class', 'active')
    })

    it('devrait naviguer vers "Paliers" au clic', () => {
      cy.clickTab('paliers')
      cy.url().should('include', '/paliers')
      cy.getByTestId('tab-paliers').should('have.class', 'active')
    })

    it('devrait naviguer vers "Domiciliation" au clic', () => {
      cy.clickTab('domiciliation')
      cy.url().should('include', '/domiciliation')
      cy.getByTestId('tab-domiciliation').should('have.class', 'active')
    })

    it('devrait revenir à "Données générales" au clic', () => {
      cy.clickTab('donnees-financieres')
      cy.clickTab('donnees-generales')
      cy.url().should('include', '/donnees-generales')
      cy.getByTestId('tab-donnees-generales').should('have.class', 'active')
    })
  })

  // ═══════════════════════════════════
  // Vue Consultation - Sections
  // ═══════════════════════════════════
  describe('Vue Consultation - Sections dépliables', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait afficher la vue de consultation', () => {
      cy.getByTestId('consultation-view').should('be.visible')
    })

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

    it('devrait fermer "Données Prêt" indépendamment des autres sections', () => {
      cy.toggleSection('pret')
      cy.getByTestId('section-body-pret').should('not.be.visible')
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-dates').should('be.visible')
    })

    it('devrait fermer "Dates" indépendamment des autres sections', () => {
      cy.toggleSection('dates')
      cy.getByTestId('section-body-dates').should('not.be.visible')
      cy.getByTestId('section-body-general').should('be.visible')
      cy.getByTestId('section-body-pret').should('be.visible')
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Données Générales
  // ═══════════════════════════════════
  describe('Formulaire - Données Générales', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait afficher les champs de la colonne gauche', () => {
      const labels = [
        'Emprunteur',
        'Co-emprunteur',
        'N° prêt',
        'N° contrat souscrit projet',
        'N° contrat souscrit prêt',
      ]
      labels.forEach((label) => {
        cy.getByTestId('section-body-general').should('contain', label)
      })
    })

    it('devrait afficher les champs de la colonne droite', () => {
      const labels = ['EFS', 'Structure', 'Code état', 'Code objet', 'Code nature']
      labels.forEach((label) => {
        cy.getByTestId('section-body-general').should('contain', label)
      })
    })

    it('devrait afficher les valeurs du dossier MARTIN', () => {
      cy.getByTestId('field-emprunteur').should('contain', 'MARTIN Jean-Pierre')
      cy.getByTestId('field-co-emprunteur').should('contain', 'MARTIN Catherine')
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Données Prêt
  // ═══════════════════════════════════
  describe('Formulaire - Données Prêt', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait afficher les champs financiers principaux', () => {
      const labels = [
        'Montant prêt',
        'Durée prêt',
        'Taux de remboursement',
        'Anticipation',
        "Type d'amortissement",
        'Montant disponible',
        'Montant RA',
        'Encours',
        'TEG',
      ]
      labels.forEach((label) => {
        cy.getByTestId('section-body-pret').should('contain', label)
      })
    })

    it('devrait afficher les valeurs financières du dossier', () => {
      cy.getByTestId('section-body-pret').should('contain', '250 000,00 €')
      cy.getByTestId('section-body-pret').should('contain', '240 mois')
      cy.getByTestId('section-body-pret').should('contain', '3,45 %')
    })
  })

  // ═══════════════════════════════════
  // Formulaire - Dates
  // ═══════════════════════════════════
  describe('Formulaire - Dates', () => {
    beforeEach(() => {
      cy.visitConsultation('DOSS-2024-001')
    })

    it('devrait afficher les dates principales', () => {
      const labels = [
        "Date d'acceptation",
        "Date d'accord",
        "Date d'ouverture de crédit",
        'Date de passage en gestion',
        "Date d'effet",
        "Date d'effet RA",
        'Date fin prêt',
      ]
      labels.forEach((label) => {
        cy.getByTestId('section-body-dates').should('contain', label)
      })
    })
  })

  // ═══════════════════════════════════
  // Titre de page (router guard)
  // ═══════════════════════════════════
  describe('Titre de page', () => {
    it('devrait contenir "Recherche" et "SIGAC" sur la page d\'accueil', () => {
      cy.visit('/recherche')
      cy.title().should('include', 'Recherche')
      cy.title().should('include', 'SIGAC')
    })

    it('devrait contenir "Données générales" en consultation', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.title().should('include', 'Données générales')
    })

    it('devrait mettre à jour le titre lors de la navigation vers "Données financières"', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.clickTab('donnees-financieres')
      cy.title().should('include', 'Données financières')
    })

    it('devrait mettre à jour le titre vers "Paliers"', () => {
      cy.visitConsultation('DOSS-2024-001')
      cy.clickTab('paliers')
      cy.title().should('include', 'Paliers')
    })
  })

  // ═══════════════════════════════════
  // Navigation complète
  // ═══════════════════════════════════
  describe('Parcours de navigation complet', () => {
    it('devrait permettre de parcourir tous les onglets et revenir', () => {
      cy.visitConsultation('DOSS-2024-001')

      cy.clickTab('donnees-financieres')
      cy.getByTestId('donnees-financieres-view').should('be.visible')

      cy.clickTab('paliers')
      cy.getByTestId('paliers-view').should('be.visible')

      cy.clickTab('domiciliation')
      cy.getByTestId('domiciliation-view').should('be.visible')

      cy.clickTab('donnees-generales')
      cy.getByTestId('consultation-view').should('be.visible')
      cy.getByTestId('section-general').should('be.visible')
    })
  })
})
