import type { PretService, DossierPret, DossierResume as FrontDossierResume, ServiceResponse } from '@/types'
import {
  loansApi,
  type DossierConsultationDto,
} from '@/api'

// ── Fonctions de formatage ──────────────────────────────────────

function formatMontant(value?: number | null): string {
  if (value == null) return ''
  return (
    value.toLocaleString('fr-FR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + ' €'
  )
}

function formatTaux(value?: number | null): string {
  if (value == null) return ''
  return (
    value.toLocaleString('fr-FR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + ' %'
  )
}

function codeLabel(code?: string, label?: string): string {
  if (code && label) return `${code} - ${label}`
  return code || ''
}

// ── Mapping API → types front ───────────────────────────────────

/**
 * Convertit un DossierConsultationDto (GET /api/v1/loans/{id})
 * en DossierPret imbriqué attendu par le front.
 */
function dtoToDossierPret(dto: DossierConsultationDto | null | undefined): DossierPret {
  if (!dto) throw new Error('Réponse vide du serveur')
  return {
    id: dto.numeroContratSouscritPret || dto.numeroPret || '',
    donneesGenerales: {
      emprunteur: dto.emprunteur || '',
      coEmprunteur: dto.coEmprunteur || '',
      noPret: dto.numeroContratSouscritPret || dto.numeroPret || '',
      noContratSouscritProjet: dto.numeroContratSouscritProjet || '',
      noContratSouscritPret: dto.numeroContratSouscritPret || '',
      efs: dto.efs || '',
      structure: dto.structure || '',
      codeEtat: codeLabel(dto.codeEtat, dto.libelleEtat),
      codeObjet: codeLabel(dto.codeObjet, dto.libelleObjet),
      codeNature: codeLabel(dto.codeNature, dto.libelleNature),
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
 * Convertit un DossierConsultationDto en résumé pour la liste de recherche.
 */
function dtoToResume(dto: DossierConsultationDto | null | undefined): FrontDossierResume {
  if (!dto) throw new Error('Réponse vide du serveur')
  return {
    id: dto.numeroContratSouscritPret || dto.numeroPret || '',
    noPret: dto.numeroContratSouscritPret || dto.numeroPret || '',
    emprunteur: dto.emprunteur || '',
    montantPret: formatMontant(dto.montantPret),
    codeEtat: codeLabel(dto.codeEtat, dto.libelleEtat),
  }
}

/**
 * Implémentation HTTP du service de prêts.
 * Utilise le client TypeScript généré par swagger-typescript-api.
 *
 * Tous les appels passent par le LoansController :
 *   GET /api/v1/loans/{numeroPret} → DossierConsultationDto
 */
/**
 * Extrait le code HTTP d'une erreur levée par le HttpClient généré.
 * Le HttpClient fait `throw data` (HttpResponse) pour les réponses non-OK.
 */
function getHttpStatus(error: unknown): number | null {
  if (error && typeof error === 'object' && 'status' in error) {
    return (error as { status: number }).status
  }
  return null
}

export class HttpPretService implements PretService {
  /**
   * Consulte un dossier complet.
   * GET /api/v1/loans/{numeroPret} → DossierConsultationDto → DossierPret
   */
  async getDossier(id: string): Promise<ServiceResponse<DossierPret>> {
    try {
      const response = await loansApi.searchLoans(id)
      return {
        data: dtoToDossierPret(response.data),
        success: true,
      }
    } catch (error) {
      const status = getHttpStatus(error)
      if (status === 404) {
        return {
          data: null as unknown as DossierPret,
          success: false,
          message: `Prêt "${id}" introuvable`,
        }
      }
      return {
        data: null as unknown as DossierPret,
        success: false,
        message: status
          ? `Erreur HTTP ${status}`
          : `Erreur réseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }

  /**
   * Pas d'endpoint de liste — retourne un tableau vide.
   */
  async listerDossiers(): Promise<ServiceResponse<FrontDossierResume[]>> {
    return {
      data: [],
      success: true,
    }
  }

  /**
   * Recherche un prêt par N° via le LoansController.
   * GET /api/v1/loans/{numeroPret} → DossierConsultationDto → DossierResume[]
   */
  async rechercherPret(numeroPret: string): Promise<ServiceResponse<FrontDossierResume[]>> {
    try {
      const response = await loansApi.searchLoans(numeroPret)
      return {
        data: [dtoToResume(response.data)],
        success: true,
      }
    } catch (error) {
      const status = getHttpStatus(error)
      if (status === 404) {
        return {
          data: [],
          success: true,
          message: `Aucun prêt trouvé pour "${numeroPret}"`,
        }
      }
      return {
        data: [],
        success: false,
        message: status
          ? `Erreur HTTP ${status}`
          : `Erreur réseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }
}
