import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';
import qiankun from 'vite-plugin-qiankun';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 4401,
    cors: true,
    headers: {
      'Access-Control-Allow-Origin': '*'
    }
  },
  base: './',
  plugins: [
    qiankun('mobile-editor', {
      useDevMode: true
    }),
    react({
      babel: {
        plugins: [
          ['@babel/plugin-proposal-decorators', { legacy: true }],
          ['@babel/plugin-proposal-class-properties', { loose: true }]
        ]
      }
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@assets': path.join(__dirname, './src/assets')
    }
  },
  assetsInclude: ['**/*.svg']
});
