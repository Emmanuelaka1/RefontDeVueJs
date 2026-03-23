import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import AppSidebar from '@/components/AppSidebar.vue'

// Mock vue-router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRoute: () => ({
    meta: { tabId: 'donnees-generales', section: 'recherche' },
    params: {},
  }),
  useRouter: () => ({ push: mockPush }),
}))

describe('AppSidebar', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockPush.mockClear()
  })

  function mountSidebar(props = {}) {
    return mount(AppSidebar, {
      props,
      global: {
        plugins: [createPinia()],
      },
    })
  }

  it('devrait afficher 4 items de navigation', () => {
    const wrapper = mountSidebar()
    expect(wrapper.findAll('.sidebar-item')).toHaveLength(4)
  })

  it('devrait afficher les labels quand non collapsed', () => {
    const wrapper = mountSidebar({ collapsed: false })
    const labels = wrapper.findAll('.sidebar-label')
    expect(labels.length).toBeGreaterThan(0)
    expect(labels[0].text()).toBe('Recherche')
  })

  it('devrait cacher les labels quand collapsed', () => {
    const wrapper = mountSidebar({ collapsed: true })
    expect(wrapper.findAll('.sidebar-label')).toHaveLength(0)
  })

  it('devrait avoir la classe collapsed quand collapsed', () => {
    const wrapper = mountSidebar({ collapsed: true })
    expect(wrapper.find('.app-sidebar').classes()).toContain('collapsed')
  })

  it('devrait afficher le header Navigation quand non collapsed', () => {
    const wrapper = mountSidebar({ collapsed: false })
    expect(wrapper.find('.sidebar-header').exists()).toBe(true)
  })

  it('devrait naviguer au clic sur un item', async () => {
    const wrapper = mountSidebar()
    const items = wrapper.findAll('.sidebar-item')

    await items[0].trigger('click') // Recherche
    expect(mockPush).toHaveBeenCalledWith('/recherche')
  })

  it('devrait avoir les bons data-testid', () => {
    const wrapper = mountSidebar()
    expect(wrapper.find('[data-testid="sidebar-item-recherche"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="sidebar-item-consultation"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="sidebar-item-deblocage"]').exists()).toBe(true)
  })
})
