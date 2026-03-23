import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import SectionDates from '@/components/SectionDates.vue'
import { usePretStore } from '@/stores/pretStore'
import FormField from '@/components/FormField.vue'

describe('SectionDates', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
  })

  function mountSection() {
    return mount(SectionDates, {
      global: { plugins: [pinia] },
    })
  }

  it('devrait afficher le titre "Dates"', () => {
    const wrapper = mountSection()
    expect(wrapper.text()).toContain('Dates')
  })

  it('devrait afficher les champs de dates remplis', () => {
    const store = usePretStore()
    store.datesPret.dateAcceptation = '01/01/2024'
    store.datesPret.dateFinPret = '31/12/2044'

    const wrapper = mountSection()
    expect(wrapper.text()).toContain('01/01/2024')
    expect(wrapper.text()).toContain('31/12/2044')
  })

  it('devrait afficher "—" pour les dates vides', () => {
    const wrapper = mountSection()
    const values = wrapper.findAll('.field-value--empty')
    expect(values.length).toBeGreaterThan(0)
  })

  it('devrait toggler la section dates', async () => {
    const store = usePretStore()
    const initialState = store.sections.dates

    const wrapper = mountSection()
    const section = wrapper.findComponent({ name: 'CollapsibleSection' })
    await section.vm.$emit('toggle')

    expect(store.sections.dates).toBe(!initialState)
  })

  it('devrait mettre à jour le store via v-model (setters)', async () => {
    const store = usePretStore()
    const wrapper = mountSection()

    const formFields = wrapper.findAllComponents(FormField)
    const fieldsWithLabel = formFields.filter((f) => f.props('label'))

    // Émettre update:modelValue sur chaque FormField pour couvrir les setters v-model
    for (const field of fieldsWithLabel) {
      await field.vm.$emit('update:modelValue', '15/03/2024')
    }

    // Vérifier que les setters ont été appelés
    expect(store.datesPret.dateAcceptation).toBe('15/03/2024')
    expect(store.datesPret.dateFinPret).toBe('15/03/2024')
    expect(store.datesPret.dateEffet).toBe('15/03/2024')
  })
})
