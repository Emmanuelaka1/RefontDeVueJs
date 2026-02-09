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
        <select
          v-if="dossierList.length > 0"
          v-model="selectedDossierId"
          class="dossier-select"
          @change="onDossierChange"
        >
          <option v-for="d in dossierList" :key="d.id" :value="d.id">
            {{ d.noPret }} — {{ d.emprunteur }}
          </option>
        </select>
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

    <!-- Loading -->
    <div v-if="store.loading" class="loading-indicator">
      <i class="pi pi-spin pi-spinner" />
      <span>Chargement du dossier...</span>
    </div>

    <!-- Erreur -->
    <div v-if="store.error" class="error-banner">
      <i class="pi pi-exclamation-triangle" />
      <span>{{ store.error }}</span>
    </div>

    <!-- Sections -->
    <SectionDonneesGenerales />
    <SectionDonneesPret />
    <SectionDates />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import SectionDonneesGenerales from '@/components/SectionDonneesGenerales.vue'
import SectionDonneesPret from '@/components/SectionDonneesPret.vue'
import SectionDates from '@/components/SectionDates.vue'
import { usePretStore } from '@/stores/pretStore'
import { getPretService } from '@/services/pretService'
import type { DossierResume } from '@/types'

const store = usePretStore()
const dossierList = ref<DossierResume[]>([])
const selectedDossierId = ref<string>('')

async function onDossierChange() {
  if (selectedDossierId.value) {
    await store.chargerDossier(selectedDossierId.value)
  }
}

onMounted(async () => {
  try {
    const service = await getPretService()
    const listResponse = await service.listerDossiers()
    if (listResponse.success) {
      dossierList.value = listResponse.data
    }
    if (dossierList.value.length > 0) {
      const idToLoad = store.dossierCourant?.id ?? dossierList.value[0].id
      selectedDossierId.value = idToLoad
      await store.chargerDossier(idToLoad)
    }
  } catch (e) {
    store.error = 'Erreur lors de l\'initialisation'
    console.error(e)
  }
})
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
  color: var(--hover-text);
  background: var(--bg-elevated);
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
  color: var(--badge-info-text);
  background: var(--badge-info-bg);
  padding: 2px $space-sm;
  border-radius: $border-radius-sm;
}

.view-header-right {
  display: flex;
  gap: $space-xs;
}

.dossier-select {
  padding: $space-xs $space-md;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-medium;
  color: var(--text-primary);
  background: var(--bg-surface);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;
  max-width: 320px;

  &:hover,
  &:focus {
    border-color: var(--hover-text);
    outline: none;
  }
}

.btn-action {
  display: flex;
  align-items: center;
  gap: $space-xs;
  padding: $space-xs $space-md;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-medium;
  color: var(--text-secondary);
  background: var(--bg-surface);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    color: var(--hover-text);
    border-color: var(--hover-text);
    background: var(--hover-bg);
  }

  .pi {
    font-size: 12px;
  }
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $space-sm;
  padding: $space-xl;
  color: var(--text-secondary);
  font-size: $font-size-sm;

  .pi-spinner {
    font-size: 18px;
    color: var(--hover-text);
  }
}

.error-banner {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: $space-md;
  margin-bottom: $space-lg;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: $border-radius;
  color: #991b1b;
  font-size: $font-size-sm;

  .pi {
    font-size: 14px;
  }
}
</style>
