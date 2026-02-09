import type { PretService } from '@/types'

let _promise: Promise<PretService> | null = null

/**
 * Factory singleton pour le service de prêts.
 * Retourne toujours la même instance (cache via promise).
 * Futur : basculer sur HttpPretService via import.meta.env.VITE_API_MODE
 */
export function getPretService(): Promise<PretService> {
  if (!_promise) {
    // Toujours le mock pour l'instant
    // Futur : if (import.meta.env.VITE_API_MODE === 'http') { ... }
    _promise = import('./pretService.mock').then((m) => new m.MockPretService())
  }
  return _promise
}
