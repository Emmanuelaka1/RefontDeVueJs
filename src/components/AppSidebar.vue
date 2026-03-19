<template>
  <aside class="app-sidebar" :class="{ collapsed }" data-testid="sidebar">
    <!-- Header -->
    <div v-if="!collapsed" class="sidebar-header">
      <span class="sidebar-header-label">Menu</span>
    </div>

    <!-- Navigation -->
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
        <Transition name="label-fade">
          <span v-if="!collapsed" class="sidebar-label">{{ item.label }}</span>
        </Transition>
      </div>
    </nav>

    <!-- Footer -->
    <div v-if="!collapsed" class="sidebar-footer">
      <div class="sidebar-footer-content">
        <i class="pi pi-info-circle" />
        <span>SIGAC v1.0</span>
      </div>
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
  border-right: 5px solid var(--sidebar-border);
  display: flex;
  flex-direction: column;
  user-select: none;
  transition: width $transition-slow, min-width $transition-slow;
  overflow: hidden;
  z-index: $z-sidebar;

  &.collapsed {
    width: $sidebar-collapsed-width;
    min-width: $sidebar-collapsed-width;
  }
}

// ── Header ──
.sidebar-header {
  padding: $space-lg $space-xl $space-sm;
}

.sidebar-header-label {
  font-size: 10px;
  font-weight: $font-weight-bold;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 1.5px;
}

// ── Navigation ──
.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: $space-xs $space-sm;
  gap: $space-xxs;

  .collapsed & {
    padding: $space-sm $space-xs;
    align-items: center;
  }
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px $space-lg;
  font-size: $font-size-sm;
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

  &:hover:not(.active) {
    background: var(--hover-bg);
    color: var(--hover-text);

    .sidebar-icon-wrap {
      background: var(--hover-bg);
    }
  }

  &.active {
    background: var(--sidebar-active-bg);
    color: var(--sidebar-active-text);
    font-weight: $font-weight-semibold;

    .sidebar-icon-wrap {
      background: var(--sidebar-icon-active-bg);
      color: var(--sidebar-icon-active-text);
      box-shadow: var(--sidebar-icon-active-shadow);
    }

    .sidebar-icon {
      color: var(--sidebar-icon-active-text);
    }

    // Left accent bar
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 60%;
      border-radius: 0 4px 4px 0;
      background: var(--sidebar-accent);
    }

    .collapsed & {
      &::before {
        left: 50%;
        top: auto;
        bottom: 0;
        transform: translateX(-50%);
        width: 60%;
        height: 3px;
        border-radius: 4px 4px 0 0;
      }
    }
  }
}

.sidebar-icon-wrap {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $border-radius-lg;
  flex-shrink: 0;
  transition: all $transition-base;
  background: transparent;

  .collapsed & {
    width: 38px;
    height: 38px;
    border-radius: 10px;
  }
}

.sidebar-icon {
  font-size: 15px;
  flex-shrink: 0;
  transition: color $transition-base;
}

.sidebar-label {
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: $font-size-sm;
  letter-spacing: 0.1px;
}

// ── Footer ──
.sidebar-footer {
  padding: $space-md $space-lg;
  border-top: 1px solid var(--border-light);
}

.sidebar-footer-content {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  color: var(--text-muted);
  letter-spacing: 0.3px;

  .pi {
    font-size: 11px;
    opacity: 0.5;
  }
}

// ── Transitions ──
.label-fade-enter-active,
.label-fade-leave-active {
  transition: opacity $transition-base;
}

.label-fade-enter-from,
.label-fade-leave-to {
  opacity: 0;
}
</style>
