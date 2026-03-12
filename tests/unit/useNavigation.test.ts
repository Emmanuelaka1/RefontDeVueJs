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
})
