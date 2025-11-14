import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 9528
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
      '@assets': path.join(__dirname, './src/assets')
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        // 这里配置 less 选项（注意 Vite 中直接写选项，无需嵌套 lessOptions）
        javascriptEnabled: true,
        modifyVars: {
          '@primary-color': '#009E9E',
          '@base-font-size': '100px',
        },
      },
    },
  },
  assetsInclude: ['**/*.svg']
});
