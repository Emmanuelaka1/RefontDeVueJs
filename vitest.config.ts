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

    // Configuration de la couverture de code (npm run test:unit:coverage)
    coverage: {
      provider: 'v8',                          // Moteur de couverture V8 (rapide, natif)
      reporter: ['text', 'json', 'html'],       // Formats de rapport generes
      include: ['src/**/*.{ts,vue}'],           // Fichiers sources a analyser
      exclude: ['src/main.ts', 'src/types/**'], // Exclut le bootstrap et les types purs
    },
  },
})
