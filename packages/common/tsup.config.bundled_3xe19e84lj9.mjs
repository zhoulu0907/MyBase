// tsup.config.ts
import * as fs from "fs";
import * as less from "less";
import { defineConfig } from "tsup";
var tsup_config_default = defineConfig({
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
      ".less": "css"
    };
    options.plugins = [
      ...options.plugins || [],
      {
        name: "less-loader",
        setup(build) {
          build.onLoad({ filter: /\.less$/ }, async (args) => {
            const source = fs.readFileSync(args.path, "utf8");
            const result = await less.render(source, {
              filename: args.path,
              javascriptEnabled: true
            });
            return {
              contents: result.css,
              loader: "css"
            };
          });
        }
      }
    ];
  }
});
export {
  tsup_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidHN1cC5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9faW5qZWN0ZWRfZmlsZW5hbWVfXyA9IFwiL1ZvbHVtZXMvc2FuZ2Zvci91ZW1fZGlzay9EYXRhLzEwMDEvVXNlcnMveWFuZ3hpbnl1L0RvY3VtZW50cy9vbmViYXNlLXYzLWZlL3BhY2thZ2VzL2NvbW1vbi90c3VwLmNvbmZpZy50c1wiO2NvbnN0IF9faW5qZWN0ZWRfZGlybmFtZV9fID0gXCIvVm9sdW1lcy9zYW5nZm9yL3VlbV9kaXNrL0RhdGEvMTAwMS9Vc2Vycy95YW5neGlueXUvRG9jdW1lbnRzL29uZWJhc2UtdjMtZmUvcGFja2FnZXMvY29tbW9uXCI7Y29uc3QgX19pbmplY3RlZF9pbXBvcnRfbWV0YV91cmxfXyA9IFwiZmlsZTovLy9Wb2x1bWVzL3Nhbmdmb3IvdWVtX2Rpc2svRGF0YS8xMDAxL1VzZXJzL3lhbmd4aW55dS9Eb2N1bWVudHMvb25lYmFzZS12My1mZS9wYWNrYWdlcy9jb21tb24vdHN1cC5jb25maWcudHNcIjtpbXBvcnQgKiBhcyBmcyBmcm9tICdmcyc7XG5pbXBvcnQgKiBhcyBsZXNzIGZyb20gJ2xlc3MnO1xuaW1wb3J0IHsgZGVmaW5lQ29uZmlnIH0gZnJvbSAndHN1cCc7XG5cbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XG4gIGVudHJ5OiBbJ3NyYy9pbmRleC50cyddLFxuICBvdXREaXI6ICdkaXN0JyxcbiAgZm9ybWF0OiAnZXNtJyxcbiAgZHRzOiB0cnVlLFxuICBzcGxpdHRpbmc6IHRydWUsXG4gIHNvdXJjZW1hcDogdHJ1ZSxcbiAgY2xlYW46IHRydWUsXG4gIGVzYnVpbGRPcHRpb25zKG9wdGlvbnMpIHtcbiAgICBvcHRpb25zLmxvYWRlciA9IHtcbiAgICAgIC4uLm9wdGlvbnMubG9hZGVyLFxuICAgICAgJy5sZXNzJzogJ2NzcydcbiAgICB9O1xuICAgIG9wdGlvbnMucGx1Z2lucyA9IFtcbiAgICAgIC4uLihvcHRpb25zLnBsdWdpbnMgfHwgW10pLFxuICAgICAge1xuICAgICAgICBuYW1lOiAnbGVzcy1sb2FkZXInLFxuICAgICAgICBzZXR1cChidWlsZCkge1xuICAgICAgICAgIGJ1aWxkLm9uTG9hZCh7IGZpbHRlcjogL1xcLmxlc3MkLyB9LCBhc3luYyAoYXJncykgPT4ge1xuICAgICAgICAgICAgY29uc3Qgc291cmNlID0gZnMucmVhZEZpbGVTeW5jKGFyZ3MucGF0aCwgJ3V0ZjgnKTtcbiAgICAgICAgICAgIGNvbnN0IHJlc3VsdCA9IGF3YWl0IGxlc3MucmVuZGVyKHNvdXJjZSwge1xuICAgICAgICAgICAgICBmaWxlbmFtZTogYXJncy5wYXRoLFxuICAgICAgICAgICAgICBqYXZhc2NyaXB0RW5hYmxlZDogdHJ1ZVxuICAgICAgICAgICAgfSk7XG5cbiAgICAgICAgICAgIHJldHVybiB7XG4gICAgICAgICAgICAgIGNvbnRlbnRzOiByZXN1bHQuY3NzLFxuICAgICAgICAgICAgICBsb2FkZXI6ICdjc3MnXG4gICAgICAgICAgICB9O1xuICAgICAgICAgIH0pO1xuICAgICAgICB9XG4gICAgICB9XG4gICAgXTtcbiAgfVxufSk7XG4iXSwKICAibWFwcGluZ3MiOiAiO0FBQStaLFlBQVksUUFBUTtBQUNuYixZQUFZLFVBQVU7QUFDdEIsU0FBUyxvQkFBb0I7QUFFN0IsSUFBTyxzQkFBUSxhQUFhO0FBQUEsRUFDMUIsT0FBTyxDQUFDLGNBQWM7QUFBQSxFQUN0QixRQUFRO0FBQUEsRUFDUixRQUFRO0FBQUEsRUFDUixLQUFLO0FBQUEsRUFDTCxXQUFXO0FBQUEsRUFDWCxXQUFXO0FBQUEsRUFDWCxPQUFPO0FBQUEsRUFDUCxlQUFlLFNBQVM7QUFDdEIsWUFBUSxTQUFTO0FBQUEsTUFDZixHQUFHLFFBQVE7QUFBQSxNQUNYLFNBQVM7QUFBQSxJQUNYO0FBQ0EsWUFBUSxVQUFVO0FBQUEsTUFDaEIsR0FBSSxRQUFRLFdBQVcsQ0FBQztBQUFBLE1BQ3hCO0FBQUEsUUFDRSxNQUFNO0FBQUEsUUFDTixNQUFNLE9BQU87QUFDWCxnQkFBTSxPQUFPLEVBQUUsUUFBUSxVQUFVLEdBQUcsT0FBTyxTQUFTO0FBQ2xELGtCQUFNLFNBQVksZ0JBQWEsS0FBSyxNQUFNLE1BQU07QUFDaEQsa0JBQU0sU0FBUyxNQUFXLFlBQU8sUUFBUTtBQUFBLGNBQ3ZDLFVBQVUsS0FBSztBQUFBLGNBQ2YsbUJBQW1CO0FBQUEsWUFDckIsQ0FBQztBQUVELG1CQUFPO0FBQUEsY0FDTCxVQUFVLE9BQU87QUFBQSxjQUNqQixRQUFRO0FBQUEsWUFDVjtBQUFBLFVBQ0YsQ0FBQztBQUFBLFFBQ0g7QUFBQSxNQUNGO0FBQUEsSUFDRjtBQUFBLEVBQ0Y7QUFDRixDQUFDOyIsCiAgIm5hbWVzIjogW10KfQo=
