import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';
import viteCompression from 'vite-plugin-compression';

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
      '@assets': path.join(__dirname, './src/assets')
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        // 这里配置 less 选项（注意 Vite 中直接写选项，无需嵌套 lessOptions）
        javascriptEnabled: true,
        modifyVars: {
          '@primary-color': '#009E9E'
        }
      }
    }
  },
  assetsInclude: ['**/*.svg']
});
