import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { HttpPretService } from '@/services/pretService.http'

describe('HttpPretService', () => {
  let service: HttpPretService
  const originalFetch = globalThis.fetch

  beforeEach(() => {
    service = new HttpPretService()
  })

  afterEach(() => {
    globalThis.fetch = originalFetch
  })

  // ── getDossier ──
  describe('getDossier', () => {
    it('devrait retourner les donnees en cas de succes', async () => {
      const mockBody = {
        data: {
          id: 'DOSS-2024-001',
          donneesGenerales: { emprunteur: 'MARTIN Jean-Pierre' },
          donneesPret: { montantPret: '250 000,00 €' },
          dates: { dateAcceptation: '15/01/2024' },
        },
        success: true,
      }

      globalThis.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockBody),
      })

      const response = await service.getDossier('DOSS-2024-001')

      expect(response.success).toBe(true)
      expect(response.data.id).toBe('DOSS-2024-001')
      expect(globalThis.fetch).toHaveBeenCalledWith('/api/v1/prets/DOSS-2024-001')
    })

    it('devrait retourner success: false en cas d\'erreur HTTP 404', async () => {
      globalThis.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 404,
        statusText: 'Not Found',
      })

      const response = await service.getDossier('INEXISTANT')

      expect(response.success).toBe(false)
      expect(response.message).toContain('404')
    })

    it('devrait retourner success: false en cas d\'erreur reseau', async () => {
      globalThis.fetch = vi.fn().mockRejectedValue(new Error('Network error'))

      const response = await service.getDossier('DOSS-2024-001')

      expect(response.success).toBe(false)
      expect(response.message).toContain('Network error')
    })
  })

  // ── listerDossiers ──
  describe('listerDossiers', () => {
    it('devrait retourner la liste en cas de succes', async () => {
      const mockBody = {
        data: [
          { id: 'DOSS-2024-001', noPret: '2024-PAP-001547', emprunteur: 'MARTIN', montantPret: '250 000 €', codeEtat: '40' },
        ],
        success: true,
      }

      globalThis.fetch = vi.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockBody),
      })

      const response = await service.listerDossiers()

      expect(response.success).toBe(true)
      expect(response.data).toHaveLength(1)
      expect(globalThis.fetch).toHaveBeenCalledWith('/api/v1/prets')
    })

    it('devrait retourner un tableau vide en cas d\'erreur HTTP', async () => {
      globalThis.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      })

      const response = await service.listerDossiers()

      expect(response.success).toBe(false)
      expect(response.data).toEqual([])
      expect(response.message).toContain('500')
    })

    it('devrait retourner un tableau vide en cas d\'erreur reseau', async () => {
      globalThis.fetch = vi.fn().mockRejectedValue(new Error('fetch failed'))

      const response = await service.listerDossiers()

      expect(response.success).toBe(false)
      expect(response.data).toEqual([])
      expect(response.message).toContain('fetch failed')
    })
  })
})
