<template>
  <router-view />
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { usePretStore } from '@/stores/pretStore'

const store = usePretStore()

// Dark mode : bascule la classe sur <html> pour activer les CSS custom properties
watch(() => store.darkMode, (isDark) => {
  console.log('[dark-mode] toggle =>', isDark)
  document.documentElement.classList.toggle('dark-mode', isDark)
}, { immediate: true })
</script>
