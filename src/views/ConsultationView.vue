<template>
  <div class="consultation-view" data-testid="consultation-view">
    <!-- Header -->
    <div class="view-header">
      <div class="view-header-left">
        <i class="pi pi-file-edit header-icon" />
        <h2 class="view-title">Consultation du dossier</h2>
        <span class="badge-status">Dossier en cours</span>
      </div>
      <div class="view-header-right">
        <button class="btn-action" @click="store.expandAllSections()">
          <i class="pi pi-angle-double-down" />
          <span>Tout ouvrir</span>
        </button>
        <button class="btn-action" @click="store.collapseAllSections()">
          <i class="pi pi-angle-double-up" />
          <span>Tout fermer</span>
        </button>
      </div>
    </div>

    <!-- Sections -->
    <SectionDonneesGenerales />
    <SectionDonneesPret />
    <SectionDates />
  </div>
</template>

<script setup lang="ts">
import SectionDonneesGenerales from '@/components/SectionDonneesGenerales.vue'
import SectionDonneesPret from '@/components/SectionDonneesPret.vue'
import SectionDates from '@/components/SectionDates.vue'
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()
</script>

<style scoped lang="scss">
.consultation-view {
  max-width: 1400px;
}

.view-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $space-lg;
  padding-bottom: $space-md;
  border-bottom: 1px solid var(--border-light);
}

.view-header-left {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.header-icon {
  font-size: 14px;
  color: $primary;
  background: $gray-100;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.view-title {
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  color: var(--text-primary);
}

.badge-status {
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  color: $info;
  background: $gray-100;
  padding: 2px $space-sm;
  border-radius: $border-radius-sm;
}

.view-header-right {
  display: flex;
  gap: $space-xs;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: $space-xs;
  padding: $space-xs $space-md;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-medium;
  color: $gray-600;
  background: $gray-100;
  border: 1px solid $gray-300;
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    color: $primary;
    border-color: $primary;
    background: $primary-light;
  }

  .pi {
    font-size: 12px;
  }
}
</style>
