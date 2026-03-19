<template>
  <div class="recherche-view" data-testid="recherche-view">
    <!-- Header -->
    <div class="view-header">
      <div class="view-header-left">
        <div class="header-icon-wrap">
          <i class="pi pi-search header-icon" />
        </div>
        <div class="header-text">
          <h2 class="view-title">Recherche de dossiers</h2>
          <p class="view-subtitle">Consultez un prêt par son numéro de contrat souscrit</p>
        </div>
      </div>
    </div>

    <!-- Formulaire de recherche -->
    <div class="search-form">
      <label class="field-label">
        <i class="pi pi-hashtag" />
        N° de prêt
      </label>
      <div class="search-row">
        <input
          v-model="numeroPret"
          type="text"
          class="field-input"
          placeholder="Saisissez un numéro de contrat souscrit — Ex: DD04063627"
          @keydown.enter="rechercher"
        />
        <button class="btn-search" :disabled="loading || !numeroPret.trim()" @click="rechercher">
          <i class="pi" :class="loading ? 'pi-spin pi-spinner' : 'pi-search'" />
          <span>{{ loading ? 'Recherche...' : 'Rechercher' }}</span>
        </button>
        <button class="btn-reset" @click="reinitialiser">
          <i class="pi pi-refresh" />
        </button>
      </div>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="loading-skeleton">
      <div class="skeleton-header" />
      <div v-for="n in 3" :key="n" class="skeleton-row">
        <div class="skeleton-cell skeleton-cell--id" />
        <div class="skeleton-cell skeleton-cell--name" />
        <div class="skeleton-cell skeleton-cell--amount" />
        <div class="skeleton-cell skeleton-cell--badge" />
        <div class="skeleton-cell skeleton-cell--action" />
      </div>
    </div>

    <!-- Erreur -->
    <Transition name="fade">
      <div v-if="erreur" class="error-banner">
        <div class="error-icon-wrap">
          <i class="pi pi-exclamation-triangle" />
        </div>
        <div class="error-content">
          <span class="error-title">Erreur de recherche</span>
          <span class="error-message">{{ erreur }}</span>
        </div>
        <button class="error-close" @click="erreur = null">
          <i class="pi pi-times" />
        </button>
      </div>
    </Transition>

    <!-- Résultats -->
    <Transition name="fade">
      <div v-if="!loading && resultats.length > 0" class="resultats-section">
        <div class="resultats-header">
          <div class="resultats-header-left">
            <i class="pi pi-list" />
            <span class="resultats-count">
              {{ resultats.length }} dossier{{ resultats.length > 1 ? 's' : '' }} trouvé{{ resultats.length > 1 ? 's' : '' }}
            </span>
          </div>
        </div>
        <table class="resultats-table">
          <thead>
            <tr>
              <th>N° Prêt</th>
              <th>Emprunteur</th>
              <th class="col-right">Montant</th>
              <th>État</th>
              <th class="col-action">Action</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(dossier, index) in resultats"
              :key="dossier.id"
              class="result-row"
              :style="{ animationDelay: `${index * 50}ms` }"
              @click="consulterDossier(dossier.id)"
            >
              <td class="cell-nopret">
                <span class="nopret-badge">
                  <i class="pi pi-file" />
                  {{ dossier.noPret }}
                </span>
              </td>
              <td class="cell-emprunteur">
                <i class="pi pi-user cell-icon" />
                {{ dossier.emprunteur }}
              </td>
              <td class="cell-montant">{{ dossier.montantPret }}</td>
              <td>
                <span class="badge-etat" :class="badgeClass(dossier.codeEtat)">
                  <span class="badge-dot" />
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
                  <span class="btn-consulter-label">Ouvrir</span>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </Transition>

    <!-- État initial (pas encore de recherche) -->
    <div v-if="!loading && !rechercheLancee" class="initial-state">
      <div class="initial-illustration">
        <i class="pi pi-search" />
      </div>
      <p class="initial-title">Recherchez un dossier de prêt</p>
      <p class="initial-hint">
        Saisissez un numéro de contrat souscrit (ex: DD04063627) puis cliquez sur Rechercher
      </p>
    </div>

    <!-- Aucun résultat -->
    <div v-if="!loading && rechercheLancee && resultats.length === 0 && !erreur" class="no-results">
      <div class="no-results-illustration">
        <i class="pi pi-inbox" />
      </div>
      <p class="no-results-title">Aucun dossier trouvé</p>
      <p class="no-results-hint">Aucun dossier ne correspond à vos critères de recherche.</p>
      <button class="btn-retry" @click="reinitialiser">
        <i class="pi pi-refresh" />
        Nouvelle recherche
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getPretService } from '@/services/pretService'
import { usePretStore } from '@/stores/pretStore'

const router = useRouter()
const store = usePretStore()

const loading = ref(false)

// État persisté dans le store (survit à la navigation)
const numeroPret = computed({
  get: () => store.rechercheNumeroPret,
  set: (v) => { store.rechercheNumeroPret = v },
})
const resultats = computed(() => store.rechercheResultats)
const rechercheLancee = computed(() => store.rechercheLancee)
const erreur = computed({
  get: () => store.rechercheErreur,
  set: (v) => { store.rechercheErreur = v },
})

