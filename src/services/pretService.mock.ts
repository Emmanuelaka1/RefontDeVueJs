import type { PretService, DossierPret, DossierResume, ServiceResponse } from '@/types'
import { mockDossiers } from '@/data/mockDossiers'

const DELAY_MS = 400

function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

/**
 * Implémentation mock du service de prêts.
 * Simule un délai réseau de 400ms et retourne des copies indépendantes.
 */
export class MockPretService implements PretService {
  async getDossier(id: string): Promise<ServiceResponse<DossierPret>> {
    await delay(DELAY_MS)

    const dossier = mockDossiers.find((d) => d.id === id)

    if (!dossier) {
      return {
        data: null as unknown as DossierPret,
        success: false,
        message: `Dossier "${id}" introuvable`,
      }
    }

    return {
      data: structuredClone(dossier),
      success: true,
    }
  }

  async listerDossiers(): Promise<ServiceResponse<DossierResume[]>> {
    await delay(DELAY_MS)

    const resumes: DossierResume[] = mockDossiers.map((d) => ({
      id: d.id,
      noPret: d.donneesGenerales.noPret,
      emprunteur: d.donneesGenerales.emprunteur,
      montantPret: d.donneesPret.montantPret,
      codeEtat: d.donneesGenerales.codeEtat,
    }))

    return {
      data: resumes,
      success: true,
    }
  }
}
