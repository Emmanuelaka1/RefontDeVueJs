<template>
  <CollapsibleSection
    title="Données Générales"
    section-id="general"
    :expanded="store.sections.general"
    @toggle="store.toggleSection('general')"
  >
    <div class="form-grid two-cols">
      <!-- Row 1 -->
      <FormField label="Emprunteur" v-model="store.donneesGenerales.emprunteur" test-id="field-emprunteur" />
      <FormField label="EFS" v-model="store.donneesGenerales.efs" test-id="field-efs" />

      <!-- Row 2 -->
      <FormField label="Co-emprunteur" v-model="store.donneesGenerales.coEmprunteur" test-id="field-co-emprunteur" />
      <FormField label="Structure" v-model="store.donneesGenerales.structure" test-id="field-structure" />

      <!-- Row 3 -->
      <FormField label="N° prêt" v-model="store.donneesGenerales.noPret" test-id="field-no-pret" />
      <FormField label="Code état" v-model="store.donneesGenerales.codeEtat" test-id="field-code-etat" />

      <!-- Row 4 -->
      <FormField label="N° contrat souscrit projet" v-model="store.donneesGenerales.noContratSouscritProjet" test-id="field-contrat-projet" />
      <FormField label="Code objet" v-model="store.donneesGenerales.codeObjet" test-id="field-code-objet" />

      <!-- Row 5 -->
      <FormField label="N° contrat souscrit prêt" v-model="store.donneesGenerales.noContratSouscritPret" test-id="field-contrat-pret" />
      <FormField label="Code nature" v-model="store.donneesGenerales.codeNature" test-id="field-code-nature" />
    </div>
  </CollapsibleSection>
</template>

<script setup lang="ts">
import CollapsibleSection from '@/components/CollapsibleSection.vue'
import FormField from '@/components/FormField.vue'
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()
</script>

<style scoped lang="scss">
.form-grid.two-cols {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $space-sm $space-xxxl;
}
</style>