async function rechercher() {
  if (!numeroPret.value.trim()) return

  loading.value = true
  store.rechercheErreur = null
  store.rechercheLancee = true

  try {
    const service = await getPretService()
    const response = service.rechercherPret
      ? await service.rechercherPret(numeroPret.value.trim())
      : await service.listerDossiers()

    if (response.success) {
      store.rechercheResultats = response.data
    } else {
      store.rechercheErreur = response.message || 'Erreur lors de la recherche'
    }
  } catch (e) {
    store.rechercheErreur = 'Erreur lors de la recherche'
    console.error(e)
  } finally {
    loading.value = false
  }
}

function reinitialiser() {
  store.rechercheNumeroPret = ''
  store.rechercheResultats = []
  store.rechercheLancee = false
  store.rechercheErreur = null
}

function consulterDossier(id: string) {
  store.setActiveSidebarItem('consultation')
  router.push(`/consultation/${id}/donnees-generales`)
}

function badgeClass(codeEtat: string): string {
  if (codeEtat.startsWith('40') || codeEtat.startsWith('AA')) return 'badge-gestion'
  if (codeEtat.startsWith('30') || codeEtat.startsWith('DB')) return 'badge-deblocage'
  if (codeEtat.startsWith('20')) return 'badge-instruction'
  return ''
}
</script>

<style scoped lang="scss">
.recherche-view {
  max-width: 1400px;
}

// ── Header ──
.view-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $space-xl;
}

.view-header-left {
  display: flex;
  align-items: center;
  gap: $space-md;
}

