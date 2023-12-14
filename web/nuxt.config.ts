// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({

  // Modules
  modules: ['@nuxtjs/tailwindcss', '@nuxtjs/google-fonts', 'nuxt-icon'],

  // Fonts
  googleFonts: {
    download: true,
    inject: true,
    families: {
      'Nunito': [400, 700],
      'JetBrains Mono': [400, 700]
    }
  },

  devtools: { enabled: true }
})
