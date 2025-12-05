import { cloneDeep } from 'lodash-es';
import { baseSchema as BasicSchema } from './Basic/schema';
import { ALL_COMPONENT_TYPES, type ComponentType } from './componentTypes';

// 定义组件配置的类型
export type ComponentSchema = typeof BasicSchema | any; // 可以根据需要扩展其他组件的类型

// 创建组件配置映射
const componentSchemaMap: Partial<Record<ComponentType, ComponentSchema>> = {
  [ALL_COMPONENT_TYPES.INPUT_TEXT]: BasicSchema.XInputText,
  [ALL_COMPONENT_TYPES.INPUT_TEXTAREA]: BasicSchema.XInputTextArea,
  [ALL_COMPONENT_TYPES.INPUT_EMAIL]: BasicSchema.XInputEmail,
  [ALL_COMPONENT_TYPES.INPUT_PHONE]: BasicSchema.XInputPhone,
  [ALL_COMPONENT_TYPES.INPUT_NUMBER]: BasicSchema.XInputNumber,
  [ALL_COMPONENT_TYPES.DATE_PICKER]: BasicSchema.XDatePicker,
  [ALL_COMPONENT_TYPES.DATE_RANGE_PICKER]: BasicSchema.XDateRangePicker,
  [ALL_COMPONENT_TYPES.DATE_TIME_PICKER]: BasicSchema.XDateTimePicker,
  [ALL_COMPONENT_TYPES.TIME_PICKER]: BasicSchema.XTimePicker,
  [ALL_COMPONENT_TYPES.SWITCH]: BasicSchema.XSwitch,
  [ALL_COMPONENT_TYPES.RADIO]: BasicSchema.XRadio,
  [ALL_COMPONENT_TYPES.CHECKBOX]: BasicSchema.XCheckbox,
  [ALL_COMPONENT_TYPES.SELECT_ONE]: BasicSchema.XSelectOne,
  [ALL_COMPONENT_TYPES.SELECT_MUTIPLE]: BasicSchema.XSelectMutiple,
  [ALL_COMPONENT_TYPES.USER_SELECT]: BasicSchema.XUserSelect,
  [ALL_COMPONENT_TYPES.USER_MULTIPLE_SELECT]: BasicSchema.XUserSelect,
  [ALL_COMPONENT_TYPES.DEPT_SELECT]: BasicSchema.XDeptSelect,
  [ALL_COMPONENT_TYPES.DEPT_MULTIPLE_SELECT]: BasicSchema.XDeptSelect,
  [ALL_COMPONENT_TYPES.FILE_UPLOAD]: BasicSchema.XFileUpload,
  [ALL_COMPONENT_TYPES.IMG_UPLOAD]: BasicSchema.XImgUpload,
  [ALL_COMPONENT_TYPES.AUTO_CODE]: BasicSchema.XAutoCode,
  [ALL_COMPONENT_TYPES.RELATED_FORM]: BasicSchema.XRelatedForm,
  [ALL_COMPONENT_TYPES.STATIC_TEXT]: BasicSchema.XStaticText,
  [ALL_COMPONENT_TYPES.RICH_TEXT]: BasicSchema.XRichText,
  [ALL_COMPONENT_TYPES.CAROUSEL_FORM]: BasicSchema.XCarouselForm,
  [ALL_COMPONENT_TYPES.SUB_TABLE]: BasicSchema.XSubTable,
  [ALL_COMPONENT_TYPES.DATA_SELECT]: BasicSchema.XDataSelect,
  [ALL_COMPONENT_TYPES.FORMDIVIDER]: BasicSchema.XFormDivider,

  [ALL_COMPONENT_TYPES.COLUMN_LAYOUT]: BasicSchema.XColumnLayout,
  [ALL_COMPONENT_TYPES.TABS_LAYOUT]: BasicSchema.XTabsLayout,
  [ALL_COMPONENT_TYPES.COLLAPSE_LAYOUT]: BasicSchema.XCollapseLayout,

  [ALL_COMPONENT_TYPES.TABLE]: BasicSchema.XTable,
  [ALL_COMPONENT_TYPES.CALENDAR]: BasicSchema.XCalendar,
  [ALL_COMPONENT_TYPES.TIMELINE]: BasicSchema.XTimeline,
  [ALL_COMPONENT_TYPES.CAROUSEL]: BasicSchema.XCarousel,
  [ALL_COMPONENT_TYPES.COLLAPSE]: BasicSchema.XCollapse,
  [ALL_COMPONENT_TYPES.LIST]: BasicSchema.XList,

  [ALL_COMPONENT_TYPES.INFO_NOTICE]: BasicSchema.XInfoNotice,
  [ALL_COMPONENT_TYPES.IMAGE]: BasicSchema.XImage,
  [ALL_COMPONENT_TYPES.FILE]: BasicSchema.XFile,
  [ALL_COMPONENT_TYPES.TEXT]: BasicSchema.XText,
  [ALL_COMPONENT_TYPES.WEB_VIEW]: BasicSchema.XWebView,
  [ALL_COMPONENT_TYPES.DIVIDER]: BasicSchema.XDivider,
  [ALL_COMPONENT_TYPES.PLACEHOLDER]: BasicSchema.XPlaceholder
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

export const schema = {
  ...BasicSchema
};
