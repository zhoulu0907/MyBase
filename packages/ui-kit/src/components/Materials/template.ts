import { buildDisplayNameMap, buildTemplate } from './registry';

/**
 * 组件 type 到 displayName 的静态映射
 * 手动维护，确保与模板配置保持一致
 */
export const COMPONENT_TYPE_DISPLAY_NAME_MAP: Record<string, string> = buildDisplayNameMap();

const allTemplate = buildTemplate();

export { allTemplate };
