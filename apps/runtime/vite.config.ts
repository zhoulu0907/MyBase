import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';
import viteCompression from 'vite-plugin-compression';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 9527
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
    }),
    viteCompression({
      verbose: true, // 输出压缩日志
      disable: false, // 确保压缩功能启用
      threshold: 10240, // 仅压缩大于 10KB 的文件
      algorithm: 'gzip', // 使用 gzip 压缩算法
      deleteOriginFile: false, // 压缩后删除源文件
      ext: '.gz' // 压缩后文件的扩展名
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@assets': path.join(__dirname, './src/assets'),
      '@workflow/nodes': path.join(__dirname, '../app-builder/src/assets/flow/nodes'),
      '@workflow/store': path.join(__dirname, '../app-builder/src/store/singals'),
      '@workflow/images': path.join(__dirname, '../app-builder/src/assets/images')
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
