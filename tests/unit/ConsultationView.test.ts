import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import ConsultationView from '@/views/ConsultationView.vue'
import { usePretStore } from '@/stores/pretStore'

// Mock vue-router
const mockPush = vi.fn()
const mockRoute = {
  params: { id: 'DD04063627' },
  meta: { section: 'consultation', tabId: 'donnees-generales' },
}

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: mockPush }),
  useRoute: () => mockRoute,
}))

// Mock pretService
const mockGetDossier = vi.fn()
vi.mock('@/services/pretService', () => ({
  getPretService: () =>
    Promise.resolve({
      getDossier: mockGetDossier,
      listerDossiers: vi.fn().mockResolvedValue({ success: true, data: [] }),
      rechercherPret: vi.fn().mockResolvedValue({ success: true, data: [] }),
    }),
}))

describe('ConsultationView', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    mockPush.mockClear()
    mockGetDossier.mockReset()
    mockRoute.params.id = 'DD04063627'
  })

  function mountView() {
    return mount(ConsultationView, {
      global: {
        plugins: [pinia],
        stubs: {
          SectionDonneesGenerales: { template: '<div class="stub-general" />' },
          SectionDonneesPret: { template: '<div class="stub-pret" />' },
          SectionDates: { template: '<div class="stub-dates" />' },
        },
      },
    })
  }

  it('devrait afficher le titre "Consultation du dossier"', () => {
    const wrapper = mountView()
    expect(wrapper.find('.view-title').text()).toBe('Consultation du dossier')
  })

  it('devrait afficher le loading pendant le chargement', async () => {
    mockGetDossier.mockImplementation(() => new Promise(() => {})) // never resolves
    const store = usePretStore()
    store.loading = true

    const wrapper = mountView()
    expect(wrapper.find('.loading-indicator').exists()).toBe(true)
  })

  it('devrait afficher une erreur', () => {
    const store = usePretStore()
    store.error = 'Erreur de chargement'

    const wrapper = mountView()
    expect(wrapper.find('.error-banner').exists()).toBe(true)
    expect(wrapper.find('.error-banner').text()).toContain('Erreur de chargement')
  })

  it('devrait afficher les sections quand un dossier est chargé', async () => {
    mockGetDossier.mockResolvedValue({
      success: true,
      data: {
        id: 'DD04063627',
        donneesGenerales: { emprunteur: 'MARTIN', coEmprunteur: '', noPret: 'DD04063627', noContratSouscritProjet: '', noContratSouscritPret: '', efs: '', structure: '', codeEtat: '', codeObjet: '', codeNature: '' },
        donneesPret: { montantPret: '', dureePret: '', tauxRemboursement: '', tauxFranchise: '', tauxBonification: '', anticipation: '', typeAmortissement: '', outilInstruction: '', montantDebloque: '', montantDisponible: '', montantRA: '', encours: '', teg: '' },
        dates: { dateAcceptation: '', dateAccord: '', dateOuvertureCredit: '', datePassageGestion: '', dateEffet: '', date1ereEcheance: '', dateEffetRA: '', dateEffetFP: '', dateFinPret: '', date1ereEcheance2: '', datePrecedenteEcheance: '', dateProchaineEcheance: '', dateAbonnementPrecedent: '', dateAbonnementSuivant: '', dateTombeePrecedente: '', dateTombeeSuivante: '' },
      },
    })

    const wrapper = mountView()
    await flushPromises()

    expect(wrapper.find('.stub-general').exists()).toBe(true)
    expect(wrapper.find('.stub-pret').exists()).toBe(true)
    expect(wrapper.find('.stub-dates').exists()).toBe(true)
  })

  it('devrait naviguer vers /recherche au clic sur "Retour recherche"', async () => {
    const wrapper = mountView()
    const buttons = wrapper.findAll('.btn-action')
    // Premier bouton = "Retour recherche"
    await buttons[0].trigger('click')
    expect(mockPush).toHaveBeenCalledWith('/recherche')
  })

  it('devrait ouvrir toutes les sections au clic sur "Tout ouvrir"', async () => {
    mockGetDossier.mockResolvedValue({ success: true, data: { id: 'DD04063627', donneesGenerales: {}, donneesPret: {}, dates: {} } })
    const store = usePretStore()
    store.collapseAllSections()

    const wrapper = mountView()
    await flushPromises()

    const buttons = wrapper.findAll('.btn-action')
    // Deuxième bouton = "Tout ouvrir"
    await buttons[1].trigger('click')
    expect(store.sections.general).toBe(true)
    expect(store.sections.pret).toBe(true)
    expect(store.sections.dates).toBe(true)
  })

  it('devrait fermer toutes les sections au clic sur "Tout fermer"', async () => {
    mockGetDossier.mockResolvedValue({ success: true, data: { id: 'DD04063627', donneesGenerales: {}, donneesPret: {}, dates: {} } })
    const store = usePretStore()

    const wrapper = mountView()
    await flushPromises()

    const buttons = wrapper.findAll('.btn-action')
    // Troisième bouton = "Tout fermer"
    await buttons[2].trigger('click')
    expect(store.sections.general).toBe(false)
    expect(store.sections.pret).toBe(false)
    expect(store.sections.dates).toBe(false)
  })

  it('devrait afficher le badge de statut quand un dossier est chargé', async () => {
    mockGetDossier.mockResolvedValue({
      success: true,
      data: {
        id: 'DD04063627',
        donneesGenerales: { codeEtat: 'AA - EN COURS NORMALE', emprunteur: '', coEmprunteur: '', noPret: '', noContratSouscritProjet: '', noContratSouscritPret: '', efs: '', structure: '', codeObjet: '', codeNature: '' },
        donneesPret: { montantPret: '', dureePret: '', tauxRemboursement: '', tauxFranchise: '', tauxBonification: '', anticipation: '', typeAmortissement: '', outilInstruction: '', montantDebloque: '', montantDisponible: '', montantRA: '', encours: '', teg: '' },
        dates: {},
      },
    })

    const wrapper = mountView()
    await flushPromises()

    expect(wrapper.find('.badge-status').exists()).toBe(true)
    expect(wrapper.find('.badge-status').text()).toContain('AA - EN COURS NORMALE')
  })
})
