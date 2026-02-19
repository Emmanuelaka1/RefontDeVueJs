import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { PrimeVueResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'
import istanbul from 'vite-plugin-istanbul'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [
      // Support des Single File Components (.vue)
      vue(),

      // Auto-import des composants PrimeVue (InputText, Button, Calendar, etc.)
      // Plus besoin de les importer manuellement ni de les enregistrer dans main.ts
      // Genere automatiquement le fichier components.d.ts pour le support TypeScript
      Components({
        resolvers: [PrimeVueResolver()],
      }),

      // Instrumentation Istanbul pour la couverture de code Cypress
      // Actif uniquement en mode test (CYPRESS=true ou NODE_ENV=test)
      istanbul({
        include: ['src/**/*'],
        exclude: [
          'node_modules/**',
          'cypress/**',
          // Jamais appelé en mode dev/test (nécessite un vrai backend REST)
          'src/services/pretService.http.ts',
          // Macros compilateur Vue (withDefaults/defineProps) : Istanbul ne peut pas
          // instrumenter ces constructs car ils sont éliminés par le compilateur Vue
          'src/components/FormField.vue',
          // Templates Vue purs : Istanbul sous-compte les lignes de template compilées
          // car les render functions générées ne correspondent pas aux lignes source
          'src/components/SectionDates.vue',
          'src/components/SectionDonneesGenerales.vue',
          'src/components/SectionDonneesPret.vue',
        ],
        cypress: true,
        requireEnv: false,
      }),
    ],

    resolve: {
      // Alias '@' pour acceder au dossier src/ (ex: import x from '@/stores/pretStore')
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },

    css: {
      preprocessorOptions: {
        scss: {
          // Utilise l'API Sass moderne (evite le warning "legacy-js-api deprecated")
          // Necessite le package 'sass-embedded' au lieu de 'sass'
          api: 'modern-compiler',

          // Injecte automatiquement les variables SCSS ($primary, $gray-500, etc.)
          // dans chaque fichier .scss et <style lang="scss"> sans import manuel
          additionalData: `@use "@/assets/variables.scss" as *;`,
        },
      },
    },

    server: {
      port: 3000,
      open: true, // Ouvre automatiquement le navigateur au lancement (npm run dev)

      // Proxy pour rediriger les appels /api vers le backend
      // Evite les problemes CORS en developpement
      proxy: {
        '/api': {
          target: env.VITE_API_TARGET || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
        },
      },
    },
  }
})
