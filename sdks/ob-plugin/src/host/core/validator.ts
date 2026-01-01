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
    const hasType = typeof (c as any).type === 'string' && (c as any).type.length > 0;
    if (hasType) {
      if ((c as any).schema === undefined) {
        throw new Error(`Plugin ${meta.name} component[${key}] invalid: schema required when type provided`);
      }
      if ((c as any).template === undefined) {
        throw new Error(`Plugin ${meta.name} component[${key}] invalid: template required when type provided`);
      }
    }
    const comp = (c as any).component;
    const isFn = typeof comp === 'function';
    const isObj = comp && typeof comp === 'object' && (comp as any).$$typeof;
    if (!hasType && !comp) {
      throw new Error(`Plugin ${meta.name} component[${key}] invalid: runtime component required`);
    }
    if (comp && !isFn && !isObj) {
      throw new Error(`Plugin ${meta.name} component[${key}] invalid: component should be function or React component`);
    }
  });
  if (plugin.configRenderers && typeof plugin.configRenderers === 'object') {
    Object.entries(plugin.configRenderers as Record<string, PluginComponent>).forEach(([key, c]) => {
      if (!c) {
        throw new Error(`Plugin ${meta.name} config[${key}] invalid: empty item`);
      }
      const comp = (c as any).component;
      const isFn = typeof comp === 'function';
      const isObj = comp && typeof comp === 'object' && (comp as any).$$typeof;
      if (!comp || (!isFn && !isObj)) {
        throw new Error(`Plugin ${meta.name} config[${key}] invalid: renderer required`);
      }
    });
  }

  // 可选：方法元信息校验（仅在提供时校验一致性与基本结构）
  if (plugin.methodsMeta && typeof plugin.methodsMeta === 'object') {
    Object.entries(plugin.methodsMeta as Record<string, any>).forEach(([key, metaItem]) => {
      if (!metaItem || typeof metaItem !== 'object') {
        throw new Error(`Plugin ${meta.name} methodsMeta[${key}] invalid: object required`);
      }
      if (metaItem.key && metaItem.key !== key) {
        throw new Error(`Plugin ${meta.name} methodsMeta[${key}] invalid: key mismatch`);
      }
      if (metaItem.params) {
        if (!Array.isArray(metaItem.params)) {
          throw new Error(`Plugin ${meta.name} methodsMeta[${key}] invalid: params must be array`);
        }
        metaItem.params.forEach((p: any, i: number) => {
          if (!p || typeof p !== 'object' || typeof p.name !== 'string') {
            throw new Error(`Plugin ${meta.name} methodsMeta[${key}].params[${i}] invalid`);
          }
        });
      }
      if (metaItem.returns && typeof metaItem.returns !== 'object') {
        throw new Error(`Plugin ${meta.name} methodsMeta[${key}] invalid: returns must be object`);
      }
      if (plugin.methods && !plugin.methods[key]) {
        throw new Error(`Plugin ${meta.name} methodsMeta[${key}] invalid: method not found`);
      }
    });
  }
}
