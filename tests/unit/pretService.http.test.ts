import { describe, it, expect, beforeEach, vi } from 'vitest'
import { HttpPretService } from '@/services/pretService.http'

// Mock le module @/api — on intercepte loansApi.searchLoans
const mockSearchLoans = vi.fn()

vi.mock('@/api', () => ({
  loansApi: {
    searchLoans: (...args: any[]) => mockSearchLoans(...args),
  },
}))

/** DTO complet aligné sur le vrai format backend (DossierConsultationDto) */
const MOCK_DTO = {
  noEmprunteur: '14336390',
  noCoEmprunteur: '14336391',
  emprunteur: 'MARTIN Jean-Pierre',
  coEmprunteur: 'MARTIN Catherine',
  numeroPret: null,
  numeroContratSouscritProjet: 'DD04063627',
  numeroContratSouscritPret: 'DD04063627',
  efs: '01',
  structure: null,
  codeEtat: 'AA',
  libelleEtat: 'EN COURS NORMALE',
  codeObjet: 'AA',
  libelleObjet: 'ACQUISITION ANCIEN',
  codeNature: '110309',
  libelleNature: 'ALTIMMO FIXE',
  montantPret: 250000.0,
  dureePret: 240,
  tauxRemboursement: 3.45,
  tauxFranchise: null,
  tauxBonification: null,
  anticipation: null,
  typeAmortissement: null,
  outilInstruction: null,
  montantDebloque: null,
  montantDisponible: 0.0,
  montantRA: null,
  encours: null,
  teg: null,
}

describe('HttpPretService', () => {
  let service: HttpPretService

  beforeEach(() => {
    service = new HttpPretService()
    mockSearchLoans.mockReset()
  })

  // ── getDossier ──────────────────────────────────────────────────
  describe('getDossier', () => {
    it('devrait retourner un DossierPret mappé en cas de succès', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: MOCK_DTO })

      const response = await service.getDossier('DD04063627')

      expect(response.success).toBe(true)
      expect(response.data.id).toBe('DD04063627')
      expect(response.data.donneesGenerales.emprunteur).toBe('MARTIN Jean-Pierre')
      expect(response.data.donneesGenerales.coEmprunteur).toBe('MARTIN Catherine')
      expect(response.data.donneesGenerales.efs).toBe('01')
      expect(response.data.donneesGenerales.codeEtat).toBe('AA - EN COURS NORMALE')
      expect(response.data.donneesGenerales.codeNature).toBe('110309 - ALTIMMO FIXE')
      expect(response.data.donneesGenerales.codeObjet).toBe('AA - ACQUISITION ANCIEN')
      expect(response.data.donneesPret.dureePret).toBe('240 mois')
      expect(mockSearchLoans).toHaveBeenCalledWith('DD04063627')
    })

    it('devrait formater les montants en euros', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: MOCK_DTO })

      const response = await service.getDossier('DD04063627')

      expect(response.data.donneesPret.montantPret).toContain('250')
      expect(response.data.donneesPret.montantPret).toContain('€')
    })

    it('devrait formater les taux en pourcentage', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: MOCK_DTO })

      const response = await service.getDossier('DD04063627')

      expect(response.data.donneesPret.tauxRemboursement).toContain('3,45')
      expect(response.data.donneesPret.tauxRemboursement).toContain('%')
    })

    it('devrait gérer les champs null du DTO', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: MOCK_DTO })

      const response = await service.getDossier('DD04063627')

      expect(response.data.donneesGenerales.structure).toBe('')
      expect(response.data.donneesPret.tauxFranchise).toBe('')
      expect(response.data.donneesPret.typeAmortissement).toBe('')
    })

    it('devrait retourner success: false en cas de 404 (HttpClient throw)', async () => {
      mockSearchLoans.mockRejectedValue({ ok: false, status: 404, error: null })

      const response = await service.getDossier('INEXISTANT')

      expect(response.success).toBe(false)
      expect(response.message).toContain('introuvable')
    })

    it('devrait retourner success: false en cas d\'erreur HTTP 500', async () => {
      mockSearchLoans.mockRejectedValue({ ok: false, status: 500, error: null })

      const response = await service.getDossier('DD04063627')

      expect(response.success).toBe(false)
      expect(response.message).toContain('500')
    })

    it('devrait retourner success: false en cas d\'erreur réseau', async () => {
      mockSearchLoans.mockRejectedValue(new Error('Network error'))

      const response = await service.getDossier('DD04063627')

      expect(response.success).toBe(false)
      expect(response.message).toContain('Network error')
    })

    it('devrait gérer un dto null (réponse vide du serveur)', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: null })

      const response = await service.getDossier('DD04063627')

      // dtoToDossierPret throws → catch → erreur
      expect(response.success).toBe(false)
      expect(response.message).toContain('Réponse vide')
    })
  })

  // ── listerDossiers ─────────────────────────────────────────────
  describe('listerDossiers', () => {
    it('devrait retourner un tableau vide (pas d\'endpoint de liste)', async () => {
      const response = await service.listerDossiers()

      expect(response.success).toBe(true)
      expect(response.data).toEqual([])
    })
  })

  // ── rechercherPret ─────────────────────────────────────────────
  describe('rechercherPret', () => {
    it('devrait retourner un résumé en cas de succès', async () => {
      mockSearchLoans.mockResolvedValue({ ok: true, status: 200, data: MOCK_DTO })

      const response = await service.rechercherPret('DD04063627')

      expect(response.success).toBe(true)
      expect(response.data).toHaveLength(1)
      expect(response.data[0].id).toBe('DD04063627')
      expect(response.data[0].noPret).toBe('DD04063627')
      expect(response.data[0].emprunteur).toBe('MARTIN Jean-Pierre')
      expect(response.data[0].codeEtat).toBe('AA - EN COURS NORMALE')
      expect(response.data[0].montantPret).toContain('€')
    })

    it('devrait retourner un tableau vide en cas de 404', async () => {
      mockSearchLoans.mockRejectedValue({ ok: false, status: 404, error: null })

      const response = await service.rechercherPret('INEXISTANT')

      expect(response.success).toBe(true)
      expect(response.data).toEqual([])
      expect(response.message).toContain('Aucun prêt trouvé')
    })

    it('devrait retourner success: false en cas d\'erreur réseau', async () => {
      mockSearchLoans.mockRejectedValue(new Error('fetch failed'))

      const response = await service.rechercherPret('DD04063627')

      expect(response.success).toBe(false)
      expect(response.data).toEqual([])
      expect(response.message).toContain('fetch failed')
    })
  })
})
