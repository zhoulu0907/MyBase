
import { Plugin } from 'vite';
import fs from 'fs';
import path from 'path';
import { exec } from 'child_process';

export default function manifestPlugin(): Plugin {
  return {
    name: 'vite-plugin-ob-manifest',
    apply: 'build', // Only apply during build
    closeBundle: async () => {
      console.log('[ob-manifest] Generating frontend.manifest.json...');
      
      const root = process.cwd();
      const srcDir = path.resolve(root, 'src');
      const distDir = path.resolve(root, 'dist');
      
      try {
        // 1. Extract Basic Info from constants.ts
        const constantsPath = path.resolve(srcDir, 'constants.ts');
        const constantsContent = fs.readFileSync(constantsPath, 'utf-8');
        
        const extractConst = (key: string) => {
          const match = constantsContent.match(new RegExp(`export const ${key} = ['"]([^'"]+)['"]`));
          return match ? match[1] : '';
        };

        // Read version from package.json as requested
        const pkgPath = path.resolve(root, 'package.json');
        const pkg = JSON.parse(fs.readFileSync(pkgPath, 'utf-8'));
        const pkgVersion = pkg.version;

        const pluginName = extractConst('PLUGIN_NAME');
        const displayName = extractConst('PLUGIN_DISPLAY_NAME');
        const version = pkgVersion || extractConst('PLUGIN_VERSION'); 
        const routePrefix = extractConst('PLUGIN_ROUTE_PREFIX');

        // 2. Extract Pages & Components from register/index.ts
        const registerPath = path.resolve(srcDir, 'register/index.ts');
        const registerContent = fs.readFileSync(registerPath, 'utf-8');

        // Extract Pages
        const pages: any[] = [];
        const pagesMatch = registerContent.match(/plugin\.registerPages\(\s*({[\s\S]+?})\s*\)/);
        if (pagesMatch && pagesMatch[1]) {
          const pagesObjStr = pagesMatch[1];
          // Simple regex to find keys and properties. Assumes structure: key: { path: '...', title: '...' }
          const pageEntryRegex = /(\w+):\s*{\s*path:\s*['"]([^'"]+)['"](?:,\s*title:\s*['"]([^'"]+)['"])?/g;
          let match;
          while ((match = pageEntryRegex.exec(pagesObjStr)) !== null) {
            pages.push({
              key: match[1],
              path: match[2],
              title: match[3] || ''
            });
          }
        }

        // Extract Components
        const components: any[] = [];
        // Match the componentsToRegister object definition
        // Note: This regex assumes the object is defined with `const componentsToRegister: any = { ... }`
        const compsMatch = registerContent.match(/const componentsToRegister[^=]*=\s*({[\s\S]+?})\n/);
        
        if (compsMatch && compsMatch[1]) {
          const compsObjStr = compsMatch[1];
          // Match entries like: PluginOCR: { type: 'PluginOCR', ...PluginOCRSchema, component: PluginOCR }
          // We want to capture the Key, Type, and the Schema variable name (e.g. PluginOCRSchema)
          const compEntryRegex = /(\w+):\s*{\s*type:\s*['"]([^'"]+)['"](?:,\s*\.\.\.(\w+))?/g;
          
          let match;
          while ((match = compEntryRegex.exec(compsObjStr)) !== null) {
            const [_, key, type, schemaVarName] = match;
            
            let schemaData = {};
            
            // If there is a schema variable spread (...PluginOCRSchema), try to find and parse it
            if (schemaVarName) {
                // 1. Find import path for this schema variable in register/index.ts
                // import { PluginOCRSchema } from '../components/PluginOCR/schema'
                const importRegex = new RegExp(`import\\s*{\\s*${schemaVarName}\\s*}\\s*from\\s*['"]([^'"]+)['"]`);
                const importMatch = registerContent.match(importRegex);
                
                if (importMatch && importMatch[1]) {
                    const importPath = importMatch[1];
                    const schemaFilePath = path.resolve(srcDir, 'register', `${importPath}.ts`); // Resolve relative to register dir
                    
                    if (fs.existsSync(schemaFilePath)) {
                        const schemaFileContent = fs.readFileSync(schemaFilePath, 'utf-8');
                        
                        // Extract template
                        const templateMatch = schemaFileContent.match(/template:\s*({[\s\S]+?})\s*,/);
                        if (templateMatch && templateMatch[1]) {
                             try {
                                 // Simple JSON-like parsing for template object (keys might not be quoted)
                                 // We'll use a safer eval or loose JSON parse
                                 // For now, let's just try to extract known fields manually to be safe
                                 const templateStr = templateMatch[1];
                                 const extractField = (f: string) => {
                                     const m = templateStr.match(new RegExp(`${f}:\\s*['"]([^'"]+)['"]`));
                                     return m ? m[1] : undefined;
                                 };
                                 const extractBool = (f: string) => {
                                     const m = templateStr.match(new RegExp(`${f}:\\s*(true|false)`));
                                     return m ? m[1] === 'true' : undefined;
                                 };
                                  const extractNum = (f: string) => {
                                     const m = templateStr.match(new RegExp(`${f}:\\s*(\d+)`));
                                     return m ? parseInt(m[1]) : undefined;
                                 };

                                 schemaData = {
                                     template: {
                                         displayName: extractField('displayName'),
                                         icon: extractField('icon'),
                                         category: extractField('category'),
                                         isPlugin: extractBool('isPlugin'),
                                         h: extractNum('h'),
                                         w: extractNum('w')
                                     }
                                 };
                             } catch (e) {
                                 console.warn(`[ob-manifest] Failed to parse template for ${schemaVarName}`);
                             }
                        }
                    }
                }
            }

            components.push({
              key,
              type,
              ...schemaData
            });
          }
        }

        // 3. Construct Manifest
        const manifest = {
          pluginId: pluginName,
          name: pluginName,
          displayName,
          version,
          routePrefix,
          description: '', // Could extract from somewhere else if needed
          entry: {
            js: `${pluginName}.umd.js`,
            css: `${pluginName}.css`
          },
          assets: {
             // List files in public or dist? For now, leave empty or static
             // If we scanned dist for images, we could add them here
          },
          pages,
          components
        };

        // 4. Write to dist
        if (!fs.existsSync(distDir)) {
          fs.mkdirSync(distDir, { recursive: true });
        }
        fs.writeFileSync(
          path.resolve(distDir, 'frontend.manifest.json'), 
          JSON.stringify(manifest, null, 2)
        );
        
        console.log('[ob-manifest] frontend.manifest.json generated successfully.');

        // 5. Package into zip
        const zipFileName = `frontend-${pluginName}-${version}.zip`;
        const zipFilePath = path.resolve(distDir, zipFileName);
        
        // Zip dist folder contents EXCLUDING the zip file itself if it's being created inside dist
        // But since we are creating it, we should probably create it outside first or use a temp name, 
        // OR simply zip everything else.
        // Actually, 'zip -r . ' will try to include the zip file itself if we output it to current dir.
        // Strategy: Zip to parent (root), then move to dist? Or zip * into zipfile.
        
        console.log(`[ob-manifest] Packaging into ${zipFileName}...`);
        
        // We will zip contents of dist to a zip file inside dist.
        // To avoid circular loop (zipping the zip file), we use -x to exclude it.
        
        const command = `cd "${distDir}" && zip -r "${zipFileName}" . -x "${zipFileName}"`;
        
        exec(command, (error, stdout, stderr) => {
            if (error) {
                console.error(`[ob-manifest] Zip error: ${error.message}`);
                return;
            }
            console.log(`[ob-manifest] Created ${zipFilePath}`);
        });

      } catch (error) {
        console.error('[ob-manifest] Error generating manifest:', error);
      }
    }
  };
}
