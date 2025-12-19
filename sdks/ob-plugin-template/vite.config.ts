import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig(({ mode }) => ({
  plugins: [react()],
  resolve: {
    alias: { '@': path.resolve(__dirname, './src') },
    dedupe: ['react', 'react-dom']
  },
  define: {
    'process.env.NODE_ENV': '"production"',
    'process': '{ env: { NODE_ENV: "production" } }'
  },
  publicDir: path.resolve(__dirname, 'public'),
  server: {
    port: 3001,
    cors: true,
  },
  build: {
    target: 'esnext',
    outDir: 'dist',
    lib: {
      entry: './src/index.tsx',
      name: 'ob-plugin-template',
      formats: ['umd'],
      fileName: () => 'ob-plugin-template.umd.js',
    },
    rollupOptions: {
      external: ['react', 'react-dom', '@arco-design/web-react', 'react-router-dom', 'react-jsx-runtime'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
          '@arco-design/web-react': 'Arco',
          'react-router-dom': 'ReactRouterDOM',
          'react-jsx-runtime': 'ReactJSXRuntime',
        },
        assetFileNames: 'ob-plugin-template.css',
      }
    },
  }
}))
