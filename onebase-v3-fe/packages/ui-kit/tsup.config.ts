import { defineConfig } from 'tsup';

export default defineConfig({
  entry: ['src/index.ts'],
  outDir: 'dist',
  format: ['esm'],
  dts: false,
  splitting: true,
  sourcemap: true,
  clean: true,
  platform: 'browser',
  loader: {
    '.css': 'copy',
    '.svg': 'dataurl'
  }
});
