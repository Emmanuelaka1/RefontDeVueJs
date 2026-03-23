import { describe, it, expect, vi, beforeEach } from 'vitest'

// Reset le module entre chaque test pour tester les deux branches
beforeEach(() => {
  vi.resetModules()
})

describe('pretService factory', () => {
  it('devrait retourner MockPretService quand VITE_API_MODE != http', async () => {
    vi.stubEnv('VITE_API_MODE', 'mock')
    const { getPretService } = await import('@/services/pretService')

    const service = await getPretService()

    expect(service).toBeDefined()
    expect(service.getDossier).toBeDefined()
    expect(service.listerDossiers).toBeDefined()
  })

  it('devrait retourner HttpPretService quand VITE_API_MODE = http', async () => {
    vi.stubEnv('VITE_API_MODE', 'http')
    const { getPretService } = await import('@/services/pretService')

    const service = await getPretService()

    expect(service).toBeDefined()
    expect(service.getDossier).toBeDefined()
    expect(service.rechercherPret).toBeDefined()
  })

  it('devrait retourner la même instance (singleton)', async () => {
    vi.stubEnv('VITE_API_MODE', 'mock')
    const { getPretService } = await import('@/services/pretService')

    const service1 = await getPretService()
    const service2 = await getPretService()

    expect(service1).toBe(service2)
  })
})
