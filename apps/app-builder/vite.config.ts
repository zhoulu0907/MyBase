import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 4399
  },
  base: './',
  plugins: [
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
      '@assets': path.join(__dirname, './src/assets'),
      '@workflow/nodes': path.join(__dirname, './src/assets/flow/nodes'),
      '@workflow/store': path.join(__dirname, './src/store/singals'),
      '@workflow/images': path.join(__dirname, './src/assets/images')
    }
  },
  assetsInclude: ['**/*.svg'],
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true,
        modifyVars: {
          prefix: 'pc'
        }
      }
    }
  }
});
