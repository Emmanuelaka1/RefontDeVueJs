/* eslint-env node */

// https://eslint.vuejs.org/user-guide/
module.exports = {
  // Empeche ESLint de chercher des configs dans les dossiers parents
  root: true,

  extends: [
    'plugin:vue/vue3-essential', // Regles essentielles Vue 3 (erreurs de syntaxe, v-if/v-for, etc.)
    'eslint:recommended',        // Regles JS recommandees (no-undef, no-unused-vars, etc.)
    '@vue/eslint-config-typescript', // Support TypeScript dans les fichiers .vue
  ],

  parserOptions: {
    ecmaVersion: 'latest', // Permet la syntaxe JS la plus recente (optional chaining, etc.)
  },

  rules: {
    // Autorise les noms de composants en un seul mot (ex: Button, Panel)
    'vue/multi-word-component-names': 'off',

    // Autorise les noms reserves HTML comme noms de composants (ex: PrimeVue Button)
    'vue/no-reserved-component-names': 'off',

    // Signale les points-virgules superflus comme warning (pas bloquant)
    'no-extra-semi': 'warn',
  },
}
