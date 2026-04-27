import { cloneDeep } from 'lodash-es';
import { baseSchema as BasicSchema } from './Basic/schema';
import { ALL_COMPONENT_TYPES, type ComponentType } from './componentTypes';
import { getComponentDescriptor, listComponentTypes } from './registry';

// 定义组件配置的类型
export type ComponentSchema = typeof BasicSchema | any; // 可以根据需要扩展其他组件的类型

// 组件配置统一从注册表读取

/**
 * 根据组件类型获取对应的配置
 * @param componentType 组件类型，如 ALL_COMPONENT_TYPES.INPUT_TEXT
 * @returns 返回该组件的配置对象，包含 editData 和 config
 */
export function getComponentSchema(componentType: ComponentType): ComponentSchema {
  const descriptor = getComponentDescriptor(componentType);
  return cloneDeep(descriptor.schema);
}

/**
 * 获取所有可用的组件类型
 * @returns 返回所有可用的组件类型数组
 */
export function getAvailableComponentTypes(): ComponentType[] {
  return listComponentTypes();
}

/**
 * 检查组件类型是否存在
 * @param componentType 组件类型
 * @returns 返回布尔值，表示该组件类型是否存在
 */
export function hasComponentSchema(componentType: string): componentType is ComponentType {
  return (listComponentTypes() as string[]).includes(componentType);
}

export function getComponentWidth(schema: any, itemType: string): string {
  if (!schema || !schema.config || !schema.config.width) {
    schema = getComponentSchema(itemType as any);
  }
  return schema.config.width;
}

export function getComponentConfig(schema: any, itemType: string): any {
  if (!schema || !schema.config) {
    schema = getComponentSchema(itemType as any);
  }
  return schema.config;
}

export const schema = {
  ...BasicSchema
};
