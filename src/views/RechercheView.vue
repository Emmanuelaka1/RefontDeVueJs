<template>
  <div class="recherche-view" data-testid="recherche-view">
    <!-- Header -->
    <div class="view-header">
      <div class="view-header-left">
        <i class="pi pi-search header-icon" />
        <h2 class="view-title">Recherche de dossiers</h2>
      </div>
    </div>

    <!-- Formulaire de recherche -->
    <div class="search-form">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">N° de prêt</label>
          <input
            v-model="criteres.numeroPret"
            type="text"
            class="field-input"
            placeholder="Ex: 2024-PAP-001547"
            @keydown.enter="rechercher"
          />
        </div>
        <div class="search-field">
          <label class="field-label">Emprunteur</label>
          <input
            v-model="criteres.emprunteur"
            type="text"
            class="field-input"
            placeholder="Nom de l'emprunteur"
            @keydown.enter="rechercher"
          />
        </div>
        <div class="search-field">
          <label class="field-label">EFS</label>
          <input
            v-model="criteres.efs"
            type="text"
            class="field-input"
            placeholder="Code EFS"
            @keydown.enter="rechercher"
          />
        </div>
        <div class="search-field">
          <label class="field-label">État</label>
          <select v-model="criteres.codeEtat" class="field-input">
            <option value="">Tous</option>
            <option value="20">20 - En instruction</option>
            <option value="30">30 - En déblocage</option>
            <option value="40">40 - En gestion</option>
          </select>
        </div>
      </div>
      <div class="search-actions">
        <button class="btn-search" @click="rechercher">
          <i class="pi pi-search" />
          <span>Rechercher</span>
        </button>
        <button class="btn-reset" @click="reinitialiser">
          <i class="pi pi-times" />
          <span>Réinitialiser</span>
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-indicator">
      <i class="pi pi-spin pi-spinner" />
      <span>Recherche en cours...</span>
    </div>

    <!-- Erreur -->
    <div v-if="erreur" class="error-banner">
      <i class="pi pi-exclamation-triangle" />
      <span>{{ erreur }}</span>
    </div>

    <!-- Résultats -->
    <div v-if="!loading && resultats.length > 0" class="resultats-section">
      <div class="resultats-header">
        <span class="resultats-count">{{ resultats.length }} dossier(s) trouvé(s)</span>
      </div>
      <table class="resultats-table">
        <thead>
          <tr>
            <th>N° Prêt</th>
            <th>Emprunteur</th>
            <th>Montant</th>
            <th>État</th>
            <th class="col-action">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="dossier in resultats"
            :key="dossier.id"
            class="result-row"
            @click="consulterDossier(dossier.id)"
          >
            <td class="cell-nopret">{{ dossier.noPret }}</td>
            <td>{{ dossier.emprunteur }}</td>
            <td class="cell-montant">{{ dossier.montantPret }}</td>
            <td>
              <span class="badge-etat" :class="badgeClass(dossier.codeEtat)">
                {{ dossier.codeEtat }}
              </span>
            </td>
            <td class="col-action">
              <button
                class="btn-consulter"
                @click.stop="consulterDossier(dossier.id)"
                title="Consulter le dossier"
              >
                <i class="pi pi-eye" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Aucun résultat -->
    <div v-if="!loading && rechercheLancee && resultats.length === 0" class="no-results">
      <i class="pi pi-inbox" />
      <p>Aucun dossier ne correspond à vos critères.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPretService } from '@/services/pretService'
import { usePretStore } from '@/stores/pretStore'
import type { DossierResume } from '@/types'

const router = useRouter()
const store = usePretStore()

const loading = ref(false)
const erreur = ref<string | null>(null)
const rechercheLancee = ref(false)
const resultats = ref<DossierResume[]>([])

const criteres = ref({
  numeroPret: '',
  emprunteur: '',
  efs: '',
  codeEtat: '',
})

async function rechercher() {
  loading.value = true
  erreur.value = null
  rechercheLancee.value = true

  try {
    const service = await getPretService()
    const response = await service.listerDossiers()

    if (response.success) {
      // Filtrage côté client sur les critères saisis
      resultats.value = response.data.filter((d) => {
        if (criteres.value.numeroPret && !d.noPret.toLowerCase().includes(criteres.value.numeroPret.toLowerCase())) {
          return false
        }
        if (criteres.value.emprunteur && !d.emprunteur.toLowerCase().includes(criteres.value.emprunteur.toLowerCase())) {
          return false
        }
        if (criteres.value.codeEtat && !d.codeEtat.startsWith(criteres.value.codeEtat)) {
          return false
        }
        return true
      })
    } else {
      erreur.value = response.message || 'Erreur lors de la recherche'
    }
  } catch (e) {
    erreur.value = 'Erreur lors de la recherche'
    console.error(e)
  } finally {
    loading.value = false
  }
}

