// ═══════════════════════════════════════
// Types du domaine métier - Gestion Prêts
// ═══════════════════════════════════════

/** Données Générales d'un prêt */
export interface DonneesGenerales {
  emprunteur: string
  coEmprunteur: string
  noPret: string
  noContratSouscritProjet: string
  noContratSouscritPret: string
  efs: string
  structure: string
  codeEtat: string
  codeObjet: string
  codeNature: string
}

/** Données financières du prêt */
export interface DonneesPret {
  montantPret: string
  dureePret: string
  tauxRemboursement: string
  tauxFranchise: string
  tauxBonification: string
  anticipation: string
  typeAmortissement: string
  outilInstruction: string
  montantDebloque: string
  montantDisponible: string
  montantRA: string
  encours: string
  teg: string
}

/** Dates associées au prêt */
export interface DatesPret {
  dateAcceptation: string
  dateAccord: string
  dateOuvertureCredit: string
  datePassageGestion: string
  dateEffet: string
  date1ereEcheance: string
  dateEffetRA: string
  dateEffetFP: string
  dateFinPret: string
  date1ereEcheance2: string
  datePrecedenteEcheance: string
  dateProchaineEcheance: string
  dateAbonnementPrecedent: string
  dateAbonnementSuivant: string
  dateTombeePrecedente: string
  dateTombeeSuivante: string
}

/** Dossier complet d'un prêt */
export interface DossierPret {
  id: string
  donneesGenerales: DonneesGenerales
  donneesPret: DonneesPret
  dates: DatesPret
}

/** Informations utilisateur connecté */
export interface UserInfo {
  nom: string
  prenom: string
  initiales: string
}

/** Item de navigation sidebar */
export interface SidebarItem {
  id: string
  label: string
  route?: string
  icon?: string
}

/** Onglet de navigation */
export interface TabItem {
  id: string
  label: string
  route: string
}

/** État d'une section dépliable */
export interface SectionState {
  general: boolean
  pret: boolean
  dates: boolean
}
