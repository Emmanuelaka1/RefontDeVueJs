/**
 * Point d'entrée API — clients générés par swagger-typescript-api
 * depuis le spec OpenAPI sgesapi (http://localhost:9088/v3/api-docs/default).
 *
 * Régénérer : npm run api:local (serveur) ou npm run api:local:file (fichier)
 */
import { HttpClient } from './generated/http-client'
import { LoansController } from './generated/LoansController'
import { Authentification } from './generated/Authentification'

// Les paths générés sont déjà complets (/api/v1/loans/{id}, etc.)
// En dev : baseUrl vide → les appels passent par le proxy Vite (/api → localhost:9088)
const BASE_URL = ''

const httpClient = new HttpClient({
  baseUrl: BASE_URL,
})

/** GET /api/v1/loans/{numeroPret} — LoansController (SIGAC) */
export const loansApi = new LoansController(httpClient)

/** POST /api/v1/auth/login — Authentification JWT */
export const authApi = new Authentification(httpClient)

/** Réexport des types générés */
export type {
  DossierConsultationDto,
  LoginRequest,
} from './generated/data-contracts'
