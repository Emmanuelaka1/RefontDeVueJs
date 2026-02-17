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
  background: var(--sidebar-bg);
  border-right: 8px solid var(--sidebar-border);
  display: flex;
  flex-direction: column;
  user-select: none;
  transition: width $transition-base, min-width $transition-base;
  overflow: hidden;
  z-index: $z-sidebar;
  padding-top: $space-sm;
  box-shadow: var(--sidebar-depth);

  &.collapsed {
    width: $sidebar-collapsed-width;
    min-width: $sidebar-collapsed-width;
  }
}

.sidebar-section-header {
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  color: var(--text-muted);
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
  margin: 1px $space-xs 1px 0;
  font-size: $font-size-md;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all $transition-base;
  border-left: 3px solid transparent;
  border-radius: 0 $border-radius $border-radius 0;
  white-space: nowrap;
  overflow: hidden;
  position: relative;

  .collapsed & {
    justify-content: center;
    padding: $space-sm 0;
    margin: 1px 0;
    border-left: none;
    border-bottom: 2px solid transparent;
    border-radius: 0;
  }

  &:hover {
    background: var(--hover-bg);
    color: var(--hover-text);
    border-left-color: var(--sidebar-border);

    .collapsed & {
      border-left-color: transparent;
    }
  }

  &.active {
    background: var(--sidebar-active-bg);
    color: var(--sidebar-active-text);
    font-weight: $font-weight-semibold;
    border-left-color: var(--sidebar-border);

    .collapsed & {
      border-left-color: transparent;
      border-bottom-color: var(--sidebar-border);
    }
  }
}

.sidebar-icon {
  font-size: 18px;
  flex-shrink: 0;
  transition: transform $transition-fast;

  .sidebar-item:hover & {
    transform: translateX(1px);
  }

  .collapsed .sidebar-item:hover & {
    transform: translateY(-1px);
  }
}

.sidebar-label {
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
