import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import type {
  DossierPret,
  DonneesGenerales,
  DonneesPret,
  DatesPret,
  SectionState,
  UserInfo,
} from '@/types'
import { getPretService } from '@/services/pretService'

/**
 * Store principal — Gestion des dossiers de prêts
 */
export const usePretStore = defineStore('pret', () => {
  // ── État ──────────────────────────────
  const activeSidebarItem = ref<string>('consultation')
  const activeTab = ref<string>('donnees-generales')
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)
  const sidebarCollapsed = ref<boolean>(false)
  const darkMode = ref<boolean>(false)
  const currentUser = ref<UserInfo>({
    nom: 'Dupont',
    prenom: 'Jean',
    initiales: 'JD',
  })

  // Sections dépliables
  const sections = reactive<SectionState>({
    general: true,
    pret: true,
    dates: true,
  })

  // Données du dossier courant
  const dossierCourant = ref<DossierPret | null>(null)

  // Données du formulaire (vides par défaut)
  const donneesGenerales = reactive<DonneesGenerales>({
    emprunteur: '',
    coEmprunteur: '',
    noPret: '',
    noContratSouscritProjet: '',
    noContratSouscritPret: '',
    efs: '',
    structure: '',
    codeEtat: '',
    codeObjet: '',
    codeNature: '',
  })

  const donneesPret = reactive<DonneesPret>({
    montantPret: '',
    dureePret: '',
    tauxRemboursement: '',
    tauxFranchise: '',
    tauxBonification: '',
    anticipation: '',
    typeAmortissement: '',
    outilInstruction: '',
    montantDebloque: '',
    montantDisponible: '',
    montantRA: '',
    encours: '',
    teg: '',
  })

  const datesPret = reactive<DatesPret>({
    dateAcceptation: '',
    dateAccord: '',
    dateOuvertureCredit: '',
    datePassageGestion: '',
    dateEffet: '',
    date1ereEcheance: '',
    dateEffetRA: '',
    dateEffetFP: '',
    dateFinPret: '',
    date1ereEcheance2: '',
    datePrecedenteEcheance: '',
    dateProchaineEcheance: '',
    dateAbonnementPrecedent: '',
    dateAbonnementSuivant: '',
    dateTombeePrecedente: '',
    dateTombeeSuivante: '',
  })

  // ── Actions ───────────────────────────

  function toggleSection(section: keyof SectionState) {
    sections[section] = !sections[section]
  }

  function setActiveTab(tabId: string) {
    activeTab.value = tabId
  }

  function setActiveSidebarItem(itemId: string) {
    activeSidebarItem.value = itemId
  }

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function toggleDarkMode() {
    darkMode.value = !darkMode.value
  }

  function expandAllSections() {
    sections.general = true
    sections.pret = true
    sections.dates = true
  }

  function collapseAllSections() {
    sections.general = false
    sections.pret = false
    sections.dates = false
  }

  function appliquerDossier(dossier: DossierPret) {
    dossierCourant.value = dossier
    Object.assign(donneesGenerales, dossier.donneesGenerales)
    Object.assign(donneesPret, dossier.donneesPret)
    Object.assign(datesPret, dossier.dates)
  }

  async function chargerDossier(id: string) {
    loading.value = true
    error.value = null
    try {
      const service = await getPretService()
      const response = await service.getDossier(id)
      if (response.success) {
        appliquerDossier(response.data)
      } else {
        error.value = response.message || 'Erreur lors du chargement'
      }
    } catch (e) {
      error.value = 'Erreur lors du chargement du dossier'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  function resetFormulaire() {
    Object.keys(donneesGenerales).forEach((key) => {
      (donneesGenerales as any)[key] = ''
    })
    Object.keys(donneesPret).forEach((key) => {
      (donneesPret as any)[key] = ''
    })
    Object.keys(datesPret).forEach((key) => {
      (datesPret as any)[key] = ''
    })
  }

  return {
    // State
    activeSidebarItem,
    activeTab,
    loading,
    error,
    sections,
    dossierCourant,
    donneesGenerales,
    donneesPret,
    datesPret,
    sidebarCollapsed,
    darkMode,
    currentUser,
    // Actions
    toggleSection,
    setActiveTab,
    setActiveSidebarItem,
    toggleSidebar,
    toggleDarkMode,
    expandAllSections,
    collapseAllSections,
    appliquerDossier,
    chargerDossier,
    resetFormulaire,
  }
})
