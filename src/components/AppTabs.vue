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
  background: var(--tabs-bg);
  padding: $space-sm $space-sm 0;
  gap: $space-xs;
}

.tab-item {
  padding: $space-sm $space-xl;
  font-size: $font-size-md;
  font-weight: $font-weight-medium;
  font-family: inherit;
  color: var(--text-secondary);
  cursor: pointer;
  background: transparent;
  border: 1px solid var(--border-main);
  border-radius: $border-radius $border-radius 0 0;
  transition: all $transition-base;
  white-space: nowrap;
  outline: none;

  &:hover:not(.active) {
    background: var(--hover-bg);
    color: var(--hover-text);
  }

  &.active {
    color: var(--tab-active-text);
    font-weight: $font-weight-semibold;
    background: var(--tab-active-bg);
    border-color: var(--tab-active-border);
    border-bottom-color: transparent;
  }
}
</style>
