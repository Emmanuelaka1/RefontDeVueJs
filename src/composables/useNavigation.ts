import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePretStore } from '@/stores/pretStore'
import type { TabItem, SidebarItem } from '@/types'

/**
 * Composable pour la navigation (onglets + sidebar).
 * Les onglets sont dynamiques : affichés uniquement en consultation/:id.
 */
export function useNavigation() {
  const route = useRoute()
  const router = useRouter()
  const store = usePretStore()

  // ── Onglets consultation (routes relatives, résolues dynamiquement) ──
  const consultationTabs: TabItem[] = [
    { id: 'donnees-generales', label: 'Données générales', route: 'donnees-generales' },
    { id: 'donnees-financieres', label: 'Données financières', route: 'donnees-financieres' },
    { id: 'paliers', label: 'Paliers', route: 'paliers' },
    { id: 'domiciliation', label: 'Domiciliation', route: 'domiciliation' },
  ]

  // ── Tabs dynamiques selon la section courante ──
  const tabs = computed<TabItem[]>(() => {
    const section = route.meta.section as string
    if (section === 'consultation') {
      const dossierId = route.params.id as string
      return consultationTabs.map((t) => ({
        ...t,
        route: `/consultation/${dossierId}/${t.route}`,
      }))
    }
    return []
  })

  // ── Sidebar ──
  const sidebarItems: SidebarItem[] = [
    { id: 'recherche', label: 'Recherche', route: '/recherche', icon: 'pi-search' },
    { id: 'consultation', label: 'Consultation', route: '/consultation', icon: 'pi-file-edit' },
    { id: 'deblocage', label: 'Déblocage', route: '/deblocage', icon: 'pi-unlock' },
    { id: 'rbt-anticipes', label: 'Rbt anticipés', route: '/rbt-anticipes', icon: 'pi-replay' },
  ]

  // ── Computed ──
  const activeTabId = computed(() => {
    return (route.meta.tabId as string) || ''
  })

  const activeSidebarId = computed(() => {
    const section = route.meta.section as string
    return section || store.activeSidebarItem
  })

  // ── Actions ──
  function navigateToTab(tab: TabItem) {
    router.push(tab.route)
  }

  function selectSidebarItem(item: SidebarItem) {
    store.setActiveSidebarItem(item.id)
    if (item.route) {
      if (item.id === 'consultation') {
        // Rediriger vers le dossier courant ou vers la recherche
        if (store.dossierCourant?.id) {
          router.push(`/consultation/${store.dossierCourant.id}/donnees-generales`)
        } else {
          router.push('/recherche')
        }
      } else {
        router.push(item.route)
      }
    }
  }

  return {
    tabs,
    sidebarItems,
    activeTabId,
    activeSidebarId,
    navigateToTab,
    selectSidebarItem,
  }
}
