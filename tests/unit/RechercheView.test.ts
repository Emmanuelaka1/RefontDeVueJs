import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import RechercheView from '@/views/RechercheView.vue'

// Mock vue-router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
}))

// Mock pretService — retourne toujours HttpPretService-like avec rechercherPret
const mockRechercherPret = vi.fn()
const mockGetDossier = vi.fn()
const mockListerDossiers = vi.fn()

vi.mock('@/services/pretService', () => ({
  getPretService: () =>
    Promise.resolve({
      rechercherPret: mockRechercherPret,
      getDossier: mockGetDossier,
      listerDossiers: mockListerDossiers,
    }),
}))

describe('RechercheView', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    mockPush.mockClear()
    mockRechercherPret.mockReset()
    mockGetDossier.mockReset()
    mockListerDossiers.mockReset()
  })

  function mountView() {
    return mount(RechercheView, {
      global: {
        plugins: [pinia],
        stubs: { Transition: true },
      },
    })
  }

  it('devrait afficher le titre "Recherche de dossiers"', () => {
    const wrapper = mountView()
    expect(wrapper.find('.view-title').text()).toBe('Recherche de dossiers')
  })

  it('devrait afficher le formulaire de recherche', () => {
    const wrapper = mountView()
    expect(wrapper.find('.search-form').exists()).toBe(true)
    expect(wrapper.find('.field-input').exists()).toBe(true)
  })

  it('devrait afficher l\'état initial (pas de recherche lancée)', () => {
    const wrapper = mountView()
    expect(wrapper.find('.initial-state').exists()).toBe(true)
    expect(wrapper.find('.resultats-section').exists()).toBe(false)
  })

  it('devrait avoir le bouton Rechercher désactivé quand le champ est vide', () => {
    const wrapper = mountView()
    const btn = wrapper.find('.btn-search')
    expect(btn.attributes('disabled')).toBeDefined()
  })

  it('devrait appeler rechercherPret au clic sur Rechercher', async () => {
    mockRechercherPret.mockResolvedValue({
      success: true,
      data: [
        {
          id: 'DD04063627',
          noPret: 'DD04063627',
          emprunteur: 'MARTIN Jean-Pierre',
          montantPret: '250 000,00 €',
          codeEtat: 'AA - EN COURS NORMALE',
        },
      ],
    })

    const wrapper = mountView()

    // Saisir un numéro de prêt
    await wrapper.find('.field-input').setValue('DD04063627')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    expect(mockRechercherPret).toHaveBeenCalledWith('DD04063627')
  })

  it('devrait afficher les résultats après une recherche réussie', async () => {
    mockRechercherPret.mockResolvedValue({
      success: true,
      data: [
        {
          id: 'DD04063627',
          noPret: 'DD04063627',
          emprunteur: 'MARTIN Jean-Pierre',
          montantPret: '250 000,00 €',
          codeEtat: 'AA - EN COURS NORMALE',
        },
      ],
    })

    const wrapper = mountView()
    await wrapper.find('.field-input').setValue('DD04063627')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    expect(wrapper.find('.resultats-section').exists()).toBe(true)
    expect(wrapper.find('.resultats-count').text()).toContain('1 dossier')
    expect(wrapper.find('.initial-state').exists()).toBe(false)
  })

  it('devrait afficher "Aucun dossier trouvé" si résultats vides', async () => {
    mockRechercherPret.mockResolvedValue({
      success: true,
      data: [],
      message: 'Aucun prêt trouvé pour "INEXISTANT"',
    })

    const wrapper = mountView()
    await wrapper.find('.field-input').setValue('INEXISTANT')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    expect(wrapper.find('.no-results').exists()).toBe(true)
    expect(wrapper.find('.no-results-title').text()).toContain('Aucun dossier')
  })

  it('devrait afficher l\'erreur en cas d\'échec', async () => {
    mockRechercherPret.mockResolvedValue({
      success: false,
      data: [],
      message: 'Erreur HTTP 500',
    })

    const wrapper = mountView()
    await wrapper.find('.field-input').setValue('DD04063627')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    expect(wrapper.find('.error-banner').exists()).toBe(true)
  })

  it('devrait réinitialiser au clic sur le bouton reset', async () => {
    mockRechercherPret.mockResolvedValue({
      success: true,
      data: [{ id: 'DD04063627', noPret: 'DD04063627', emprunteur: 'M', montantPret: '1 €', codeEtat: 'AA' }],
    })

    const wrapper = mountView()
    const { usePretStore } = await import('@/stores/pretStore')
    const store = usePretStore()

    await wrapper.find('.field-input').setValue('DD04063627')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    // Après recherche : résultats dans le store
    expect(store.rechercheResultats).toHaveLength(1)
    expect(store.rechercheLancee).toBe(true)

    await wrapper.find('.btn-reset').trigger('click')
    await flushPromises()

    // Après reset : store vidé, champ vide
    expect(store.rechercheNumeroPret).toBe('')
    expect(store.rechercheResultats).toHaveLength(0)
    expect(store.rechercheLancee).toBe(false)
    expect((wrapper.find('.field-input').element as HTMLInputElement).value).toBe('')
  })

  it('devrait naviguer vers la consultation au clic sur un résultat', async () => {
    mockRechercherPret.mockResolvedValue({
      success: true,
      data: [{ id: 'DD04063627', noPret: 'DD04063627', emprunteur: 'M', montantPret: '1 €', codeEtat: 'AA' }],
    })

    const wrapper = mountView()
    await wrapper.find('.field-input').setValue('DD04063627')
    await wrapper.find('.btn-search').trigger('click')
    await flushPromises()

    await wrapper.find('.result-row').trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/consultation/DD04063627/donnees-generales')
  })
})
