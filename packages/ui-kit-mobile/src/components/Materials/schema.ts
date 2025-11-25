import { cloneDeep } from 'lodash-es';
import { ALL_COMPONENT_TYPES, FormSchema as BasicSchema, type ComponentType } from '@onebase/ui-kit';
import XLoadMoreSchema from '@/components/Materials/Basic/ListComponents/LoadMore/schema'

// 定义组件配置的类型
export type ComponentSchema = typeof BasicSchema | any; // 可以根据需要扩展其他组件的类型

// 创建组件配置映射
const componentSchemaMap: Partial<Record<ComponentType, ComponentSchema>> = {
  [ALL_COMPONENT_TYPES.INPUT_TEXT]: BasicSchema.XInputTextSchema,
  [ALL_COMPONENT_TYPES.INPUT_TEXTAREA]: BasicSchema.XInputTextAreaSchema,
  [ALL_COMPONENT_TYPES.INPUT_EMAIL]: BasicSchema.XInputEmailSchema,
  [ALL_COMPONENT_TYPES.INPUT_PHONE]: BasicSchema.XInputPhoneSchema,
  [ALL_COMPONENT_TYPES.INPUT_NUMBER]: BasicSchema.XInputNumberSchema,
  [ALL_COMPONENT_TYPES.DATE_PICKER]: BasicSchema.XDatePickerSchema,
  [ALL_COMPONENT_TYPES.TIME_PICKER]: BasicSchema.XTimePickerSchema,
  [ALL_COMPONENT_TYPES.DATE_RANGE_PICKER]: BasicSchema.XDateRangePickerSchema,
  [ALL_COMPONENT_TYPES.DATE_TIME_PICKER]: BasicSchema.XDateTimePickerSchema,
  [ALL_COMPONENT_TYPES.CHECKBOX]: BasicSchema.XCheckboxSchema,
  [ALL_COMPONENT_TYPES.RADIO]: BasicSchema.XRadioSchema,
  [ALL_COMPONENT_TYPES.SWITCH]: BasicSchema.XSwitchSchema,
  [ALL_COMPONENT_TYPES.SELECT_ONE]: BasicSchema.XSelectOneSchema,
  [ALL_COMPONENT_TYPES.SELECT_MUTIPLE]: BasicSchema.XSelectMutipleSchema,
  [ALL_COMPONENT_TYPES.IMG_UPLOAD]: BasicSchema.XImgUploadSchema,
  [ALL_COMPONENT_TYPES.CAROUSEL]: BasicSchema.XCarouselFormSchema,
  [ALL_COMPONENT_TYPES.FILE_UPLOAD]: BasicSchema.XFileUploadSchema,
  [ALL_COMPONENT_TYPES.AUTO_CODE]: BasicSchema.XAutoCodeSchema,
  [ALL_COMPONENT_TYPES.DEPT_SELECT]: BasicSchema.XDeptSelectSchema,

  [ALL_COMPONENT_TYPES.TABLE]: XLoadMoreSchema,
};

/**
 * 根据组件类型获取对应的配置
 * @param componentType 组件类型，如 ALL_COMPONENT_TYPES.INPUT_TEXT
 * @returns 返回该组件的配置对象，包含 editData 和 config
 */
export function getComponentSchema(componentType: ComponentType): ComponentSchema {
  const config = componentSchemaMap[componentType];

  if (!config) {
    throw new Error(`未找到组件类型 "${componentType}" 的配置`);
  }

  // 使用 lodash 的 cloneDeep 进行深度克隆，避免修改原始配置
  return cloneDeep(config);
}

/**
 * 获取所有可用的组件类型
 * @returns 返回所有可用的组件类型数组
 */
export function getAvailableComponentTypes(): ComponentType[] {
  return Object.keys(componentSchemaMap) as ComponentType[];
}

/**
 * 检查组件类型是否存在
 * @param componentType 组件类型
 * @returns 返回布尔值，表示该组件类型是否存在
 */
export function hasComponentSchema(componentType: string): componentType is ComponentType {
  return componentType in componentSchemaMap;
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

// export const schema = {
//   ...BasicSchema
// };
