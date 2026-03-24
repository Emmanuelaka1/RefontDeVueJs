/**
 * cypress-ci.mjs
 * Lance le serveur Vite, attend qu'il soit prêt sur localhost:3000,
 * exécute Cypress + rapport de couverture NYC, puis arrête le serveur.
 * Aucune dépendance externe requise (zéro install).
 */
import { spawn } from 'node:child_process'
import http from 'node:http'

const BASE_URL = 'http://localhost:3000'
const MAX_WAIT = 60_000 // 60s max pour attendre le serveur
const POLL_INTERVAL = 1_000

// ── Helpers ────────────────────────────────────────────────────
function run(cmd, args) {
  return new Promise((resolve, reject) => {
    const child = spawn(cmd, args, {
      stdio: 'inherit',
      shell: true,
    })
    child.on('close', (code) => {
      if (code === 0) resolve()
      else reject(new Error(`"${cmd} ${args.join(' ')}" exited with code ${code}`))
    })
    child.on('error', reject)
  })
}

function waitForServer(url, timeout) {
  return new Promise((resolve, reject) => {
    const start = Date.now()
    const check = () => {
      http
        .get(url, (res) => {
          res.resume()
          if (res.statusCode >= 200 && res.statusCode < 400) {
            console.log(`✔ Server ready at ${url}`)
            resolve()
          } else {
            retry()
          }
        })
        .on('error', retry)
    }
    const retry = () => {
      if (Date.now() - start > timeout) {
        reject(new Error(`Server not ready at ${url} after ${timeout / 1000}s`))
      } else {
        setTimeout(check, POLL_INTERVAL)
      }
    }
    check()
  })
}

// ── Main ───────────────────────────────────────────────────────
const isWin = process.platform === 'win32'
const npx = isWin ? 'npx.cmd' : 'npx'

// 1. Lancer Vite en arrière-plan
console.log('▶ Starting Vite dev server...')
const vite = spawn(npx, ['vite', '--port', '3000'], {
  stdio: ['ignore', 'pipe', 'pipe'],
  shell: true,
  detached: !isWin,
})

// Afficher la sortie Vite dans la console
vite.stdout.pipe(process.stdout)
vite.stderr.pipe(process.stderr)

let exitCode = 0
try {
  // 2. Attendre que le serveur soit prêt
  await waitForServer(BASE_URL, MAX_WAIT)

  // 3. Exécuter Cypress
  console.log('\n▶ Running Cypress tests...')
  await run(npx, ['cypress', 'run'])

  // 4. Rapport de couverture
  console.log('\n▶ Generating coverage report...')
  await run(npx, ['nyc', 'report', '--reporter=text-summary'])

  // 5. Vérifier les seuils
  console.log('\n▶ Checking coverage thresholds...')
  await run(npx, ['nyc', 'check-coverage'])

  console.log('\n✔ All checks passed!')
} catch (err) {
  console.error(`\n✖ ${err.message}`)
  exitCode = 1
} finally {
  // 6. Arrêter Vite
  console.log('\n▶ Stopping Vite server...')
  if (isWin) {
    spawn('taskkill', ['/pid', String(vite.pid), '/F', '/T'], { stdio: 'ignore', shell: true })
  } else {
    process.kill(-vite.pid, 'SIGTERM')
  }
  process.exit(exitCode)
}
