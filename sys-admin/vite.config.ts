import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import javascriptObfuscator from "vite-plugin-javascript-obfuscator";

// https://vite.dev/config/
export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: 8999,
  },
  plugins: [
    vue(),
    // vueDevTools(),
    javascriptObfuscator({
      options: {
        compact: true,
        controlFlowFlattening: true,
        numbersToExpressions: true,
        simplify: false,
        stringArrayShuffle: true,
        stringArrayThreshold: 0.75,
      },
      include: ["src/**/*.js", "src/**/*.ts","src/decrypt/decryptUtils.ts"],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  build: {
    minify: false, // 关闭默认 Terser 压缩，避免冲突
  },
})
