export { FormComp, FormSchema, LayoutComp, ListComp, ListSchema, NavigateComp, ShowComp, ShowSchema } from './Basic';
export * from './common';
export * from './types';
export * from './Workbench';
export {
  ALL_COMPONENT_TYPES,
  ENTITY_COMPONENT_TYPES,
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES,
  LIST_COMPONENT_TYPES,
  SHOW_COMPONENT_TYPES,
  ComponentType
} from './componentTypes';
export * from './constants';
export {
  getAvailableComponentTypes,
  getComponentConfig,
  getComponentSchema,
  getComponentWidth,
  hasComponentSchema,
  schema
} from './schema';
export { allTemplate, COMPONENT_TYPE_DISPLAY_NAME_MAP } from './template';
export type { EditConfig } from './types';
export { COMPONENT_MAP, COMPONENT_FIELD_MAP } from './componentsMap';

// 导出 Workbench 相关内容
export {
  WorkbenchComp,
  ALL_WORKBENCH_COMPONENT_TYPES,
  WORKBENCH_COMPONENT_TYPES,
  WORKBENCH_COMPONENT_TYPE_VALUES,
  WORKBENCH_COMPONENT_MAP,
  getAvailableWorkbenchComponentTypes,
  getWorkbenchComponentConfig,
  getWorkbenchComponentSchema,
  getWorkbenchComponentWidth,
  hasWorkbenchComponentSchema,
  workbenchSchema,
  workbenchTemplate,
  WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP
} from './Workbench';
export type { WorkbenchComponentSchema, WorkbenchComponentType } from './Workbench';
export {
  COMPONENT_REGISTRY,
  getComponentDescriptor,
  listComponentTypes,
  buildTemplate,
  buildDisplayNameMap,
  buildComponentFieldMap,
  buildEntityToComponentMap
} from './registry';
export {
  buildFormComponentTypes,
  buildLayoutComponentTypes,
  buildListComponentTypes,
  buildShowComponentTypes,
  buildAllComponentTypes
} from './registry';
export {
  registerComponent,
  registerComponents,
  unregisterComponent,
  registerMaterialsPlugin,
  invalidateMaterialsPlugin,
  getComponentImpl
} from './registry';
export type { MaterialsPlugin } from './registry';
export {
  loadMaterialsPlugin,
  unloadMaterialsPlugin,
  reloadMaterialsPlugin,
  listMaterialsPlugins,
  getMaterialsPluginStatus
} from './registry';
export {
  listPluginComponentTypes,
  buildPluginComponentTypes,
  isPluginComponentType
} from './registry';

import { initComponentImplementations } from './registry';
initComponentImplementations();
