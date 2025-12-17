import type { PluginMeta, PluginPage, PluginComponent } from '../../sdk/types';

/** 校验插件导出结构的基本形态 */
export function validatePlugin(plugin: any, meta: PluginMeta): void {
  if (!plugin || typeof plugin !== 'object') {
    throw new Error(`Plugin ${meta.name} export must be an object`);
  }
  if (!plugin.pages || typeof plugin.pages !== 'object') {
    throw new Error(`Plugin ${meta.name} pages must be an object`);
  }
  if (!plugin.components || typeof plugin.components !== 'object') {
    throw new Error(`Plugin ${meta.name} components must be an object`);
  }
  if (!plugin.methods || typeof plugin.methods !== 'object') {
    throw new Error(`Plugin ${meta.name} methods must be an object`);
  }
  if (!meta.routePrefix || typeof meta.routePrefix !== 'string') {
    throw new Error(`Plugin ${meta.name} meta.routePrefix must be a non-empty string`);
  }
  Object.values(plugin.pages as Record<string, PluginPage>).forEach((p) => {
    if (!p || typeof p.path !== 'string' || typeof p.component !== 'function') {
      throw new Error(`Plugin ${meta.name} page invalid: require path and component()`);
    }
  });
  Object.entries(plugin.components as Record<string, PluginComponent>).forEach(([key, c]) => {
    if (!c) {
      throw new Error(`Plugin ${meta.name} component[${key}] invalid: empty item`);
    }
    if (c.component && typeof c.component !== 'function') {
      throw new Error(`Plugin ${meta.name} component[${key}] invalid: component should be function`);
    }
  });
}
