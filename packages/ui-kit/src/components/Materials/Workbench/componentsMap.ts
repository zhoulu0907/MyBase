import type { ComponentType } from 'react';

import { WORKBENCH_COMPONENT_REGISTRY } from './componentRegistry';
import { WORKBENCH_COMPONENT_TYPE_VALUES, type WorkbenchComponentType } from './componentTypes';

/**
 * 工作台组件类型与组件实现的映射表
 * 用于运行时组件渲染
 */
export const WORKBENCH_COMPONENT_MAP: Record<WorkbenchComponentType, ComponentType<any>> =
  WORKBENCH_COMPONENT_TYPE_VALUES.reduce((acc, componentType) => {
    acc[componentType] = WORKBENCH_COMPONENT_REGISTRY[componentType].component;
    return acc;
  }, {} as Record<WorkbenchComponentType, ComponentType<any>>);
