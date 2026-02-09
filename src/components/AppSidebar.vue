<template>
  <aside class="app-sidebar" :class="{ collapsed }" data-testid="sidebar">
    <!--div class="sidebar-section-header" v-if="!collapsed">Navigation</div -->
    <div
      v-for="item in sidebarItems"
      :key="item.id"
      class="sidebar-item"
      :class="{ active: activeSidebarId === item.id }"
      :data-testid="`sidebar-item-${item.id}`"
      :title="collapsed ? item.label : undefined"
      @click="selectSidebarItem(item)"
    >
      <i v-if="item.icon" class="pi sidebar-icon" :class="item.icon" />
      <span v-if="!collapsed" class="sidebar-label">{{ item.label }}</span>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { useNavigation } from '@/composables/useNavigation'

defineProps<{
  collapsed?: boolean
}>()

const { sidebarItems, activeSidebarId, selectSidebarItem } = useNavigation()
</script>

<style scoped lang="scss">
.app-sidebar {
  width: $sidebar-width;
  min-width: $sidebar-width;
  background: $white;
  border-right: 8px solid $primary-ciel;
  display: flex;
  flex-direction: column;
  user-select: none;
  transition: width $transition-base, min-width $transition-base;
  overflow: hidden;
  z-index: $z-sidebar;
  padding-top: $space-sm;

  &.collapsed {
    width: $sidebar-collapsed-width;
    min-width: $sidebar-collapsed-width;
  }
}

.sidebar-section-header {
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  color: $gray-400;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: $space-sm $space-lg;
  white-space: nowrap;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: $space-sm $space-lg;
  font-size: $font-size-sm;
  color: $gray-600;
  cursor: pointer;
  transition: all $transition-fast;
  border-left: 3px solid transparent;
  white-space: nowrap;
  overflow: hidden;

  .collapsed & {
    justify-content: center;
    padding: $space-sm 0;
    border-left: none;
    border-bottom: 2px solid transparent;
  }

  &:hover {
    background: $primary-light;
    color: $primary-dark;
  }

  &.active {
    background: $primary-light;
    color: $primary;
    font-weight: $font-weight-semibold;
    border-left-color: $primary;

    .collapsed & {
      border-left-color: transparent;
      border-bottom-color: $primary;
    }
  }
}

.sidebar-icon {
  font-size: $font-size-md;
  flex-shrink: 0;
}

.sidebar-label {
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
