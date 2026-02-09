import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'

// Mock vue-router
const mockPush = vi.fn()
const mockRoute = { meta: { tabId: 'consultation' } }

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

  it('devrait retourner 5 onglets', () => {
    const { tabs } = useNavigation()
    expect(tabs).toHaveLength(5)
  })

  it('devrait avoir les bons labels d\'onglets', () => {
    const { tabs } = useNavigation()
    const labels = tabs.map((t) => t.label)

    expect(labels).toEqual([
      'Consultation',
      'Données détaillées',
      'Données comptables',
      'Préavis',
      'Recouvrement',
    ])
  })

  it('devrait retourner 2 items de sidebar', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems).toHaveLength(2)
  })

  it('devrait avoir les bons labels de sidebar', () => {
    const { sidebarItems } = useNavigation()
    expect(sidebarItems[0].label).toBe('Dossier')
    expect(sidebarItems[1].label).toBe('Nul entièrge')
  })

  it('devrait naviguer au clic sur un onglet', () => {
    const { tabs, navigateToTab } = useNavigation()

    navigateToTab(tabs[2])
    expect(mockPush).toHaveBeenCalledWith('/dossier/donnees-comptables')
  })

  it('devrait avoir le bon onglet actif basé sur la route', () => {
    const { activeTabId } = useNavigation()
    expect(activeTabId.value).toBe('consultation')
  })

  it('chaque onglet devrait avoir une route valide', () => {
    const { tabs } = useNavigation()

    tabs.forEach((tab) => {
      expect(tab.route).toMatch(/^\/dossier\//)
      expect(tab.id).toBeTruthy()
    })
  })
})
