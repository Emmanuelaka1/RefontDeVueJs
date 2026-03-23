import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock vue-router — simule la consultation d'un dossier avec :id
const mockPush = vi.fn()
const mockRoute = {
  meta: { tabId: 'donnees-generales', section: 'consultation' },
  params: { id: 'DOSS-2024-001' },
}

vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
  useRouter: () => ({ push: mockPush }),
}))

import { useNavigation } from '@/composables/useNavigation'

describe('useNavigation', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockPush.mockClear()
  })

  it('devrait retourner 4 onglets en mode consultation', () => {
    const { tabs } = useNavigation()
    expect(tabs.value).toHaveLength(4)
  })

  it('devrait retourner 0 onglets en mode recherche', () => {
    mockRoute.meta.section = 'recherche'
    const { tabs } = useNavigation()
    expect(tabs.value).toHaveLength(0)
    mockRoute.meta.section = 'consultation' // reset
  })

  it('devrait avoir les bons labels d\'onglets', () => {
    const { tabs } = useNavigation()
    const labels = tabs.value.map((t) => t.label)

    expect(labels).toEqual([
      'Données générales',
      'Données financières',
      'Paliers',
      'Domiciliation',
    ])
  })

  it('devrait retourner 4 items de sidebar (avec recherche)', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems).toHaveLength(4)
  })

  it('devrait avoir les bons labels de sidebar', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems[0].label).toBe('Recherche')
    expect(sidebarItems[1].label).toBe('Consultation')
    expect(sidebarItems[2].label).toBe('Déblocage')
    expect(sidebarItems[3].label).toBe('Rbt anticipés')
  })

  it('devrait naviguer au clic sur un onglet avec le bon ID dossier', () => {
    const { tabs, navigateToTab } = useNavigation()

    navigateToTab(tabs.value[2])
    expect(mockPush).toHaveBeenCalledWith('/consultation/DOSS-2024-001/paliers')
  })

  it('devrait avoir le bon onglet actif basé sur la route', () => {
    const { activeTabId } = useNavigation()
    expect(activeTabId.value).toBe('donnees-generales')
  })

  it('chaque onglet devrait avoir une route valide avec l\'ID du dossier', () => {
    const { tabs } = useNavigation()

    tabs.value.forEach((tab) => {
      expect(tab.route).toMatch(/^\/consultation\/DOSS-2024-001\//)
      expect(tab.id).toBeTruthy()
    })
  })

  // ── selectSidebarItem ──
  describe('selectSidebarItem', () => {
    it('devrait naviguer vers la route du sidebar item (recherche)', () => {
      const { sidebarItems, selectSidebarItem } = useNavigation()

      selectSidebarItem(sidebarItems[0]) // Recherche
      expect(mockPush).toHaveBeenCalledWith('/recherche')
    })

    it('devrait naviguer vers la route du sidebar item (deblocage)', () => {
      const { sidebarItems, selectSidebarItem } = useNavigation()

      selectSidebarItem(sidebarItems[2]) // Déblocage
      expect(mockPush).toHaveBeenCalledWith('/deblocage')
    })

    it('devrait rediriger consultation vers recherche si pas de dossier courant', () => {
      const { sidebarItems, selectSidebarItem } = useNavigation()

      selectSidebarItem(sidebarItems[1]) // Consultation (sans dossier courant)
      expect(mockPush).toHaveBeenCalledWith('/recherche')
    })

    it('devrait naviguer vers le dossier courant si consultation avec dossier', async () => {
      // Charger un dossier dans le store pour simuler un dossier courant
      const { usePretStore } = await import('@/stores/pretStore')
      const store = usePretStore()
      store.dossierCourant = {
        id: 'DD04063627',
        donneesGenerales: {} as any,
        donneesPret: {} as any,
        dates: {} as any,
      }

      const { sidebarItems, selectSidebarItem } = useNavigation()
      selectSidebarItem(sidebarItems[1]) // Consultation (avec dossier)
      expect(mockPush).toHaveBeenCalledWith('/consultation/DD04063627/donnees-generales')
    })

    it('devrait mettre à jour l\'activeSidebarItem dans le store', async () => {
      const { usePretStore } = await import('@/stores/pretStore')
      const store = usePretStore()
      const { sidebarItems, selectSidebarItem } = useNavigation()

      selectSidebarItem(sidebarItems[3]) // Rbt anticipés
      expect(store.activeSidebarItem).toBe('rbt-anticipes')
    })
  })

  // ── activeSidebarId ──
  describe('activeSidebarId', () => {
    it('devrait retourner la section de la route courante', () => {
      mockRoute.meta.section = 'consultation'
      const { activeSidebarId } = useNavigation()
      expect(activeSidebarId.value).toBe('consultation')
    })

    it('devrait retourner recherche quand section = recherche', () => {
      mockRoute.meta.section = 'recherche'
      const { activeSidebarId } = useNavigation()
      expect(activeSidebarId.value).toBe('recherche')
      mockRoute.meta.section = 'consultation' // reset
    })
  })
})