.header-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: $border-radius-lg;
  background: linear-gradient(135deg, rgba($primary, 0.12) 0%, rgba($primary-ciel, 0.12) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.header-icon {
  font-size: 16px;
  color: $primary;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.view-title {
  font-size: 16px;
  font-weight: $font-weight-bold;
  color: var(--text-primary);
  line-height: 1.2;
}

.view-subtitle {
  font-size: $font-size-xs;
  color: var(--text-muted);
  line-height: 1.3;
}

// ── Formulaire de recherche ──
.search-form {
  background: var(--bg-surface);
  border: 1px solid var(--border-main);
  border-radius: $border-radius-lg;
  padding: $space-xl $space-xxl;
  margin-bottom: $space-xl;
  box-shadow: $shadow-sm;
}

.field-label {
  display: flex;
  align-items: center;
  gap: $space-xs;
  font-size: 10px;
  font-weight: $font-weight-bold;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.6px;
  margin-bottom: $space-sm;

  .pi {
    font-size: 10px;
    opacity: 0.5;
  }
}

.search-row {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.field-input {
  flex: 1;
  padding: 12px $space-lg;
  font-size: $font-size-md;
  font-family: inherit;
  color: var(--text-primary);
  background: var(--bg-elevated);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  transition: all $transition-base;

  &:focus {
    border-color: var(--sidebar-accent, $primary);
    outline: none;
    box-shadow: 0 0 0 3px rgba($primary, 0.1);
    background: var(--bg-surface);
  }

  &::placeholder {
    color: var(--text-muted);
    font-weight: $font-weight-normal;
    font-size: $font-size-sm;
  }
}

.btn-search {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px $space-xxl;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-semibold;
  color: $white;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  border: none;
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;
  white-space: nowrap;
  flex-shrink: 0;

  &:hover:not(:disabled) {
    filter: brightness(1.1);
    box-shadow: 0 2px 8px rgba($primary, 0.35);
  }

  &:active:not(:disabled) {
    filter: brightness(0.95);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .pi {
    font-size: 12px;
  }
}

.btn-reset {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  font-size: 14px;
  color: var(--text-muted);
  background: transparent;
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;
  flex-shrink: 0;

  &:hover {
    color: var(--hover-text);
    border-color: var(--hover-text);
    background: var(--hover-bg);
  }
}

// ── Loading skeleton ──
.loading-skeleton {
  border: 1px solid var(--border-main);
  border-radius: $border-radius-lg;
  overflow: hidden;
}

.skeleton-header {
  height: 44px;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-main);
}

.skeleton-row {
  display: flex;
  align-items: center;
  gap: $space-lg;
  padding: $space-lg $space-xl;
  border-bottom: 1px solid var(--border-light);

  &:last-child {
    border-bottom: none;
  }
}

.skeleton-cell {
  height: 14px;
  border-radius: $border-radius-sm;
  background: linear-gradient(90deg, var(--bg-elevated) 25%, var(--border-light) 50%, var(--bg-elevated) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;

  &--id { width: 120px; }
  &--name { width: 180px; }
  &--amount { width: 100px; margin-left: auto; }
  &--badge { width: 140px; }
  &--action { width: 60px; }
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

// ── Erreur ──
.error-banner {
  display: flex;
  align-items: center;
  gap: $space-md;
  padding: $space-md $space-lg;
  margin-bottom: $space-lg;
  background: $danger-light;
  border: 1px solid rgba($danger, 0.2);
  border-left: 3px solid $danger;
  border-radius: $border-radius;
}

.error-icon-wrap {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba($danger, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .pi {
    font-size: 14px;
    color: $danger;
  }
}

.error-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.error-title {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: $danger;
}

.error-message {
  font-size: $font-size-xs;
  color: rgba($danger, 0.8);
}

.error-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: $space-xs;
  color: rgba($danger, 0.5);
  border-radius: $border-radius-sm;
  transition: all $transition-fast;

  &:hover {
    color: $danger;
    background: rgba($danger, 0.1);
  }
}

// ── Résultats ──
.resultats-section {
  border: 1px solid var(--border-main);
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;
  animation: fadeSlideUp 0.3s ease;
}

@keyframes fadeSlideUp {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.resultats-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-md $space-lg;
  background: var(--bg-elevated);
  border-bottom: 1px solid var(--border-main);
}

.resultats-header-left {
  display: flex;
  align-items: center;
  gap: $space-sm;
  color: var(--text-secondary);

  .pi {
    font-size: 13px;
  }
}

.resultats-count {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
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
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.6px;
    background: var(--bg-surface);
    border-bottom: 2px solid var(--border-main);

    &.col-right {
      text-align: right;
    }
  }

  td {
    padding: $space-md $space-lg;
    color: var(--text-primary);
    border-bottom: 1px solid var(--border-light);
  }
}

.result-row {
  cursor: pointer;
  transition: all $transition-fast;
  animation: fadeSlideUp 0.3s ease backwards;

  &:hover {
    background: var(--hover-bg);

    .btn-consulter {
      opacity: 1;
      border-color: $primary;
    }
  }

  &:last-child td {
    border-bottom: none;
  }
}

.cell-nopret {
  font-weight: $font-weight-semibold;
}

.nopret-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: $primary;
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
  font-size: $font-size-sm;

  .pi {
    font-size: 11px;
    opacity: 0.5;
  }
}

.cell-emprunteur {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cell-icon {
  font-size: 11px;
  color: var(--text-muted);
  opacity: 0.5;
}

.cell-montant {
  font-family: 'SF Mono', 'Fira Code', 'Consolas', monospace;
  text-align: right;
  font-size: $font-size-xs;
  color: var(--text-secondary);
}

.col-action {
  width: 80px;
  text-align: center;
}

// ── Badges ──
.badge-etat {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px $space-sm 3px 6px;
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  border-radius: 10px;
  white-space: nowrap;
  line-height: 1.3;
}

.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.badge-gestion {
  color: $success;
  background: $success-light;

  .badge-dot {
    background: $success;
  }
}

.badge-deblocage {
  color: $warning;
  background: $warning-light;

  .badge-dot {
    background: $warning;
  }
}

.badge-instruction {
  color: $info;
  background: $info-light;

  .badge-dot {
    background: $info;
  }
}

.btn-consulter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: $font-size-xs;
  font-family: inherit;
  font-weight: $font-weight-medium;
  color: $primary;
  background: transparent;
  border: 1px solid var(--border-light);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;
  opacity: 0.6;

  .pi {
    font-size: 11px;
  }

  &:hover {
    background: $primary;
    color: $white;
    border-color: $primary;
    opacity: 1;
  }
}

.btn-consulter-label {
  @media (max-width: 1200px) {
    display: none;
  }
}

// ── État initial ──
.initial-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px $space-xxxl;
  text-align: center;
}

.initial-illustration {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: var(--bg-elevated);
  border: 2px solid var(--border-main);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-xxl;

  .pi {
    font-size: 36px;
    color: var(--text-secondary);
  }
}

.initial-title {
  font-size: 18px;
  font-weight: $font-weight-bold;
  color: var(--text-primary);
  margin-bottom: $space-sm;
}

.initial-hint {
  font-size: $font-size-base;
  color: var(--text-secondary);
  max-width: 440px;
  line-height: 1.6;
}

// ── Aucun résultat ──
.no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px $space-xxxl;
  text-align: center;
}

.no-results-illustration {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: var(--bg-elevated);
  border: 2px solid var(--border-main);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-xxl;

  .pi {
    font-size: 36px;
    color: var(--text-secondary);
  }
}

.no-results-title {
  font-size: 18px;
  font-weight: $font-weight-bold;
  color: var(--text-primary);
  margin-bottom: $space-sm;
}

.no-results-hint {
  font-size: $font-size-base;
  color: var(--text-secondary);
  margin-bottom: $space-xxl;
}

.btn-retry {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px $space-xxl;
  font-size: $font-size-sm;
  font-family: inherit;
  font-weight: $font-weight-medium;
  color: var(--text-primary);
  background: var(--bg-elevated);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  cursor: pointer;
  transition: all $transition-base;

  .pi {
    font-size: 12px;
  }

  &:hover {
    color: var(--hover-text);
    border-color: var(--hover-text);
    background: var(--hover-bg);
  }
}

// ── Transitions ──
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(4px);
}

.fade-leave-to {
  opacity: 0;
}
</style>
