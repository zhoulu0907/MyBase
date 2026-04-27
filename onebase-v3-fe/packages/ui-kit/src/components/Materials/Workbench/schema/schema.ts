import { cloneDeep } from 'lodash-es';

import type { WorkbenchComponentSchema } from '../registry/componentRegistry';
export type { WorkbenchComponentSchema } from '../registry/componentRegistry';
import WorkbenchSchemaBasic from '../WorkbenchBasicComponents/schema';
import WorkbenchSchemaAdvanced from '../WorkbenchAdvancedComponents/schema';
import { WORKBENCH_COMPONENT_TYPE_VALUES, type WorkbenchComponentType } from '../core/componentTypes';
import { WORKBENCH_WIDTH_OPTIONS, WORKBENCH_WIDTH_VALUES } from '../core/constants';

type WorkbenchComponentSchemaMap = Record<WorkbenchComponentType, WorkbenchComponentSchema>;

export const workbenchSchema = {
  ...WorkbenchSchemaBasic,
  ...WorkbenchSchemaAdvanced
};

// 创建组件配置映射
const workbenchComponentSchemaMap: WorkbenchComponentSchemaMap = WORKBENCH_COMPONENT_TYPE_VALUES.reduce(
  (acc, componentType) => {
    const schema = workbenchSchema[componentType as keyof typeof workbenchSchema];

    if (!schema) {
      throw new Error(`未找到工作台组件类型 "${componentType}" 的 schema 定义`);
    }

    acc[componentType] = schema as WorkbenchComponentSchema;
    return acc;
  },
  {} as WorkbenchComponentSchemaMap
);

/**
 * 根据组件类型获取对应的配置
 * @param componentType 组件类型
 * @returns 返回该组件的配置对象，包含 editData 和 config
 */
export function getWorkbenchComponentSchema<T extends WorkbenchComponentType>(
  componentType: T
): WorkbenchComponentSchemaMap[T] {
  const config = workbenchComponentSchemaMap[componentType];

  if (!config) {
    throw new Error(`未找到工作台组件类型 "${componentType}" 的配置`);
  }

  return cloneDeep(config);
}

/**
 * 获取所有可用的工作台组件类型
 * @returns 返回所有可用的组件类型数组
 */
export function getAvailableWorkbenchComponentTypes(): WorkbenchComponentType[] {
  return [...WORKBENCH_COMPONENT_TYPE_VALUES];
}

/**
 * 检查工作台组件类型是否存在
 * @param componentType 组件类型
 * @returns 返回布尔值，表示该组件类型是否存在
 */
export function hasWorkbenchComponentSchema(componentType: string): componentType is WorkbenchComponentType {
  return componentType in workbenchComponentSchemaMap;
}

/**
 * 获取工作台组件宽度
 * @param schema 组件 schema
 * @param itemType 组件类型
 * @returns 返回宽度字符串
 */
export function getWorkbenchComponentWidth(
  schema: Partial<WorkbenchComponentSchema> | undefined,
  itemType: WorkbenchComponentType
): string {
  const resolvedSchema = schema?.config?.width
    ? (schema as WorkbenchComponentSchema)
    : getWorkbenchComponentSchema(itemType);
  return resolvedSchema.config.width || WORKBENCH_WIDTH_VALUES[WORKBENCH_WIDTH_OPTIONS.FULL];
}

/**
 * 获取工作台组件配置
 * @param schema 组件 schema
 * @param itemType 组件类型
 * @returns 返回配置对象
 */
export function getWorkbenchComponentConfig(
  schema: Partial<WorkbenchComponentSchema> | undefined,
  itemType: WorkbenchComponentType
): WorkbenchComponentSchema['config'] {
  const resolvedSchema = schema?.config ? (schema as WorkbenchComponentSchema) : getWorkbenchComponentSchema(itemType);
  return resolvedSchema.config;
}