function reinitialiser() {
  criteres.value = { numeroPret: '', emprunteur: '', efs: '', codeEtat: '' }
  resultats.value = []
  rechercheLancee.value = false
  erreur.value = null
}

function consulterDossier(id: string) {
  store.setActiveSidebarItem('consultation')
  router.push(`/consultation/${id}/donnees-generales`)
}

function badgeClass(codeEtat: string): string {
  if (codeEtat.startsWith('40')) return 'badge-gestion'
  if (codeEtat.startsWith('30')) return 'badge-deblocage'
  if (codeEtat.startsWith('20')) return 'badge-instruction'
  return ''
}

// Chargement initial : afficher tous les dossiers
onMounted(() => {
  rechercher()
})
</script>

<style scoped lang="scss">
.recherche-view {
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

// ── Formulaire de recherche ──
.search-form {
  background: var(--bg-elevated);
  border: 1px solid var(--border-main);
  border-radius: $border-radius-lg;
  padding: $space-lg;
  margin-bottom: $space-lg;
}

.search-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-md;
  margin-bottom: $space-md;
}

.search-field {
  display: flex;
  flex-direction: column;
  gap: $space-xs;
}

.field-label {
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.field-input {
  padding: $space-sm $space-md;
  font-size: $font-size-sm;
  font-family: inherit;
  color: var(--text-primary);
  background: var(--bg-surface);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  transition: all $transition-base;

  &:focus {
    border-color: $primary;
    outline: none;
    box-shadow: 0 0 0 2px rgba($primary, 0.15);
  }

  &::placeholder {
    color: var(--text-muted);
  }
}

.search-actions {
  display: flex;
  gap: $space-sm;
}

.btn-search {
  display: flex;
  align-items: center;
  gap: $space-xs;
  padding: $space-sm $space-lg;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-semibold;
  color: $white;
  background: $primary;
  border: none;
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    background: $primary-dark;
  }

  .pi {
    font-size: 12px;
  }
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: $space-xs;
  padding: $space-sm $space-lg;
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
  }

  .pi {
    font-size: 12px;
  }
}

// ── Résultats ──
.resultats-section {
  border: 1px solid var(--border-main);
  border-radius: $border-radius-lg;
  overflow: hidden;
}

.resultats-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-md $space-lg;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-main);
}

.resultats-count {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: var(--text-secondary);
}

.resultats-table {
  width: 100%;
  border-collapse: collapse;
  font-size: $font-size-sm;

  th {
    text-align: left;
    padding: $space-md $space-lg;
    font-size: $font-size-xs;
    font-weight: $font-weight-bold;
    color: var(--text-secondary);
    text-transform: uppercase;
    letter-spacing: 0.5px;
    background: var(--bg-elevated);
    border-bottom: 2px solid var(--border-main);
  }

  td {
    padding: $space-md $space-lg;
    color: var(--text-primary);
    border-bottom: 1px solid var(--border-light);
  }
}

.result-row {
  cursor: pointer;
  transition: background $transition-fast;

  &:hover {
    background: var(--hover-bg);
  }
}

.cell-nopret {
  font-weight: $font-weight-semibold;
  color: $primary;
}

.cell-montant {
  font-family: 'Courier New', monospace;
  text-align: right;
}

.col-action {
  width: 60px;
  text-align: center;
}

.badge-etat {
  display: inline-block;
  padding: 2px $space-sm;
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  border-radius: $border-radius-sm;
  white-space: nowrap;
}

.badge-gestion {
  color: $success;
  background: $success-light;
}

.badge-deblocage {
  color: $warning;
  background: $warning-light;
}

.badge-instruction {
  color: $info;
  background: $info-light;
}

.btn-consulter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 14px;
  color: $primary;
  background: transparent;
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    background: $primary;
    color: $white;
    border-color: $primary;
  }
}

// ── États ──
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

.no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $space-xxxl;
  color: var(--text-muted);
  text-align: center;

  .pi {
    font-size: 32px;
    margin-bottom: $space-md;
  }

  p {
    font-size: $font-size-sm;
  }
}
</style>
