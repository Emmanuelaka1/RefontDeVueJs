<template>
  <CollapsibleSection
    title="Données Prêt"
    section-id="pret"
    :expanded="store.sections.pret"
    @toggle="store.toggleSection('pret')"
  >
    <div class="form-grid three-cols">
      <!-- Row 1 -->
      <FormField label="Montant prêt" v-model="store.donneesPret.montantPret" />
      <FormField label="Anticipation" v-model="store.donneesPret.anticipation" />
      <FormField label="Montant débloqué" v-model="store.donneesPret.montantDebloque" />

      <!-- Row 2 -->
      <FormField label="Durée prêt" v-model="store.donneesPret.dureePret" />
      <FormField label="Type d'amortissement" v-model="store.donneesPret.typeAmortissement" />
      <FormField label="Montant disponible" v-model="store.donneesPret.montantDisponible" />

      <!-- Row 3 -->
      <FormField label="Taux de remboursement" v-model="store.donneesPret.tauxRemboursement" />
      <FormField label="Outil d'instruction" v-model="store.donneesPret.outilInstruction" />
      <FormField label="Montant RA" v-model="store.donneesPret.montantRA" />

      <!-- Row 4 -->
      <FormField label="Taux de franchise" v-model="store.donneesPret.tauxFranchise" />
      <FormField /><!-- empty -->
      <FormField label="Encours" v-model="store.donneesPret.encours" />

      <!-- Row 5 -->
      <FormField label="Taux de bonification" v-model="store.donneesPret.tauxBonification" />
      <FormField /><!-- empty -->
      <FormField label="TEG" v-model="store.donneesPret.teg" />
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
.form-grid.three-cols {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: $space-sm $space-xxl;
}
</style>
