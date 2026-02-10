import type { PretService, DossierPret, DossierResume, ServiceResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

/**
 * Implementation HTTP du service de prets.
 * Utilise fetch() natif pour communiquer avec le backend.
 */
export class HttpPretService implements PretService {
  async getDossier(id: string): Promise<ServiceResponse<DossierPret>> {
    try {
      const response = await fetch(`${BASE_URL}/prets/${id}`)

      if (!response.ok) {
        return {
          data: null as unknown as DossierPret,
          success: false,
          message: `Erreur HTTP ${response.status} : ${response.statusText}`,
        }
      }

      const body: ServiceResponse<DossierPret> = await response.json()
      return body
    } catch (error) {
      return {
        data: null as unknown as DossierPret,
        success: false,
        message: `Erreur reseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }

  async listerDossiers(): Promise<ServiceResponse<DossierResume[]>> {
    try {
      const response = await fetch(`${BASE_URL}/prets`)

      if (!response.ok) {
        return {
          data: [],
          success: false,
          message: `Erreur HTTP ${response.status} : ${response.statusText}`,
        }
      }

      const body: ServiceResponse<DossierResume[]> = await response.json()
      return body
    } catch (error) {
      return {
        data: [],
        success: false,
        message: `Erreur reseau : ${error instanceof Error ? error.message : String(error)}`,
      }
    }
  }
}
