import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CollapsibleSection from '@/components/CollapsibleSection.vue'

describe('CollapsibleSection', () => {
  const defaultProps = {
    title: 'Données Générales',
    sectionId: 'general',
    expanded: true,
  }

  it('devrait afficher le titre en uppercase', () => {
    const wrapper = mount(CollapsibleSection, {
      props: defaultProps,
      slots: { default: '<div>Contenu</div>' },
    })

    expect(wrapper.find('.header-title').text()).toBe('Données Générales')
  })

  it('devrait afficher le contenu quand expanded = true', () => {
    const wrapper = mount(CollapsibleSection, {
      props: { ...defaultProps, expanded: true },
      slots: { default: '<div class="test-content">Contenu</div>' },
    })

    expect(wrapper.find('.section-body').isVisible()).toBe(true)
  })

  it('devrait cacher le contenu quand expanded = false', () => {
    const wrapper = mount(CollapsibleSection, {
      props: { ...defaultProps, expanded: false },
      slots: { default: '<div>Contenu</div>' },
    })

    expect(wrapper.find('.section-body').isVisible()).toBe(false)
  })

  it('devrait émettre toggle au clic sur le header', async () => {
    const wrapper = mount(CollapsibleSection, {
      props: defaultProps,
      slots: { default: '<div>Contenu</div>' },
    })

    await wrapper.find('.section-header').trigger('click')
    expect(wrapper.emitted('toggle')).toHaveLength(1)
  })

  it('devrait afficher le bon chevron selon l\'état', () => {
    const expandedWrapper = mount(CollapsibleSection, {
      props: { ...defaultProps, expanded: true },
      slots: { default: '<div>Contenu</div>' },
    })

    expect(expandedWrapper.find('.chevron').classes()).toContain('pi-chevron-down')

    const collapsedWrapper = mount(CollapsibleSection, {
      props: { ...defaultProps, expanded: false },
      slots: { default: '<div>Contenu</div>' },
    })

    expect(collapsedWrapper.find('.chevron').classes()).toContain('pi-chevron-right')
  })

  it('devrait avoir le bon data-testid', () => {
    const wrapper = mount(CollapsibleSection, {
      props: { ...defaultProps, sectionId: 'pret' },
      slots: { default: '<div>Contenu</div>' },
    })

    expect(wrapper.find('[data-testid="section-pret"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="section-header-pret"]').exists()).toBe(true)
  })
})
