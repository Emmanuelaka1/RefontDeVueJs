import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { createPinia, setActivePinia } from 'pinia'
import AppTabs from '@/components/AppTabs.vue'

// Mock useNavigation with real refs
const mockNavigateToTab = vi.fn()
const mockTabs = ref<any[]>([])
const mockActiveTabId = ref('')

vi.mock('@/composables/useNavigation', () => ({
  useNavigation: () => ({
    tabs: mockTabs,
    activeTabId: mockActiveTabId,
    navigateToTab: mockNavigateToTab,
  }),
}))

describe('AppTabs', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockNavigateToTab.mockClear()
    mockTabs.value = []
    mockActiveTabId.value = ''
  })

  it('ne devrait rien afficher quand il n\'y a pas d\'onglets', () => {
    const wrapper = mount(AppTabs)
    expect(wrapper.find('[data-testid="tabs-nav"]').exists()).toBe(false)
  })

  it('devrait afficher les onglets quand il y en a', () => {
    mockTabs.value = [
      { id: 'tab1', label: 'Onglet 1', route: '/tab1' },
      { id: 'tab2', label: 'Onglet 2', route: '/tab2' },
    ]
    const wrapper = mount(AppTabs)
    expect(wrapper.find('[data-testid="tabs-nav"]').exists()).toBe(true)
    expect(wrapper.findAll('.tab-item')).toHaveLength(2)
  })

  it('devrait marquer l\'onglet actif', () => {
    mockTabs.value = [
      { id: 'tab1', label: 'Onglet 1', route: '/tab1' },
      { id: 'tab2', label: 'Onglet 2', route: '/tab2' },
    ]
    mockActiveTabId.value = 'tab1'
    const wrapper = mount(AppTabs)
    expect(wrapper.find('[data-testid="tab-tab1"]').classes()).toContain('active')
    expect(wrapper.find('[data-testid="tab-tab2"]').classes()).not.toContain('active')
  })

  it('devrait appeler navigateToTab au clic', async () => {
    const tab = { id: 'tab1', label: 'Onglet 1', route: '/tab1' }
    mockTabs.value = [tab]
    const wrapper = mount(AppTabs)
    await wrapper.find('[data-testid="tab-tab1"]').trigger('click')
    expect(mockNavigateToTab).toHaveBeenCalledWith(tab)
  })
})
