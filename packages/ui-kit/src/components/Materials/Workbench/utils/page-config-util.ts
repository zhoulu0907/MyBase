import { v4 as uuidv4 } from 'uuid';
import type { EditConfig } from '../../types';

/**
 * 页面配置工具类
 * 用于管理页面级别的配置，不作为工作台组件存在
 */

export const PAGE_CONFIG_TYPE = '__PAGE_CONFIG__';

export interface PageConfig {
  showHeader: boolean;
  showSidebar: boolean;
}

export interface PageConfigSchema extends EditConfig {
  id: string;
  type: typeof PAGE_CONFIG_TYPE;
  config: PageConfig;
  editData: Record<string, any>;
}

/**
 * 创建默认的页面配置
 */
export function createDefaultPageConfig(id?: string): PageConfigSchema {
  return {
    id: id || `page-config-${uuidv4()}`,
    type: PAGE_CONFIG_TYPE,
    config: { showHeader: true, showSidebar: true },
    editData: {}
  };
}

/**
 * 判断是否为页面配置（兼容旧类型）
 */
export function isPageConfig(schema: any): schema is PageConfigSchema {
  return schema?.type === PAGE_CONFIG_TYPE || schema?.type === 'page';
}

/**
 * 从 schemas 中查找页面配置
 */
export function findPageConfig(schemas: Record<string, EditConfig>): [string, PageConfigSchema] | null {
  const entry = Object.entries(schemas).find(([_, schema]) => isPageConfig(schema));
  return entry ? [entry[0], entry[1] as PageConfigSchema] : null;
}

/**
 * 获取或创建页面配置
 */
export function getOrCreatePageConfig(schemas: Record<string, EditConfig>): [string, PageConfigSchema] {
  const existing = findPageConfig(schemas);
  
  if (existing) {
    const [id, config] = existing;
    // 返回最新的配置数据
    return [
      id,
      {
        id,
        type: PAGE_CONFIG_TYPE,
        config: {
          showHeader: config.config?.showHeader ?? true,
          showSidebar: config.config?.showSidebar ?? true
        },
        editData: config.editData || {}
      }
    ];
  }
  
  // 创建新的页面配置
  const newConfig = createDefaultPageConfig();
  return [newConfig.id, newConfig];
}

/**
 * 判断组件类型是否应该在 workspace 中显示
 */
export function shouldShowInWorkspace(type: string): boolean {
  return type !== 'entity' && !isPageConfig({ type });
}
