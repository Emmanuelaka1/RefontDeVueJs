import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import PaliersView from '@/views/PaliersView.vue'
import DeblocageView from '@/views/DeblocageView.vue'
import DomiciliationView from '@/views/DomiciliationView.vue'
import DonneesFinancieresView from '@/views/DonneesFinancieresView.vue'
import RbtAnticipesView from '@/views/RbtAnticipesView.vue'

describe('Vues placeholder', () => {
  it('PaliersView devrait afficher le titre et le badge "À venir"', () => {
    const wrapper = mount(PaliersView)
    expect(wrapper.find('[data-testid="paliers-view"]').exists()).toBe(true)
    expect(wrapper.find('.placeholder-title').text()).toBe('Paliers')
    expect(wrapper.find('.badge-coming').text()).toBe('À venir')
    // Skeleton cards
    expect(wrapper.findAll('.skeleton-card')).toHaveLength(3)
  })

  it('DeblocageView devrait afficher le titre', () => {
    const wrapper = mount(DeblocageView)
    expect(wrapper.find('[data-testid="deblocage-view"]').exists()).toBe(true)
    expect(wrapper.find('.placeholder-title').text()).toBe('Déblocage')
  })

  it('DomiciliationView devrait afficher le titre', () => {
    const wrapper = mount(DomiciliationView)
    expect(wrapper.find('[data-testid="domiciliation-view"]').exists()).toBe(true)
    expect(wrapper.find('.placeholder-title').text()).toBe('Domiciliation')
  })

  it('DonneesFinancieresView devrait afficher le titre', () => {
    const wrapper = mount(DonneesFinancieresView)
    expect(wrapper.find('[data-testid="donnees-financieres-view"]').exists()).toBe(true)
    expect(wrapper.find('.placeholder-title').text()).toBe('Données financières')
  })

  it('RbtAnticipesView devrait afficher le titre', () => {
    const wrapper = mount(RbtAnticipesView)
    expect(wrapper.find('[data-testid="rbt-anticipes-view"]').exists()).toBe(true)
    expect(wrapper.find('.placeholder-title').text()).toBe('Remboursements anticipés')
  })
})
