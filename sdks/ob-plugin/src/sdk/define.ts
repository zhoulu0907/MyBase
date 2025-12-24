import { LoadedPlugin, PluginComponent } from './types';

/**
 * 定义插件的辅助函数
 * 自动处理命名空间前缀，避免与其他插件冲突
 * 1. 自动给 components 和 configRenderers 中的 type 加上插件名为前缀
 * 2. 自动遍历 components 的 schema，将 type 引用也加上前缀（如果该 type 存在于当前插件中）
 */
export function definePlugin(plugin: LoadedPlugin): LoadedPlugin {
  const { name } = plugin.meta;
  const prefix = (str: string) => `${name}_${str}`;

  // 收集所有本地定义的组件类型（包括 configRenderers）
  const localTypes = new Set<string>();
  
  if (plugin.components) {
    Object.values(plugin.components).forEach(c => {
      if (c.type) localTypes.add(c.type);
    });
  }
  
  if (plugin.configRenderers) {
    Object.values(plugin.configRenderers).forEach(c => {
      if (c.type) localTypes.add(c.type);
    });
  }

  // 处理组件
  if (plugin.components) {
    Object.values(plugin.components).forEach(c => {
      if (c.type) {
        // 更新 Schema 中的引用
        if (c.schema && c.schema.editData && Array.isArray(c.schema.editData)) {
          c.schema.editData.forEach((item: any) => {
            if (item && typeof item === 'object' && item.type && localTypes.has(item.type)) {
              item.type = prefix(item.type);
            }
          });
        }
        // 更新自身 type
        c.type = prefix(c.type);
      }
    });
  }

  // 处理配置渲染器
  if (plugin.configRenderers) {
    Object.values(plugin.configRenderers).forEach(c => {
      if (c.type) {
        c.type = prefix(c.type);
      }
    });
  }

  return plugin;
}
