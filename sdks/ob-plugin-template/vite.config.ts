import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import fs from 'fs'
import manifestPlugin from './vite-plugin-manifest'

// Read constants.ts to get PLUGIN_NAME
const root = process.cwd();
const constantsPath = path.resolve(root, 'src/constants.ts')
let pluginId = 'onebase-plugin-ocr';

try {
  if (fs.existsSync(constantsPath)) {
    const constantsContent = fs.readFileSync(constantsPath, 'utf-8')
    const match = constantsContent.match(/export const PLUGIN_NAME = ['"]([^'"]+)['"]/)
    if (match && match[1]) {
      pluginId = match[1];
    }
  }
} catch (e) {
  console.error('Failed to read constants.ts', e);
}

export default defineConfig((mode) => ({
  plugins: [react(), manifestPlugin()],
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
    emptyOutDir: true, // 清空输出目录，避免残留旧文件
    lib: {
      entry: './src/index.tsx',
      name: pluginId,
      formats: ['umd'],
      fileName: () => `${pluginId}.umd.js`,
    },
    rollupOptions: {
      external: ['react', 'react-dom', '@arco-design/web-react', 'react-router-dom'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
          '@arco-design/web-react': 'Arco',
          'react-router-dom': 'ReactRouterDOM',
        },
        assetFileNames: (assetInfo) => {
           if (assetInfo.name === 'style.css') return `${pluginId}.css`;
           return `${pluginId}.[ext]`;
        },
      }
    },
  }
}))
