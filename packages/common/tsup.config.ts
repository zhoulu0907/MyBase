import * as fs from "fs";
import * as less from "less";
import { defineConfig } from "tsup";

export default defineConfig({
  entry: ["src/index.ts"],
  outDir: "dist",
  format: "esm",
  dts: true,
  splitting: true,
  sourcemap: true,
  clean: true,
  esbuildOptions(options) {
    options.loader = {
      ...options.loader,
      ".less": "css",
    };
    options.plugins = [
      ...(options.plugins || []),
      {
        name: "less-loader",
        setup(build) {
          build.onLoad({ filter: /\.less$/ }, async (args) => {
            const source = fs.readFileSync(args.path, "utf8");
            const result = await less.render(source, {
              filename: args.path,
              javascriptEnabled: true,
            });

            return {
              contents: result.css,
              loader: "css",
            };
          });
        },
      },
    ];
  },
});
