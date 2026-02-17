<template>
  <header class="app-toolbar" data-testid="toolbar">
    <div class="toolbar-left">
      <button class="hamburger-btn" data-testid="hamburger-btn" @click="store.toggleSidebar()">
        <i class="pi pi-bars" />
      </button>
      <span class="toolbar-divider" />
      <div class="toolbar-titles">
        <span class="toolbar-title">Template</span>
        <span class="toolbar-subtitle">Refont Dev Vue3</span>
      </div>
    </div>
    <div class="toolbar-right">
      <button class="theme-toggle" data-testid="dark-mode-toggle" @click="store.toggleDarkMode()">
        <Transition name="icon-flip" mode="out-in">
          <i v-if="store.darkMode" key="sun" class="pi pi-sun" />
          <i v-else key="moon" class="pi pi-moon" />
        </Transition>
      </button>
      <span class="toolbar-divider" />
      <span class="env-badge"><span class="env-dot" />DEV</span>
      <span class="toolbar-divider" />
      <span class="user-name">{{ store.currentUser.prenom }} {{ store.currentUser.nom }}</span>
      <span class="user-avatar">{{ store.currentUser.initiales }}</span>
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
  color: $white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $space-md;
  flex-shrink: 0;
  box-shadow: var(--toolbar-shadow);
  z-index: $z-toolbar;
  position: relative;

  // Accent line at the bottom
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, var(--toolbar-accent) 0%, transparent 80%);
  }
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.15);
  flex-shrink: 0;
}

.hamburger-btn {
  background: none;
  border: none;
  color: $white;
  font-size: 18px;
  cursor: pointer;
  padding: $space-xs 6px;
  border-radius: $border-radius;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background $transition-fast, transform $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.15);
  }

  &:active {
    transform: scale(0.92);
  }
}

.toolbar-titles {
  display: flex;
  align-items: baseline;
  gap: $space-sm;
}

.toolbar-title {
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

.toolbar-subtitle {
  font-size: $font-size-sm;
  font-weight: $font-weight-normal;
  opacity: 0.7;
  white-space: nowrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.env-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  padding: 2px $space-sm;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: $border-radius-sm;
  letter-spacing: 0.5px;
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
  50% { opacity: 0.35; }
}

.user-name {
  font-size: $font-size-sm;
}

.theme-toggle {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: $white;
  font-size: 14px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all $transition-base;
  overflow: hidden;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
    transform: rotate(15deg);
  }
}

// Icon flip transition for dark mode toggle
.icon-flip-enter-active,
.icon-flip-leave-active {
  transition: all 0.25s ease;
}
.icon-flip-enter-from {
  opacity: 0;
  transform: rotate(-90deg) scale(0.6);
}
.icon-flip-leave-to {
  opacity: 0;
  transform: rotate(90deg) scale(0.6);
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  border: 1.5px solid rgba(255, 255, 255, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  transition: border-color $transition-base;

  &:hover {
    border-color: rgba(255, 255, 255, 0.6);
  }
}
</style>
