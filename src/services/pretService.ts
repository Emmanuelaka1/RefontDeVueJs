import type { PretService } from '@/types'

let _promise: Promise<PretService> | null = null

/**
 * Factory singleton pour le service de prets.
 * Retourne toujours la meme instance (cache via promise).
 * Bascule entre MockPretService et HttpPretService selon VITE_API_MODE.
 */
export function getPretService(): Promise<PretService> {
  if (!_promise) {
    if (import.meta.env.VITE_API_MODE === 'http') {
      _promise = import('./pretService.http').then((m) => new m.HttpPretService())
    } else {
      _promise = import('./pretService.mock').then((m) => new m.MockPretService())
    }
  }
  return _promise
}
