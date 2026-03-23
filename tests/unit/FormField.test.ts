import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import FormField from '@/components/FormField.vue'

describe('FormField', () => {
  it('devrait afficher le label et la valeur', () => {
    const wrapper = mount(FormField, {
      props: { label: 'Montant', modelValue: '1 000 €' },
    })
    expect(wrapper.find('.field-label').text()).toBe('Montant')
    expect(wrapper.find('.field-value').text()).toBe('1 000 €')
  })

  it('devrait afficher "—" quand la valeur est vide', () => {
    const wrapper = mount(FormField, {
      props: { label: 'Montant', modelValue: '' },
    })
    expect(wrapper.find('.field-value').text()).toBe('—')
    expect(wrapper.find('.field-value').classes()).toContain('field-value--empty')
  })

  it('devrait masquer le champ (empty) quand pas de label', () => {
    const wrapper = mount(FormField)
    expect(wrapper.find('.form-field').classes()).toContain('empty')
    expect(wrapper.find('.field-label').exists()).toBe(false)
    expect(wrapper.find('.field-value').exists()).toBe(false)
  })

  it('devrait utiliser le testId personnalisé', () => {
    const wrapper = mount(FormField, {
      props: { label: 'N° prêt', modelValue: 'DD04', testId: 'my-field' },
    })
    expect(wrapper.find('[data-testid="my-field"]').exists()).toBe(true)
  })

  it('devrait utiliser le testId par défaut', () => {
    const wrapper = mount(FormField, {
      props: { label: 'EFS', modelValue: '01' },
    })
    expect(wrapper.find('[data-testid="form-field"]').exists()).toBe(true)
  })
})
