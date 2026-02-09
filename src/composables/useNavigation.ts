import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePretStore } from '@/stores/pretStore'
import type { TabItem, SidebarItem } from '@/types'

/**
 * Composable pour la navigation (onglets + sidebar)
 */
export function useNavigation() {
  const route = useRoute()
  const router = useRouter()
  const store = usePretStore()

  // ── Onglets ──
  const tabs: TabItem[] = [
    { id: 'donnees-generales', label: 'Données générales', route: '/consultation/donnees-generales' },
    { id: 'donnees-financieres', label: 'Données financières', route: '/consultation/donnees-financieres' },
    { id: 'paliers', label: 'Paliers', route: '/consultation/paliers' },
    { id: 'domiciliation', label: 'Domiciliation', route: '/consultation/domiciliation' },
  ]

  // ── Sidebar ──
  const sidebarItems: SidebarItem[] = [
    { id: 'consultation', label: 'Consultation', route: '/consultation', icon: 'pi-file-edit' },
    { id: 'deblocage', label: 'Déblocage', route: '/deblocage', icon: 'pi-unlock' },
    { id: 'rbt-anticipes', label: 'Rbt anticipés', route: '/rbt-anticipes', icon: 'pi-replay' },
  ]

  // ── Computed ──
  const activeTabId = computed(() => {
    return (route.meta.tabId as string) || 'donnees-generales'
  })

  const activeSidebarId = computed(() => store.activeSidebarItem)

  // ── Actions ──
  function navigateToTab(tab: TabItem) {
    router.push(tab.route)
  }

  function selectSidebarItem(item: SidebarItem) {
    store.setActiveSidebarItem(item.id)
    if (item.route) {
      router.push(item.route)
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
