<template>
  <nav class="app-tabs" data-testid="tabs-nav">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      class="tab-item"
      :class="{ active: activeTabId === tab.id }"
      :data-testid="`tab-${tab.id}`"
      @click="navigateToTab(tab)"
    >
      {{ tab.label }}
    </button>
  </nav>
</template>

<script setup lang="ts">
import { useNavigation } from '@/composables/useNavigation'

const { tabs, activeTabId, navigateToTab } = useNavigation()
</script>

<style scoped lang="scss">
.app-tabs {
  display: flex;
  align-items: stretch;
  background: $white;
  padding: $space-sm $space-sm 0;
  gap: $space-xs;
}

.tab-item {
  padding: $space-sm $space-xl;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  font-family: inherit;
  color: $gray-600;
  cursor: pointer;
  background: transparent;
  border: 1px solid $border-color;
  border-radius: $border-radius $border-radius 0 0;
  transition: all $transition-fast;
  white-space: nowrap;
  outline: none;

  &:hover {
    background: $primary-light;
    color: $primary-dark;
  }

  &.active {
    color: $primary;
    font-weight: $font-weight-semibold;
    background: $white;
    border-color: $primary;
    border-bottom-color: transparent;
  }
}
</style>
