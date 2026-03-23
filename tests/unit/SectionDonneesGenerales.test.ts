import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import SectionDonneesGenerales from '@/components/SectionDonneesGenerales.vue'
import { usePretStore } from '@/stores/pretStore'
import FormField from '@/components/FormField.vue'

describe('SectionDonneesGenerales', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
  })

  function mountSection() {
    return mount(SectionDonneesGenerales, {
      global: { plugins: [pinia] },
    })
  }

  it('devrait afficher le titre "Données Générales"', () => {
    const wrapper = mountSection()
    expect(wrapper.text()).toContain('Données Générales')
  })

  it('devrait afficher les champs emprunteur et EFS', () => {
    const store = usePretStore()
    store.donneesGenerales.emprunteur = 'MARTIN Jean-Pierre'
    store.donneesGenerales.efs = '01'

    const wrapper = mountSection()
    expect(wrapper.find('[data-testid="field-emprunteur"]').text()).toContain('MARTIN Jean-Pierre')
    expect(wrapper.find('[data-testid="field-efs"]').text()).toContain('01')
  })

  it('devrait afficher les champs N° prêt et code état', () => {
    const store = usePretStore()
    store.donneesGenerales.noPret = 'DD04063627'
    store.donneesGenerales.codeEtat = 'AA - EN COURS NORMALE'

    const wrapper = mountSection()
    expect(wrapper.find('[data-testid="field-no-pret"]').text()).toContain('DD04063627')
    expect(wrapper.find('[data-testid="field-code-etat"]').text()).toContain('AA - EN COURS NORMALE')
  })

  it('devrait toggler la section au clic sur le header', async () => {
    const store = usePretStore()
    const initialState = store.sections.general

    const wrapper = mountSection()
    const section = wrapper.findComponent({ name: 'CollapsibleSection' })
    await section.vm.$emit('toggle')

    expect(store.sections.general).toBe(!initialState)
  })

  it('devrait mettre à jour le store via v-model (setters)', async () => {
    const store = usePretStore()
    const wrapper = mountSection()

    const formFields = wrapper.findAllComponents(FormField)
    const fieldsWithLabel = formFields.filter((f) => f.props('label'))

    for (const field of fieldsWithLabel) {
      await field.vm.$emit('update:modelValue', 'test-val')
    }

    expect(store.donneesGenerales.emprunteur).toBe('test-val')
    expect(store.donneesGenerales.efs).toBe('test-val')
    expect(store.donneesGenerales.noPret).toBe('test-val')
    expect(store.donneesGenerales.codeEtat).toBe('test-val')
    expect(store.donneesGenerales.codeNature).toBe('test-val')
  })
})
