import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/consultation/donnees-generales',
  },
  {
    path: '/consultation',
    name: 'Consultation',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/consultation/donnees-generales',
    children: [
      {
        path: 'donnees-generales',
        name: 'DonneesGenerales',
        component: () => import('@/views/ConsultationView.vue'),
        meta: { title: 'Données générales', tabId: 'donnees-generales' },
      },
      {
        path: 'donnees-financieres',
        name: 'DonneesFinancieres',
        component: () => import('@/views/DonneesFinancieresView.vue'),
        meta: { title: 'Données financières', tabId: 'donnees-financieres' },
      },
      {
        path: 'paliers',
        name: 'Paliers',
        component: () => import('@/views/PaliersView.vue'),
        meta: { title: 'Paliers', tabId: 'paliers' },
      },
      {
        path: 'domiciliation',
        name: 'Domiciliation',
        component: () => import('@/views/DomiciliationView.vue'),
        meta: { title: 'Domiciliation', tabId: 'domiciliation' },
      },
    ],
  },
  {
    path: '/deblocage',
    name: 'Deblocage',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'DeblocageIndex',
        component: () => import('@/views/DeblocageView.vue'),
        meta: { title: 'Déblocage' },
      },
    ],
  },
  {
    path: '/rbt-anticipes',
    name: 'RbtAnticipes',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'RbtAnticipesIndex',
        component: () => import('@/views/RbtAnticipesView.vue'),
        meta: { title: 'Rbt anticipés' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
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
