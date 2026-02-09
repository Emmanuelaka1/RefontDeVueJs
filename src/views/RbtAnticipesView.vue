<template>
  <div class="placeholder-view" data-testid="rbt-anticipes-view">
    <div class="placeholder-header">
      <div class="placeholder-header-left">
        <i class="pi pi-replay placeholder-icon" />
        <div>
          <h2 class="placeholder-title">Remboursements anticipés</h2>
          <p class="placeholder-desc">Contenu de la gestion des remboursements anticipés à implémenter.</p>
        </div>
      </div>
      <span class="badge-coming">À venir</span>
    </div>

    <div class="placeholder-body">
      <div class="skeleton-cards">
        <div v-for="n in 3" :key="n" class="skeleton-card">
          <div class="skeleton-card-label" />
          <div class="skeleton-card-value" />
        </div>
      </div>

      <div class="skeleton-table">
        <div class="skeleton-thead">
          <div v-for="n in 6" :key="n" class="skeleton-th" />
        </div>
        <div v-for="row in 5" :key="row" class="skeleton-tr">
          <div v-for="col in 6" :key="col" class="skeleton-td" />
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
  color: $gray-400;
  margin-top: 2px;
}

.placeholder-title {
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  color: $gray-800;
  margin-bottom: $space-xs;
}

.placeholder-desc {
  font-size: $font-size-sm;
  color: $gray-500;
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
  background: $white;
  border: 1px solid $gray-300;
  border-radius: $border-radius;
  padding: $space-lg;
}

.skeleton-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $space-md;
  margin-bottom: $space-xl;
}

.skeleton-card {
  padding: $space-lg;
  border: 1px solid $gray-200;
  border-radius: $border-radius;
}

.skeleton-card-label {
  height: 10px;
  width: 60%;
  background: $gray-200;
  border-radius: $border-radius-sm;
  margin-bottom: $space-sm;
}

.skeleton-card-value {
  height: 16px;
  width: 40%;
  background: $gray-100;
  border-radius: $border-radius-sm;
}

.skeleton-table {
  width: 100%;
}

.skeleton-thead {
  display: flex;
  gap: $space-md;
  margin-bottom: $space-md;
  padding-bottom: $space-sm;
  border-bottom: 2px solid $gray-200;
}

.skeleton-th {
  flex: 1;
  height: 12px;
  background: $gray-200;
  border-radius: $border-radius-sm;
}

.skeleton-tr {
  display: flex;
  gap: $space-md;
  padding: $space-sm 0;
  border-bottom: 1px solid $gray-100;
}

.skeleton-td {
  flex: 1;
  height: 10px;
  background: $gray-100;
  border-radius: $border-radius-sm;
}
</style>
