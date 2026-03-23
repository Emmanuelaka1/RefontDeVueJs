import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import SectionDonneesPret from '@/components/SectionDonneesPret.vue'
import { usePretStore } from '@/stores/pretStore'
import FormField from '@/components/FormField.vue'

describe('SectionDonneesPret', () => {
  let pinia: ReturnType<typeof createPinia>

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
  })

  function mountSection() {
    return mount(SectionDonneesPret, {
      global: { plugins: [pinia] },
    })
  }

  it('devrait afficher le titre "Données Prêt"', () => {
    const wrapper = mountSection()
    expect(wrapper.text()).toContain('Données Prêt')
  })

  it('devrait afficher les champs montant et durée', () => {
    const store = usePretStore()
    store.donneesPret.montantPret = '250 000,00 €'
    store.donneesPret.dureePret = '240 mois'

    const wrapper = mountSection()
    expect(wrapper.text()).toContain('250 000,00 €')
    expect(wrapper.text()).toContain('240 mois')
  })

  it('devrait afficher les taux', () => {
    const store = usePretStore()
    store.donneesPret.tauxRemboursement = '3,45 %'
    store.donneesPret.tauxFranchise = '1,00 %'
    store.donneesPret.tauxBonification = '0,50 %'

    const wrapper = mountSection()
    expect(wrapper.text()).toContain('3,45 %')
    expect(wrapper.text()).toContain('1,00 %')
    expect(wrapper.text()).toContain('0,50 %')
  })

  it('devrait toggler la section pret', async () => {
    const store = usePretStore()
    const initialState = store.sections.pret

    const wrapper = mountSection()
    const section = wrapper.findComponent({ name: 'CollapsibleSection' })
    await section.vm.$emit('toggle')

    expect(store.sections.pret).toBe(!initialState)
  })

  it('devrait mettre à jour le store via v-model (setters)', async () => {
    const store = usePretStore()
    const wrapper = mountSection()

    const formFields = wrapper.findAllComponents(FormField)
    const fieldsWithLabel = formFields.filter((f) => f.props('label'))

    for (const field of fieldsWithLabel) {
      await field.vm.$emit('update:modelValue', 'valeur-test')
    }

    expect(store.donneesPret.montantPret).toBe('valeur-test')
    expect(store.donneesPret.dureePret).toBe('valeur-test')
    expect(store.donneesPret.tauxRemboursement).toBe('valeur-test')
    expect(store.donneesPret.encours).toBe('valeur-test')
    expect(store.donneesPret.teg).toBe('valeur-test')
  })
})
