import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Configuration globale PrimeVue (theme, ripple, locale, etc.)
import PrimeVue from 'primevue/config'

// Styles PrimeVue : theme visuel + styles de base + icones
import 'primevue/resources/themes/lara-light-blue/theme.css'
import 'primevue/resources/primevue.min.css'
import 'primeicons/primeicons.css'

// Composant racine, routeur et styles globaux de l'application
import App from './App.vue'
import router from './router'
import './assets/global.scss'

const app = createApp(App)

// Enregistrement des plugins Vue
app.use(createPinia())                  // Store global (gestion d'etat)
app.use(router)                         // Routeur (navigation par URL)
app.use(PrimeVue, { ripple: true })     // PrimeVue avec effet ripple sur les boutons

// Note : les composants PrimeVue (InputText, Button, Calendar, etc.)
// sont auto-importes par unplugin-vue-components (voir vite.config.ts)

app.mount('#app')
