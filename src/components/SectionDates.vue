<template>
  <CollapsibleSection
    title="Dates"
    section-id="dates"
    :expanded="store.sections.dates"
    @toggle="store.toggleSection('dates')"
  >
    <div class="form-grid three-cols">
      <!-- Row 1 -->
      <FormField label="Date d'acceptation" v-model="store.datesPret.dateAcceptation" />
      <FormField label="Date de 1ère échéance" v-model="store.datesPret.date1ereEcheance" />
      <FormField label="Date de précédente échéance" v-model="store.datesPret.datePrecedenteEcheance" />

      <!-- Row 2 -->
      <FormField label="Date d'accord" v-model="store.datesPret.dateAccord" />
      <FormField label="Date d'effet RA" v-model="store.datesPret.dateEffetRA" />
      <FormField label="Date de prochaine échéance" v-model="store.datesPret.dateProchaineEcheance" />

      <!-- Row 3 -->
      <FormField label="Date d'ouverture de crédit" v-model="store.datesPret.dateOuvertureCredit" />
      <FormField label="Date d'effet FP" v-model="store.datesPret.dateEffetFP" />
      <FormField label="Date abonnement précédent" v-model="store.datesPret.dateAbonnementPrecedent" />

      <!-- Row 4 -->
      <FormField label="Date de passage en gestion" v-model="store.datesPret.datePassageGestion" />
      <FormField label="Date fin prêt" v-model="store.datesPret.dateFinPret" />
      <FormField label="Date abonnement suivant" v-model="store.datesPret.dateAbonnementSuivant" />

      <!-- Row 5 -->
      <FormField label="Date d'effet" v-model="store.datesPret.dateEffet" />
      <FormField label="Date de 1ère échéance" v-model="store.datesPret.date1ereEcheance2" />
      <FormField label="Date tombée précédente" v-model="store.datesPret.dateTombeePrecedente" />

      <!-- Row 6 -->
      <FormField /><!-- empty -->
      <FormField /><!-- empty -->
      <FormField label="Date tombée suivante" v-model="store.datesPret.dateTombeeSuivante" />
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
