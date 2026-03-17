import type { PretService, DossierPret, DossierResume, ServiceResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

/**
 * Réponse plate du LoansController (DossierConsultationDto côté Java).
 * Les champs correspondent au JSON retourné par GET /api/v1/loans/{numeroPret}.
 */
interface DossierConsultationDto {
  noEmprunteur: string | null
  noCoEmprunteur: string | null
  emprunteur: string | null
  coEmprunteur: string | null
  numeroPret: string | null
  numeroContratSouscritProjet: string | null
  numeroContratSouscritPret: string | null
  efs: string | null
  structure: string | null
  codeEtat: string | null
  libelleEtat: string | null
  codeObjet: string | null
  libelleObjet: string | null
  codeNature: string | null
  libelleNature: string | null
  montantPret: number | null
  dureePret: number | null
  tauxRemboursement: number | null
  tauxFranchise: number | null
  tauxBonification: number | null
  anticipation: boolean | null
  typeAmortissement: string | null
  outilInstruction: string | null
  montantDebloque: number | null
  montantDisponible: number | null
  montantRA: number | null
  encours: number | null
  teg: number | null
}

// ── Fonctions de formatage ──────────────────────────────────────

function formatMontant(value: number | null): string {
  if (value == null) return ''
  return (
    value.toLocaleString('fr-FR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + ' €'
  )
}

function formatTaux(value: number | null): string {
  if (value == null) return ''
  return (
    value.toLocaleString('fr-FR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + ' %'
  )
}

// ── Mapping DossierConsultationDto → types front ────────────────

/**
 * Convertit le DTO plat du backend en DossierPret imbriqué attendu par le front.
 */
function toDossierPret(dto: DossierConsultationDto): DossierPret {
  const id = dto.numeroContratSouscritPret || ''

  return {
    id,
    donneesGenerales: {
      emprunteur: dto.emprunteur || '',
      coEmprunteur: dto.coEmprunteur || '',
      noPret: dto.numeroContratSouscritPret || '',
      noContratSouscritProjet: dto.numeroContratSouscritProjet || '',
      noContratSouscritPret: dto.numeroContratSouscritPret || '',
      efs: dto.efs || '',
      structure: dto.structure || '',
      codeEtat:
        dto.codeEtat && dto.libelleEtat
          ? `${dto.codeEtat} - ${dto.libelleEtat}`
          : dto.codeEtat || '',
      codeObjet:
        dto.codeObjet && dto.libelleObjet
          ? `${dto.codeObjet} - ${dto.libelleObjet}`
          : dto.codeObjet || '',
      codeNature:
        dto.codeNature && dto.libelleNature
          ? `${dto.codeNature} - ${dto.libelleNature}`
          : dto.codeNature || '',
    },
    donneesPret: {
      montantPret: formatMontant(dto.montantPret),
      dureePret: dto.dureePret != null ? `${dto.dureePret} mois` : '',
      tauxRemboursement: formatTaux(dto.tauxRemboursement),
      tauxFranchise: formatTaux(dto.tauxFranchise),
      tauxBonification: formatTaux(dto.tauxBonification),
      anticipation: dto.anticipation != null ? (dto.anticipation ? 'Oui' : 'Non') : '',
      typeAmortissement: dto.typeAmortissement || '',
      outilInstruction: dto.outilInstruction || '',
      montantDebloque: formatMontant(dto.montantDebloque),
      montantDisponible: formatMontant(dto.montantDisponible),
      montantRA: formatMontant(dto.montantRA),
      encours: formatMontant(dto.encours),
      teg: formatTaux(dto.teg),
    },
    dates: {
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
    },
  }
}

/**
 * Convertit le DTO plat en résumé pour la liste de recherche.
 */
function toDossierResume(dto: DossierConsultationDto): DossierResume {
  return {
    id: dto.numeroContratSouscritPret || '',
    noPret: dto.numeroContratSouscritPret || '',
    emprunteur: dto.emprunteur || '',
    montantPret: formatMontant(dto.montantPret),
    codeEtat:
      dto.codeEtat && dto.libelleEtat
        ? `${dto.codeEtat} - ${dto.libelleEtat}`
        : dto.codeEtat || '',
  }
}

/**
 * Implémentation HTTP du service de prêts.
 * Appelle le LoansController du backend sgesapi :
 *   GET /api/v1/loans/{numeroPret} → DossierConsultationDto (plat)
 * et mappe la réponse vers les types du front (DossierPret imbriqué).
 */
export class HttpPretService implements PretService {
  /**
   * Recherche un prêt par son numéro de contrat souscrit.
   * Appelle GET /api/v1/loans/{id} sur le LoansController.
   */
  async getDossier(id: string): Promise<ServiceResponse<DossierPret>> {
    try {
      const response = await fetch(`${BASE_URL}/loans/${id}`)

      if (response.status === 404) {
        return {
          data: null as unknown as DossierPret,
          success: false,
          message: `Prêt "${id}" introuvable`,
        }
      }

      if (!response.ok) {
        return {
          data: null as unknown as DossierPret,
          success: false,
          message: `Erreur HTTP ${response.status} : ${response.statusText}`,
        }
      }

      const dto: DossierConsultationDto = await response.json()
      return {
        data: toDossierPret(dto),
        success: true,
      }
    } catch (error) {
      return {
        data: null as unknown as DossierPret,
        success: false,
        message: `Erreur réseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }

  /**
   * Le LoansController n'expose pas d'endpoint de liste.
   * Retourne un tableau vide — la recherche se fait via rechercherPret().
   */
  async listerDossiers(): Promise<ServiceResponse<DossierResume[]>> {
    return {
      data: [],
      success: true,
      message: 'Utilisez la recherche par N° de prêt',
    }
  }

  /**
   * Recherche un prêt par son numéro et retourne un résumé.
   * Appelle GET /api/v1/loans/{numeroPret} et mappe en DossierResume.
   */
  async rechercherPret(numeroPret: string): Promise<ServiceResponse<DossierResume[]>> {
    try {
      const response = await fetch(`${BASE_URL}/loans/${numeroPret}`)

      if (response.status === 404) {
        return {
          data: [],
          success: true,
          message: `Aucun prêt trouvé pour "${numeroPret}"`,
        }
      }

      if (!response.ok) {
        return {
          data: [],
          success: false,
          message: `Erreur HTTP ${response.status} : ${response.statusText}`,
        }
      }

      const dto: DossierConsultationDto = await response.json()
      return {
        data: [toDossierResume(dto)],
        success: true,
      }
    } catch (error) {
      return {
        data: [],
        success: false,
        message: `Erreur réseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }
}
