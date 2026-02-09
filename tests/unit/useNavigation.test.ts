import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock vue-router
const mockPush = vi.fn()
const mockRoute = { meta: { tabId: 'donnees-generales' } }

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

  it('devrait retourner 4 onglets', () => {
    const { tabs } = useNavigation()
    expect(tabs).toHaveLength(4)
  })

  it('devrait avoir les bons labels d\'onglets', () => {
    const { tabs } = useNavigation()
    const labels = tabs.map((t) => t.label)

    expect(labels).toEqual([
      'Données générales',
      'Données financières',
      'Paliers',
      'Domiciliation',
    ])
  })

  it('devrait retourner 3 items de sidebar', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems).toHaveLength(3)
  })

  it('devrait avoir les bons labels de sidebar', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems[0].label).toBe('Consultation')
    expect(sidebarItems[1].label).toBe('Déblocage')
    expect(sidebarItems[2].label).toBe('Rbt anticipés')
  })

  it('devrait naviguer au clic sur un onglet', () => {
    const { tabs, navigateToTab } = useNavigation()

    navigateToTab(tabs[2])
    expect(mockPush).toHaveBeenCalledWith('/consultation/paliers')
  })

  it('devrait avoir le bon onglet actif basé sur la route', () => {
    const { activeTabId } = useNavigation()
    expect(activeTabId.value).toBe('donnees-generales')
  })

  it('chaque onglet devrait avoir une route valide', () => {
    const { tabs } = useNavigation()

    tabs.forEach((tab) => {
      expect(tab.route).toMatch(/^\/consultation\//)
      expect(tab.id).toBeTruthy()
    })
  })
})
