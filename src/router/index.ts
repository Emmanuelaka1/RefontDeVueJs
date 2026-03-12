import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  // ── Recherche = nouveau Home ──
  {
    path: '/',
    redirect: '/recherche',
  },
  {
    path: '/recherche',
    name: 'Recherche',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'RechercheIndex',
        component: () => import('@/views/RechercheView.vue'),
        meta: { title: 'Recherche', tabId: 'recherche', section: 'recherche' },
      },
    ],
  },

  // ── Consultation d'un dossier (avec ID depuis la recherche) ──
  {
    path: '/consultation/:id',
    name: 'ConsultationDossier',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: (to) => `/consultation/${to.params.id}/donnees-generales`,
    children: [
      {
        path: 'donnees-generales',
        name: 'DonneesGenerales',
        component: () => import('@/views/ConsultationView.vue'),
        meta: { title: 'Données générales', tabId: 'donnees-generales', section: 'consultation' },
      },
      {
        path: 'donnees-financieres',
        name: 'DonneesFinancieres',
        component: () => import('@/views/DonneesFinancieresView.vue'),
        meta: { title: 'Données financières', tabId: 'donnees-financieres', section: 'consultation' },
      },
      {
        path: 'paliers',
        name: 'Paliers',
        component: () => import('@/views/PaliersView.vue'),
        meta: { title: 'Paliers', tabId: 'paliers', section: 'consultation' },
      },
      {
        path: 'domiciliation',
        name: 'Domiciliation',
        component: () => import('@/views/DomiciliationView.vue'),
        meta: { title: 'Domiciliation', tabId: 'domiciliation', section: 'consultation' },
      },
    ],
  },

  // ── Consultation sans ID (redirige vers recherche) ──
  {
    path: '/consultation',
    redirect: '/recherche',
  },

  // ── Déblocage ──
  {
    path: '/deblocage',
    name: 'Deblocage',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'DeblocageIndex',
        component: () => import('@/views/DeblocageView.vue'),
        meta: { title: 'Déblocage', section: 'deblocage' },
      },
    ],
  },

  // ── Rbt anticipés ──
  {
    path: '/rbt-anticipes',
    name: 'RbtAnticipes',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'RbtAnticipesIndex',
        component: () => import('@/views/RbtAnticipesView.vue'),
        meta: { title: 'Rbt anticipés', section: 'rbt-anticipes' },
      },
    ],
  },

  // ── Catch-all ──
  {
    path: '/:pathMatch(.*)*',
    redirect: '/recherche',
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Guard pour mettre à jour le titre
router.beforeEach((to) => {
  const title = (to.meta.title as string) || 'Gestion des Prêts'
  document.title = `${title} - SIGAC`
})

export default router
