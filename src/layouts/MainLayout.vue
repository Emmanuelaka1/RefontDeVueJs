<template>
  <div class="main-layout" data-testid="main-layout">
    <!-- ═══ Toolbar ═══ -->
    <AppToolbar />

    <!-- ═══ Body: Sidebar + Right Panel ═══ -->
    <div class="main-body">
      <!-- ═══ Sidebar ═══ -->
      <AppSidebar :collapsed="store.sidebarCollapsed" />

      <!-- ═══ Right panel: Tabs + Content ═══ -->
      <div class="right-panel">
        <!-- Tabs -->
        <AppTabs />

        <!-- Content card (blue-bar + content) -->
        <div class="content-card">
          <div class="blue-bar" />
          <div class="content-area" data-testid="content-area">
            <router-view v-slot="{ Component }">
              <Transition name="fade" mode="out-in">
                <component :is="Component" />
              </Transition>
            </router-view>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppToolbar from '@/components/AppToolbar.vue'
import AppSidebar from '@/components/AppSidebar.vue'
import AppTabs from '@/components/AppTabs.vue'
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()
</script>

<style scoped lang="scss">
.main-layout {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 10px;
}

.content-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 2px solid $gray-900;
  border-top: none;
  border-radius: $border-radius-lg;
}

.blue-bar {
  height: $blue-bar-height;
  background: $primary-ciel;
  flex-shrink: 0;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: $space-xl $space-xxl;
  background: $gray-100;
}

// Fade transition
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
