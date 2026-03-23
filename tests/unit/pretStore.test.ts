import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { usePretStore } from '@/stores/pretStore'

describe('pretStore', () => {
  let store: ReturnType<typeof usePretStore>

  beforeEach(() => {
    setActivePinia(createPinia())
    store = usePretStore()
  })

  // ── State initial ──
  describe('état initial', () => {
    it('devrait avoir les sections ouvertes par défaut', () => {
      expect(store.sections.general).toBe(true)
      expect(store.sections.pret).toBe(true)
      expect(store.sections.dates).toBe(true)
    })

    it('devrait avoir "recherche" comme item sidebar actif', () => {
      expect(store.activeSidebarItem).toBe('recherche')
    })

    it('devrait avoir "donnees-generales" comme onglet actif', () => {
      expect(store.activeTab).toBe('donnees-generales')
    })

    it('devrait avoir les données générales vides', () => {
      expect(store.donneesGenerales.emprunteur).toBe('')
      expect(store.donneesGenerales.noPret).toBe('')
      expect(store.donneesGenerales.efs).toBe('')
    })

    it('devrait avoir les données prêt vides', () => {
      expect(store.donneesPret.montantPret).toBe('')
      expect(store.donneesPret.teg).toBe('')
    })

    it('devrait avoir les dates vides', () => {
      expect(store.datesPret.dateAcceptation).toBe('')
      expect(store.datesPret.dateEffet).toBe('')
    })

    it('ne devrait pas être en chargement', () => {
      expect(store.loading).toBe(false)
    })

    it('ne devrait pas avoir d\'erreur', () => {
      expect(store.error).toBeNull()
    })

    it('devrait avoir l\'état de recherche vide', () => {
      expect(store.rechercheNumeroPret).toBe('')
      expect(store.rechercheResultats).toEqual([])
      expect(store.rechercheLancee).toBe(false)
      expect(store.rechercheErreur).toBeNull()
    })
  })

  // ── toggleSection ──
  describe('toggleSection', () => {
    it('devrait fermer une section ouverte', () => {
      store.toggleSection('general')
      expect(store.sections.general).toBe(false)
    })

    it('devrait ouvrir une section fermée', () => {
      store.sections.pret = false
      store.toggleSection('pret')
      expect(store.sections.pret).toBe(true)
    })

    it('devrait toggle indépendamment chaque section', () => {
      store.toggleSection('general')
      store.toggleSection('dates')

      expect(store.sections.general).toBe(false)
      expect(store.sections.pret).toBe(true)
      expect(store.sections.dates).toBe(false)
    })
  })

  // ── expandAllSections / collapseAllSections ──
  describe('expandAll / collapseAll', () => {
    it('devrait ouvrir toutes les sections', () => {
      store.sections.general = false
      store.sections.pret = false
      store.sections.dates = false

      store.expandAllSections()

      expect(store.sections.general).toBe(true)
      expect(store.sections.pret).toBe(true)
      expect(store.sections.dates).toBe(true)
    })

    it('devrait fermer toutes les sections', () => {
      store.collapseAllSections()

      expect(store.sections.general).toBe(false)
      expect(store.sections.pret).toBe(false)
      expect(store.sections.dates).toBe(false)
    })
  })

  // ── setActiveTab ──
  describe('setActiveTab', () => {
    it('devrait changer l\'onglet actif', () => {
      store.setActiveTab('donnees-financieres')
      expect(store.activeTab).toBe('donnees-financieres')
    })
  })

  // ── setActiveSidebarItem ──
  describe('setActiveSidebarItem', () => {
    it('devrait changer l\'item sidebar actif', () => {
      store.setActiveSidebarItem('deblocage')
      expect(store.activeSidebarItem).toBe('deblocage')
    })
  })

  // ── toggleSidebar ──
  describe('toggleSidebar', () => {
    it('devrait collapse la sidebar', () => {
      expect(store.sidebarCollapsed).toBe(false)
      store.toggleSidebar()
      expect(store.sidebarCollapsed).toBe(true)
    })

    it('devrait expand la sidebar', () => {
      store.sidebarCollapsed = true
      store.toggleSidebar()
      expect(store.sidebarCollapsed).toBe(false)
    })
  })

  // ── toggleDarkMode ──
  describe('toggleDarkMode', () => {
    it('devrait activer le dark mode', () => {
      expect(store.darkMode).toBe(false)
      store.toggleDarkMode()
      expect(store.darkMode).toBe(true)
    })

    it('devrait désactiver le dark mode', () => {
      store.darkMode = true
      store.toggleDarkMode()
      expect(store.darkMode).toBe(false)
    })
  })

  // ── État recherche (persisté) ──
  describe('état recherche', () => {
    it('devrait persister le numéro de prêt recherché', () => {
      store.rechercheNumeroPret = 'DD04063627'
      expect(store.rechercheNumeroPret).toBe('DD04063627')
    })

    it('devrait persister les résultats de recherche', () => {
      const resultats = [
        { id: 'DD04063627', noPret: 'DD04063627', emprunteur: 'MARTIN', montantPret: '250 000 €', codeEtat: 'AA' },
      ]
      store.rechercheResultats = resultats
      expect(store.rechercheResultats).toHaveLength(1)
      expect(store.rechercheResultats[0].id).toBe('DD04063627')
    })

    it('devrait persister l\'état rechercheLancee', () => {
      store.rechercheLancee = true
      expect(store.rechercheLancee).toBe(true)
    })

    it('devrait persister l\'erreur de recherche', () => {
      store.rechercheErreur = 'Prêt introuvable'
      expect(store.rechercheErreur).toBe('Prêt introuvable')
    })
  })

  // ── chargerDossier ──
  describe('chargerDossier', () => {
    it('devrait remplir les champs réactifs avec les données mock', async () => {
      await store.chargerDossier('DOSS-2024-001')

      expect(store.dossierCourant).not.toBeNull()
      expect(store.donneesGenerales.emprunteur).toBe('MARTIN Jean-Pierre')
      expect(store.donneesPret.montantPret).toBe('250 000,00 €')
      expect(store.datesPret.dateAcceptation).toBe('15/01/2024')
      expect(store.loading).toBe(false)
      expect(store.error).toBeNull()
    })

    it('devrait positionner l\'erreur si ID introuvable', async () => {
      await store.chargerDossier('INEXISTANT')

      expect(store.error).toBeTruthy()
      expect(store.dossierCourant).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('devrait gérer une exception du service (catch)', async () => {
      // Mock getPretService pour qu'il retourne un service qui throw
      const pretServiceModule = await import('@/services/pretService')
      vi.spyOn(pretServiceModule, 'getPretService').mockResolvedValue({
        getDossier: () => { throw new Error('Erreur inattendue') },
        listerDossiers: vi.fn(),
      } as any)

      await store.chargerDossier('DOSS-2024-001')

      expect(store.error).toBe('Erreur lors du chargement du dossier')
      expect(store.loading).toBe(false)

      vi.restoreAllMocks()
    })
  })

  // ── resetFormulaire ──
  describe('resetFormulaire', () => {
    it('devrait vider toutes les données générales', () => {
      store.donneesGenerales.emprunteur = 'Test'
      store.donneesGenerales.noPret = '12345'

      store.resetFormulaire()

      expect(store.donneesGenerales.emprunteur).toBe('')
      expect(store.donneesGenerales.noPret).toBe('')
    })

    it('devrait vider toutes les données prêt', () => {
      store.donneesPret.montantPret = '50000'
      store.donneesPret.teg = '3.5'

      store.resetFormulaire()

      expect(store.donneesPret.montantPret).toBe('')
      expect(store.donneesPret.teg).toBe('')
    })

    it('devrait vider toutes les dates', () => {
      store.datesPret.dateAcceptation = '2024-01-01'

      store.resetFormulaire()

      expect(store.datesPret.dateAcceptation).toBe('')
    })

    it('devrait vider les données après un chargement', async () => {
      await store.chargerDossier('DOSS-2024-001')
      expect(store.donneesGenerales.emprunteur).toBe('MARTIN Jean-Pierre')

      store.resetFormulaire()

      expect(store.donneesGenerales.emprunteur).toBe('')
      expect(store.donneesPret.montantPret).toBe('')
      expect(store.datesPret.dateAcceptation).toBe('')
    })
  })
})
