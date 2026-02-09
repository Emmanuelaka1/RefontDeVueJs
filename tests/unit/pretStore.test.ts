import { describe, it, expect, beforeEach } from 'vitest'
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

    it('devrait avoir "consultation" comme item sidebar actif', () => {
      expect(store.activeSidebarItem).toBe('consultation')
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
  })
})
