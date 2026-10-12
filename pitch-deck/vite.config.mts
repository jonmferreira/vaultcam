import { defineConfig } from 'vite'

export default defineConfig({
  build: {
    // lightningcss chokes on the default theme's katex CSS; disable CSS minify
    cssMinify: false,
  },
})
