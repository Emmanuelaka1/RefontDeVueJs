<template>
  <aside class="app-sidebar" :class="{ collapsed }" data-testid="sidebar">
    <div v-if="!collapsed" class="sidebar-header">
      <span class="sidebar-header-label">Navigation</span>
    </div>
    <nav class="sidebar-nav">
      <div
        v-for="item in sidebarItems"
        :key="item.id"
        class="sidebar-item"
        :class="{ active: activeSidebarId === item.id }"
        :data-testid="`sidebar-item-${item.id}`"
        :title="collapsed ? item.label : undefined"
        @click="selectSidebarItem(item)"
      >
        <span class="sidebar-icon-wrap">
          <i v-if="item.icon" class="pi sidebar-icon" :class="item.icon" />
        </span>
        <span v-if="!collapsed" class="sidebar-label">{{ item.label }}</span>
      </div>
    </nav>
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
  border-right: 3px solid var(--sidebar-border);
  display: flex;
  flex-direction: column;
  user-select: none;
  transition: width $transition-base, min-width $transition-base;
  overflow: hidden;
  z-index: $z-sidebar;
  box-shadow: var(--sidebar-depth);

  &.collapsed {
    width: $sidebar-collapsed-width;
    min-width: $sidebar-collapsed-width;
  }
}

.sidebar-header {
  padding: $space-lg $space-lg $space-xs;
}

.sidebar-header-label {
  font-size: 10px;
  font-weight: $font-weight-bold;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 1.2px;
}

.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: $space-xs $space-sm;
  gap: 2px;

  .collapsed & {
    padding: $space-sm $space-xs;
    align-items: center;
  }
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: 7px $space-md;
  font-size: $font-size-md;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all $transition-base;
  border-radius: $border-radius-lg;
  white-space: nowrap;
  overflow: hidden;
  position: relative;

  .collapsed & {
    padding: $space-sm;
    justify-content: center;
    border-radius: $border-radius;
  }

  &:hover {
    background: var(--hover-bg);
    color: var(--hover-text);
  }

  &.active {
    background: var(--sidebar-active-bg);
    color: var(--sidebar-active-text);
    font-weight: $font-weight-semibold;

    // Floating left accent strip
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 55%;
      border-radius: 0 3px 3px 0;
      background: var(--sidebar-border);
    }

    .collapsed & {
      &::before {
        left: 50%;
        top: auto;
        bottom: 0;
        transform: translateX(-50%);
        width: 55%;
        height: 3px;
        border-radius: 3px 3px 0 0;
      }
    }
  }
}

.sidebar-icon-wrap {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $border-radius;
  flex-shrink: 0;
  transition: all $transition-base;

  .collapsed & {
    width: 36px;
    height: 36px;
    border-radius: 50%;
  }
}

.sidebar-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.sidebar-label {
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: $font-size-sm;
}
</style>
