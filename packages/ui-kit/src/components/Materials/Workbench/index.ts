/**
 * Workbench 模块统一导出
 */

import { WorkbenchBasicComp } from './WorkbenchBasicComponents';
import { WorkbenchAdvancedComp } from './WorkbenchAdvancedComponents';

// 组件导出
export const WorkbenchComp = { ...WorkbenchBasicComp, ...WorkbenchAdvancedComp };
export { QUICK_ENTRY_THEME_OPTIONS, QUICK_ENTRY_THEME_VALUES } from './WorkbenchBasicComponents';
export { WORKBENCH_CONFIG_TYPES } from './constants';

// 类型常量导出
export {
  ALL_WORKBENCH_COMPONENT_TYPES,
  WORKBENCH_COMPONENT_TYPES,
  WORKBENCH_COMPONENT_TYPE_VALUES
} from './componentTypes';
export type { WorkbenchComponentType } from './componentTypes';

// 组件映射导出
export { WORKBENCH_COMPONENT_MAP } from './componentsMap';

// Schema 相关导出
export {
  getAvailableWorkbenchComponentTypes,
  getWorkbenchComponentConfig,
  getWorkbenchComponentSchema,
  getWorkbenchComponentWidth,
  hasWorkbenchComponentSchema,
  workbenchSchema
} from './schema';
export type { WorkbenchComponentSchema } from './schema';

// 模板导出
export { workbenchTemplate, WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP } from './template';

// 类型导出
export { IDataConfigConfigType } from './types';