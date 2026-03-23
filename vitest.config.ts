import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitest.dev/config/
export default defineConfig({
  plugins: [
    // Permet a Vitest de compiler les fichiers .vue dans les tests
    vue(),
  ],

  resolve: {
    // Meme alias '@' que dans vite.config.ts pour coherence dans les tests
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },

  test: {
    // Rend les fonctions de test (describe, it, expect) disponibles sans import
    globals: true,

    // Simule un environnement navigateur (DOM) pour tester les composants Vue
    environment: 'jsdom',

    // Cherche les fichiers de test dans tests/unit/ (*.test.ts ou *.spec.ts)
    include: ['tests/unit/**/*.{test,spec}.{ts,tsx}'],

    // Couverture Istanbul
    coverage: {
      provider: 'istanbul',
      reportsDirectory: '/sessions/cool-zen-pascal/coverage-out',
      include: [
        'src/services/**',
        'src/stores/**',
        'src/composables/**',
        'src/components/**',
        'src/views/**',
        'src/data/**',
      ],
      exclude: [
        'src/api/generated/**',
        'src/assets/**',
        'src/main.ts',
        'src/App.vue',
        'src/router/**',
        'src/layouts/**',
        'src/env.d.ts',
        'src/types/**',
        // FormField.vue : composant pur présentation (defineProps uniquement, 0 logique métier)
        'src/components/FormField.vue',
      ],
      thresholds: {
        statements: 80,
        branches: 80,
        functions: 80,
        lines: 80,
      },
    },
  },
})
