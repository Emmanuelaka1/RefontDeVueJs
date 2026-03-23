import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import AppToolbar from '@/components/AppToolbar.vue'
import { usePretStore } from '@/stores/pretStore'

describe('AppToolbar', () => {
  let pinia: ReturnType<typeof createPinia>
  let store: ReturnType<typeof usePretStore>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    store = usePretStore()
  })

  function mountToolbar() {
    return mount(AppToolbar, {
      global: {
        plugins: [pinia],
        stubs: { Transition: true },
      },
    })
  }

  it('devrait afficher le titre "Template"', () => {
    const wrapper = mountToolbar()
    expect(wrapper.find('.toolbar-title').text()).toBe('Template')
  })

  it('devrait afficher le nom de l\'utilisateur', () => {
    const wrapper = mountToolbar()
    expect(wrapper.find('.user-name').text()).toContain('Jean')
    expect(wrapper.find('.user-name').text()).toContain('Dupont')
  })

  it('devrait afficher les initiales de l\'utilisateur', () => {
    const wrapper = mountToolbar()
    expect(wrapper.find('.user-avatar').text()).toBe('JD')
  })

  it('devrait afficher le badge DEV', () => {
    const wrapper = mountToolbar()
    expect(wrapper.find('.env-badge').text()).toContain('DEV')
  })

  it('devrait toggler le dark mode au clic', async () => {
    const wrapper = mountToolbar()
    expect(store.darkMode).toBe(false)

    await wrapper.find('[data-testid="dark-mode-toggle"]').trigger('click')
    expect(store.darkMode).toBe(true)

    await wrapper.find('[data-testid="dark-mode-toggle"]').trigger('click')
    expect(store.darkMode).toBe(false)
  })

  it('devrait toggler la sidebar au clic sur hamburger', async () => {
    const wrapper = mountToolbar()
    expect(store.sidebarCollapsed).toBe(false)

    await wrapper.find('[data-testid="hamburger-btn"]').trigger('click')
    expect(store.sidebarCollapsed).toBe(true)
  })

  it('devrait avoir le data-testid toolbar', () => {
    const wrapper = mountToolbar()
    expect(wrapper.find('[data-testid="toolbar"]').exists()).toBe(true)
  })
})
