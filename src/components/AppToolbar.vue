<template>
  <header class="app-toolbar" data-testid="toolbar">
    <div class="toolbar-left">
      <button class="hamburger-btn" data-testid="hamburger-btn" @click="store.toggleSidebar()">
        <i class="pi pi-bars" />
      </button>
      <span class="toolbar-divider" />
      <div class="toolbar-titles">
        <span class="toolbar-title">SIGAC</span>
        <span class="toolbar-subtitle">Gestion des Prêts</span>
      </div>
    </div>
    <div class="toolbar-right">
      <span class="env-badge">DEV</span>
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
  background: $primary;
  color: $white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $space-md;
  flex-shrink: 0;
  box-shadow: $shadow-toolbar;
  z-index: $z-toolbar;
  position: relative;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.toolbar-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
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
  transition: background $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.15);
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
}

.toolbar-subtitle {
  font-size: $font-size-sm;
  font-weight: $font-weight-normal;
  opacity: 0.8;
  white-space: nowrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.env-badge {
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  padding: 2px $space-sm;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: $border-radius-sm;
  letter-spacing: 0.5px;
}

.user-name {
  font-size: $font-size-sm;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
}
</style>
