import { describe, it, expect, beforeEach } from 'vitest'
import { MockPretService } from '@/services/pretService.mock'

describe('MockPretService', () => {
  let service: MockPretService

  beforeEach(() => {
    service = new MockPretService()
  })

  // ── getDossier ──
  describe('getDossier', () => {
    it('devrait retourner success: true pour un ID valide', async () => {
      const response = await service.getDossier('DOSS-2024-001')

      expect(response.success).toBe(true)
      expect(response.data).toBeDefined()
      expect(response.data.id).toBe('DOSS-2024-001')
      expect(response.data.donneesGenerales.emprunteur).toBe('MARTIN Jean-Pierre')
    })

    it('devrait retourner success: false pour un ID inconnu', async () => {
      const response = await service.getDossier('INEXISTANT')

      expect(response.success).toBe(false)
      expect(response.message).toContain('introuvable')
    })

    it('devrait retourner des copies indépendantes (pas de mutation partagée)', async () => {
      const response1 = await service.getDossier('DOSS-2024-001')
      const response2 = await service.getDossier('DOSS-2024-001')

      // Modifier la première copie
      response1.data.donneesGenerales.emprunteur = 'MODIFIÉ'

      // La seconde copie ne doit pas être affectée
      expect(response2.data.donneesGenerales.emprunteur).toBe('MARTIN Jean-Pierre')
    })

    it('devrait retourner le dossier PTZ de DUBOIS', async () => {
      const response = await service.getDossier('DOSS-2023-047')

      expect(response.success).toBe(true)
      expect(response.data.donneesGenerales.emprunteur).toBe('DUBOIS Sophie')
      expect(response.data.donneesGenerales.coEmprunteur).toBe('')
      expect(response.data.donneesPret.tauxRemboursement).toBe('0,00 %')
    })

    it('devrait retourner le dossier PAS avec RA de LEFEBVRE', async () => {
      const response = await service.getDossier('DOSS-2022-189')

      expect(response.success).toBe(true)
      expect(response.data.donneesGenerales.emprunteur).toBe('LEFEBVRE Antoine')
      expect(response.data.donneesPret.montantRA).toBe('25 000,00 €')
      expect(response.data.donneesPret.anticipation).toBe('Oui')
    })
  })

  // ── listerDossiers ──
  describe('listerDossiers', () => {
    it('devrait retourner 3 résumés', async () => {
      const response = await service.listerDossiers()

      expect(response.success).toBe(true)
      expect(response.data).toHaveLength(3)
    })

    it('devrait contenir les bons champs dans chaque résumé', async () => {
      const response = await service.listerDossiers()
      const premier = response.data[0]

      expect(premier).toHaveProperty('id')
      expect(premier).toHaveProperty('noPret')
      expect(premier).toHaveProperty('emprunteur')
      expect(premier).toHaveProperty('montantPret')
      expect(premier).toHaveProperty('codeEtat')
    })

    it('devrait avoir MARTIN comme premier dossier', async () => {
      const response = await service.listerDossiers()
      const premier = response.data[0]

      expect(premier.id).toBe('DOSS-2024-001')
      expect(premier.emprunteur).toBe('MARTIN Jean-Pierre')
      expect(premier.noPret).toBe('2024-PAP-001547')
      expect(premier.montantPret).toBe('250 000,00 €')
    })
  })
})
