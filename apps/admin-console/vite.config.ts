import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';
import viteCompression from 'vite-plugin-compression';

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 4400
  },
  base: './',
  plugins: [
    react(),
    viteCompression({
      verbose: true, // 输出压缩日志
      disable: false, // 确保压缩功能启用
      threshold: 10240, // 仅压缩大于 10KB 的文件
      algorithm: 'gzip', // 使用 gzip 压缩算法
      deleteOriginFile: true, // 压缩后删除源文件
      ext: '.gz' // 压缩后文件的扩展名
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  assetsInclude: ['**/*.svg']
});
