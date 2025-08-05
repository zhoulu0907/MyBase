import react from '@vitejs/plugin-react';
import path from 'path';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        port: 4399,
    },
    base: './',
    plugins: [react()],
    resolve: {
        alias: {
          '@': path.resolve(__dirname, './src'),
          '@assets': path.join(__dirname, './src/assets'),
        },
    },
    assetsInclude: ['**/*.svg'],
})