<template>
  <div class="placeholder-view" data-testid="donnees-financieres-view">
    <div class="placeholder-header">
      <div class="placeholder-header-left">
        <i class="pi pi-chart-line placeholder-icon" />
        <div>
          <h2 class="placeholder-title">Données financières</h2>
          <p class="placeholder-desc">Contenu des données financières du prêt à implémenter.</p>
        </div>
      </div>
      <span class="badge-coming">À venir</span>
    </div>

    <div class="placeholder-body">
      <div class="skeleton-table">
        <div class="skeleton-thead">
          <div v-for="n in 5" :key="n" class="skeleton-th" />
        </div>
        <div v-for="row in 4" :key="row" class="skeleton-tr">
          <div v-for="col in 5" :key="col" class="skeleton-td" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.placeholder-view {
  max-width: 1400px;
}

.placeholder-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: $space-lg;
}

.placeholder-header-left {
  display: flex;
  align-items: flex-start;
  gap: $space-md;
}

.placeholder-icon {
  font-size: 24px;
  color: var(--text-muted);
  margin-top: 2px;
}

.placeholder-title {
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  color: var(--text-primary);
  margin-bottom: $space-xs;
}

.placeholder-desc {
  font-size: $font-size-sm;
  color: var(--text-muted);
}

.badge-coming {
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  color: $warning;
  background: $warning-light;
  padding: 2px $space-sm;
  border-radius: $border-radius-sm;
  white-space: nowrap;
}

.placeholder-body {
  background: var(--bg-surface);
  border: 1px solid var(--border-main);
  border-radius: $border-radius;
  padding: $space-lg;
}

.skeleton-table {
  width: 100%;
}

.skeleton-thead {
  display: flex;
  gap: $space-md;
  margin-bottom: $space-md;
  padding-bottom: $space-sm;
  border-bottom: 2px solid var(--border-light);
}

.skeleton-th {
  flex: 1;
  height: 12px;
  background: var(--bg-chevron);
  border-radius: $border-radius-sm;
}

.skeleton-tr {
  display: flex;
  gap: $space-md;
  padding: $space-sm 0;
  border-bottom: 1px solid var(--border-light);
}

.skeleton-td {
  flex: 1;
  height: 10px;
  background: var(--bg-elevated);
  border-radius: $border-radius-sm;
}
</style>
