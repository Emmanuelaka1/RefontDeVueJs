<template>
  <header class="app-toolbar" data-testid="toolbar">
    <div class="toolbar-left">
      <div class="brand-mark">
        <i class="pi pi-building" />
      </div>
      <div class="toolbar-titles">
        <span class="toolbar-title">Template</span>
        <span class="toolbar-sep">&middot;</span>
        <span class="toolbar-subtitle">Refont Dev Vue3</span>
      </div>
      <button
        class="sidebar-toggle-btn"
        :class="{ 'is-collapsed': store.sidebarCollapsed }"
        data-testid="hamburger-btn"
        @click="store.toggleSidebar()"
      >
        <Transition name="icon-flip" mode="out-in">
          <i v-if="store.sidebarCollapsed" key="bars" class="pi pi-bars" />
          <i v-else key="close" class="pi pi-times" />
        </Transition>
      </button>
    </div>
    <div class="toolbar-right">
      <button class="toolbar-icon-btn" data-testid="dark-mode-toggle" @click="store.toggleDarkMode()">
        <Transition name="icon-flip" mode="out-in">
          <i v-if="store.darkMode" key="sun" class="pi pi-sun" />
          <i v-else key="moon" class="pi pi-moon" />
        </Transition>
      </button>
      <span class="env-badge"><span class="env-dot" />DEV</span>
      <div class="user-block">
        <span class="user-name">{{ store.currentUser.prenom }} {{ store.currentUser.nom }}</span>
        <span class="user-avatar">{{ store.currentUser.initiales }}</span>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()
</script>

<style scoped lang="scss">
.app-toolbar {
  height: $toolbar-height;
  min-height: $toolbar-height;
  background: var(--toolbar-bg);
  background-image: linear-gradient(180deg, rgba(255, 255, 255, 0.06) 0%, transparent 100%);
  color: $white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $space-lg;
  flex-shrink: 0;
  box-shadow: var(--toolbar-shadow);
  z-index: $z-toolbar;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, var(--toolbar-accent) 0%, transparent 70%);
    opacity: 0.8;
  }
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: $space-md;
}

.brand-mark {
  width: 32px;
  height: 32px;
  border-radius: $border-radius-lg;
  background: var(--brand-mark-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  flex-shrink: 0;
  transition: background $transition-base;

  &:hover {
    background: rgba(255, 255, 255, 0.18);
  }
}

.toolbar-titles {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.toolbar-title {
  font-size: 15px;
  font-weight: $font-weight-bold;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.toolbar-sep {
  opacity: 0.4;
  font-size: 16px;
}

.toolbar-subtitle {
  font-size: $font-size-sm;
  font-weight: $font-weight-normal;
  opacity: 0.6;
  white-space: nowrap;
}

.sidebar-toggle-btn {
  background: none;
  border: none;
  color: $white;
  font-size: 14px;
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background $transition-fast, transform $transition-fast, opacity $transition-fast;
  overflow: hidden;
  opacity: 0.6;

  &:hover {
    background: rgba(255, 255, 255, 0.12);
    opacity: 1;
  }

  &:active {
    transform: scale(0.9);
  }

  &.is-collapsed {
    order: -1;
  }
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: $space-md;
}

.toolbar-icon-btn {
  background: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: $white;
  font-size: 14px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-base;
  overflow: hidden;

  &:hover {
    background: rgba(255, 255, 255, 0.12);
    border-color: rgba(255, 255, 255, 0.25);
    transform: rotate(12deg);
  }
}

.env-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 10px;
  font-weight: $font-weight-bold;
  padding: 3px 10px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  letter-spacing: 0.8px;
}

.env-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4ade80;
  flex-shrink: 0;
  animation: pulse-dot 2s ease-in-out infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.user-block {
  display: flex;
  align-items: center;
  gap: $space-sm;
  padding: 3px 4px 3px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: background $transition-base;

  &:hover {
    background: rgba(255, 255, 255, 0.1);
  }
}

.user-name {
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: 1.5px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: $font-weight-bold;
  letter-spacing: 0.5px;
}

// Icon flip transition
.icon-flip-enter-active,
.icon-flip-leave-active {
  transition: all 0.25s ease;
}
.icon-flip-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.5);
}
.icon-flip-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.5);
}
</style>
