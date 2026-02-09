<template>
  <div class="consultation-view" data-testid="consultation-view">
    <SectionDonneesGenerales />
    <SectionDonneesPret />
    <SectionDates />
  </div>
</template>

<script setup lang="ts">
import SectionDonneesGenerales from '@/components/SectionDonneesGenerales.vue'
import SectionDonneesPret from '@/components/SectionDonneesPret.vue'
import SectionDates from '@/components/SectionDates.vue'
</script>

<style scoped lang="scss">
.consultation-view {
  max-width: 1400px;
}
</style>
