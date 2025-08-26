import * as fs from 'fs';
import * as less from 'less';
import { defineConfig } from 'tsup';

export default defineConfig({
  entry: ['src/index.ts'],
  outDir: 'dist',
  format: 'esm',
  dts: true,
  splitting: true,
  sourcemap: true,
  clean: true,
  esbuildOptions(options) {
    options.loader = {
      ...options.loader,
      '.less': 'css'
    };
    options.plugins = [
      ...(options.plugins || []),
      {
        name: 'less-loader',
        setup(build) {
            build.onLoad({ filter: /\.less$/ }, async (args) => {
              console.log('Processing Less file:', args.path); // 添加日志
              const source = fs.readFileSync(args.path, 'utf8');
              try {
                const result = await less.render(source, {
                  filename: args.path,
                  javascriptEnabled: true
                });
                console.log('Less compilation successful:', args.path); // 添加日志
                return {
                  contents: result.css,
                  loader: 'css'
                };
              } catch (error) {
                console.error('Less compilation failed:', args.path, error); // 添加错误日志
                throw error;
              }
            });
          }
      }
    ];
  }
});
