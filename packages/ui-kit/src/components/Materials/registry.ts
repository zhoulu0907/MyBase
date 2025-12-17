/**
 * 组件注册表（Single Source of Truth）
 *
 * 统一维护组件的：类型字符串、schema、模板展示信息、字段能力映射（fieldMap）、默认实体字段映射（entityMap）。
 * 其他模块（类型常量、模板、组件与实体映射等）均从此注册表派生，避免重复维护与配置漂移。
 */
import { cloneDeep } from 'lodash-es'
import { baseSchema as BasicSchema } from './Basic/schema'
import { FormComp, LayoutComp, ListComp, ShowComp } from './Basic'
import { ENTITY_FIELD_TYPE } from '@/components/DataFactory'
import type { ComponentType } from './componentTypes'
import type { ComponentType as ReactComponentType } from 'react'

/** 组件分类类型 */
type ComponentCategory = 'layout' | 'form' | 'list' | 'show'

/**
 * 组件模板展示信息（用于左侧物料面板）
 */
interface ComponentTemplate {
  h: number
  w: number
  displayName: string
  icon: string
  category: ComponentCategory
}

/**
 * 注册表中的组件描述信息
 */
export interface ComponentDescriptor {
  type: ComponentType
  schema: any
  template: ComponentTemplate
  fieldMap?: string[]
  entityMap?: string[]
  component?: ReactComponentType<any>
}

/**
 * 局部类型常量集合，集中维护所有组件类型字符串，避免在注册表条目里重复书写
 */
const COMPONENT_TYPE = {
  // 布局
  COLUMN_LAYOUT: 'XColumnLayout',
  TABS_LAYOUT: 'XTabsLayout',
  COLLAPSE_LAYOUT: 'XCollpaseLayout',
  PREVIEW_COLUMN_LAYOUT: 'XPreviewColumnLayout',
  PREVIEW_TABS_LAYOUT: 'XPreviewTabsLayout',
  PREVIEW_COLLAPSE_LAYOUT: 'XPreviewCollpaseLayout',
  // 表单
  INPUT_TEXT: 'XInputText',
  INPUT_TEXTAREA: 'XInputTextArea',
  INPUT_EMAIL: 'XInputEmail',
  INPUT_PHONE: 'XInputPhone',
  INPUT_NUMBER: 'XInputNumber',
  DATE_PICKER: 'XDatePicker',
  DATE_RANGE_PICKER: 'XDateRangePicker',
  DATE_TIME_PICKER: 'XDateTimePicker',
  TIME_PICKER: 'XTimePicker',
  SWITCH: 'XSwitch',
  RADIO: 'XRadio',
  CHECKBOX: 'XCheckbox',
  SELECT_ONE: 'XSelectOne',
  SELECT_MUTIPLE: 'XSelectMutiple',
  USER_SELECT: 'XUserSelect',
  USER_MULTIPLE_SELECT: 'XUserMultipleSelect',
  DEPT_SELECT: 'XDeptSelect',
  DEPT_MULTIPLE_SELECT: 'XDeptMultipleSelect',
  FILE_UPLOAD: 'XFileUpload',
  IMG_UPLOAD: 'XImgUpload',
  AUTO_CODE: 'XAutoCode',
  RELATED_FORM: 'XRelatedForm',
  STATIC_TEXT: 'XStaticText',
  RICH_TEXT: 'XRichText',
  CAROUSEL_FORM: 'XCarouselForm',
  SUB_TABLE: 'XSubTable',
  DATA_SELECT: 'XDataSelect',
  // 列表
  TABLE: 'XTable',
  CALENDAR: 'XCalendar',
  TIMELINE: 'XTimeline',
  LIST_COLLAPSE: 'XCollapse',
  LIST_CAROUSEL: 'XCarousel',
  LIST: 'XList',
  // 展示
  INFO_NOTICE: 'XInfoNotice',
  TEXT: 'XText',
  IMAGE: 'XImage',
  FILE: 'XFile',
  WEB_VIEW: 'XWebView',
  DIVIDER: 'XDivider',
  PLACEHOLDER: 'XPlaceholder'
} as const


/**
 * 组件注册表：使用组件类型字符串作为 key（例如 'XInputText'）
 */
/**
 * 设计目标：
 * - 作为所有组件元信息的单一事实来源（Single Source of Truth）
 * - 支持内置组件与插件组件统一建模与查询
 * 字段说明：
 * - `type`、`schema`、`template`、`fieldMap`、`entityMap`、`component`
 */
export const COMPONENT_REGISTRY: Partial<Record<ComponentType, ComponentDescriptor>> = {
  [COMPONENT_TYPE.COLUMN_LAYOUT]: {
    type: COMPONENT_TYPE.COLUMN_LAYOUT,
    schema: cloneDeep(BasicSchema.XColumnLayout),
    template: { h: 36, w: 118, displayName: '分栏布局', icon: 'col_layout_cp.svg', category: 'layout' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.TABS_LAYOUT]: {
    type: COMPONENT_TYPE.TABS_LAYOUT,
    schema: cloneDeep(BasicSchema.XTabsLayout),
    template: { h: 36, w: 118, displayName: '页签组件', icon: 'tabs_layout_cp.svg', category: 'layout' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.COLLAPSE_LAYOUT]: {
    type: COMPONENT_TYPE.COLLAPSE_LAYOUT,
    schema: cloneDeep(BasicSchema.XCollapseLayout),
    template: { h: 36, w: 118, displayName: '分组布局', icon: 'colpase_layout_cp.svg', category: 'layout' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.INPUT_TEXT]: {
    type: COMPONENT_TYPE.INPUT_TEXT,
    schema: cloneDeep(BasicSchema.XInputText),
    template: { h: 36, w: 118, displayName: '单行文本', icon: 'text_input_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.TEXT.VALUE,
      ENTITY_FIELD_TYPE.LONG_TEXT.VALUE,
      ENTITY_FIELD_TYPE.ID.VALUE
    ],
    entityMap: [
      ENTITY_FIELD_TYPE.TEXT.VALUE,
      ENTITY_FIELD_TYPE.URL.VALUE,
      ENTITY_FIELD_TYPE.ADDRESS.VALUE,
      ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE,
      ENTITY_FIELD_TYPE.PASSWORD.VALUE,
      ENTITY_FIELD_TYPE.ENCRYPTED.VALUE,
      ENTITY_FIELD_TYPE.AGGREGATE.VALUE,
      ENTITY_FIELD_TYPE.ID.VALUE
    ]
  },
  [COMPONENT_TYPE.INPUT_TEXTAREA]: {
    type: COMPONENT_TYPE.INPUT_TEXTAREA,
    schema: cloneDeep(BasicSchema.XInputTextArea),
    template: { h: 36, w: 118, displayName: '多行文本', icon: 'textarea_input_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.TEXT.VALUE,
      ENTITY_FIELD_TYPE.LONG_TEXT.VALUE,
      ENTITY_FIELD_TYPE.ID.VALUE
    ],
    entityMap: [
      ENTITY_FIELD_TYPE.LONG_TEXT.VALUE
    ]
  },
  [COMPONENT_TYPE.STATIC_TEXT]: {
    type: COMPONENT_TYPE.STATIC_TEXT,
    schema: cloneDeep(BasicSchema.XStaticText),
    template: { h: 36, w: 118, displayName: '静态文本', icon: 'static_text_cp.svg', category: 'form' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.RICH_TEXT]: {
    type: COMPONENT_TYPE.RICH_TEXT,
    schema: cloneDeep(BasicSchema.XRichText),
    template: { h: 36, w: 118, displayName: '富文本', icon: 'rich_text_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.TEXT.VALUE,
      ENTITY_FIELD_TYPE.LONG_TEXT.VALUE
    ],
    entityMap: []
  },
  [COMPONENT_TYPE.INPUT_EMAIL]: {
    type: COMPONENT_TYPE.INPUT_EMAIL,
    schema: cloneDeep(BasicSchema.XInputEmail),
    template: { h: 36, w: 118, displayName: '邮箱', icon: 'email_input_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.EMAIL.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.EMAIL.VALUE]
  },
  [COMPONENT_TYPE.INPUT_PHONE]: {
    type: COMPONENT_TYPE.INPUT_PHONE,
    schema: cloneDeep(BasicSchema.XInputPhone),
    template: { h: 36, w: 118, displayName: '电话', icon: 'phone_input_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.PHONE.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.PHONE.VALUE]
  },
  [COMPONENT_TYPE.INPUT_NUMBER]: {
    type: COMPONENT_TYPE.INPUT_NUMBER,
    schema: cloneDeep(BasicSchema.XInputNumber),
    template: { h: 36, w: 118, displayName: '数字录入', icon: 'number_input_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.NUMBER.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.NUMBER.VALUE]
  },
  [COMPONENT_TYPE.DATE_PICKER]: {
    type: COMPONENT_TYPE.DATE_PICKER,
    schema: cloneDeep(BasicSchema.XDatePicker),
    template: { h: 36, w: 118, displayName: '日期', icon: 'date_picker_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.DATE.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.DATE.VALUE]
  },
  [COMPONENT_TYPE.DATE_RANGE_PICKER]: {
    type: COMPONENT_TYPE.DATE_RANGE_PICKER,
    schema: cloneDeep(BasicSchema.XDateRangePicker),
    template: { h: 36, w: 118, displayName: '日期区间', icon: 'date_picker_cp.svg', category: 'form' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.DATE_TIME_PICKER]: {
    type: COMPONENT_TYPE.DATE_TIME_PICKER,
    schema: cloneDeep(BasicSchema.XDateTimePicker),
    template: { h: 36, w: 118, displayName: '日期时间', icon: 'time_picker_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.DATETIME.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.DATETIME.VALUE]
  },
  [COMPONENT_TYPE.TIME_PICKER]: {
    type: COMPONENT_TYPE.TIME_PICKER,
    schema: cloneDeep(BasicSchema.XTimePicker),
    template: { h: 36, w: 118, displayName: '时间', icon: 'time_picker_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.TIME.VALUE,
      ENTITY_FIELD_TYPE.DATETIME.VALUE
    ],
    entityMap: [ENTITY_FIELD_TYPE.TIME.VALUE]
  },
  [COMPONENT_TYPE.SWITCH]: {
    type: COMPONENT_TYPE.SWITCH,
    schema: cloneDeep(BasicSchema.XSwitch),
    template: { h: 36, w: 118, displayName: '开关', icon: 'switch_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.BOOLEAN.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.BOOLEAN.VALUE]
  },
  [COMPONENT_TYPE.RADIO]: {
    type: COMPONENT_TYPE.RADIO,
    schema: cloneDeep(BasicSchema.XRadio),
    template: { h: 36, w: 118, displayName: '单选框', icon: 'radio_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.RADIO.VALUE,
      ENTITY_FIELD_TYPE.SELECT.VALUE
    ],
    entityMap: []
  },
  [COMPONENT_TYPE.CHECKBOX]: {
    type: COMPONENT_TYPE.CHECKBOX,
    schema: cloneDeep(BasicSchema.XCheckbox),
    template: { h: 36, w: 118, displayName: '复选框', icon: 'checkbox_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.CHECKBOX.VALUE,
      ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
    ],
    entityMap: []
  },
  [COMPONENT_TYPE.SELECT_ONE]: {
    type: COMPONENT_TYPE.SELECT_ONE,
    schema: cloneDeep(BasicSchema.XSelectOne),
    template: { h: 36, w: 118, displayName: '下拉单选', icon: 'select_one_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.RADIO.VALUE,
      ENTITY_FIELD_TYPE.SELECT.VALUE
    ],
    entityMap: [ENTITY_FIELD_TYPE.SELECT.VALUE]
  },
  [COMPONENT_TYPE.SELECT_MUTIPLE]: {
    type: COMPONENT_TYPE.SELECT_MUTIPLE,
    schema: cloneDeep(BasicSchema.XSelectMutiple),
    template: { h: 36, w: 118, displayName: '下拉多选', icon: 'select_mutiple_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.CHECKBOX.VALUE,
      ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
    ],
    entityMap: [ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE]
  },
  [COMPONENT_TYPE.USER_SELECT]: {
    type: COMPONENT_TYPE.USER_SELECT,
    schema: cloneDeep(BasicSchema.XUserSelect),
    template: { h: 36, w: 118, displayName: '人员选择', icon: 'user_select_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.USER.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.USER.VALUE]
  },
  [COMPONENT_TYPE.USER_MULTIPLE_SELECT]: {
    type: COMPONENT_TYPE.USER_MULTIPLE_SELECT,
    schema: cloneDeep(BasicSchema.XUserSelect),
    template: { h: 36, w: 118, displayName: '人员多选', icon: 'user_select_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.MULTI_USER.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.MULTI_USER.VALUE]
  },
  [COMPONENT_TYPE.DEPT_SELECT]: {
    type: COMPONENT_TYPE.DEPT_SELECT,
    schema: cloneDeep(BasicSchema.XDeptSelect),
    template: { h: 36, w: 118, displayName: '部门选择', icon: 'dept_select_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.DEPARTMENT.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.DEPARTMENT.VALUE]
  },
  [COMPONENT_TYPE.DEPT_MULTIPLE_SELECT]: {
    type: COMPONENT_TYPE.DEPT_MULTIPLE_SELECT,
    schema: cloneDeep(BasicSchema.XDeptSelect),
    template: { h: 36, w: 118, displayName: '部门多选', icon: 'dept_select_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE]
  },
  [COMPONENT_TYPE.FILE_UPLOAD]: {
    type: COMPONENT_TYPE.FILE_UPLOAD,
    schema: cloneDeep(BasicSchema.XFileUpload),
    template: { h: 36, w: 118, displayName: '文件上传', icon: 'upload_file_cp.svg', category: 'form' },
    fieldMap: [
      ENTITY_FIELD_TYPE.FILE.VALUE,
      ENTITY_FIELD_TYPE.IMAGE.VALUE
    ],
    entityMap: [ENTITY_FIELD_TYPE.FILE.VALUE]
  },
  [COMPONENT_TYPE.IMG_UPLOAD]: {
    type: COMPONENT_TYPE.IMG_UPLOAD,
    schema: cloneDeep(BasicSchema.XImgUpload),
    template: { h: 36, w: 118, displayName: '图片上传', icon: 'upload_image_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.IMAGE.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.IMAGE.VALUE]
  },
  [COMPONENT_TYPE.AUTO_CODE]: {
    type: COMPONENT_TYPE.AUTO_CODE,
    schema: cloneDeep(BasicSchema.XAutoCode),
    template: { h: 36, w: 118, displayName: '自动编号', icon: 'readonly_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.AUTO_CODE.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.AUTO_CODE.VALUE]
  },
  [COMPONENT_TYPE.RELATED_FORM]: {
    type: COMPONENT_TYPE.RELATED_FORM,
    schema: cloneDeep(BasicSchema.XRelatedForm),
    template: { h: 36, w: 118, displayName: '关联表单', icon: 'related_form_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.RELATION.VALUE],
    entityMap: [ENTITY_FIELD_TYPE.RELATION.VALUE]
  },
  [COMPONENT_TYPE.CAROUSEL_FORM]: {
    type: COMPONENT_TYPE.CAROUSEL_FORM,
    schema: cloneDeep(BasicSchema.XCarouselForm),
    template: { h: 36, w: 118, displayName: '轮播图', icon: 'carousel_cp.svg', category: 'form' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.SUB_TABLE]: {
    type: COMPONENT_TYPE.SUB_TABLE,
    schema: cloneDeep(BasicSchema.XSubTable),
    template: { h: 36, w: 118, displayName: '子表单', icon: 'sub_table_cp.svg', category: 'form' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.DATA_SELECT]: {
    type: COMPONENT_TYPE.DATA_SELECT,
    schema: cloneDeep(BasicSchema.XDataSelect),
    template: { h: 36, w: 118, displayName: '数据选择', icon: 'data_select_cp.svg', category: 'form' },
    fieldMap: [ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE],
    entityMap: [
      ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE,
      ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE
    ]
  },
  [COMPONENT_TYPE.TABLE]: {
    type: COMPONENT_TYPE.TABLE,
    schema: cloneDeep(BasicSchema.XTable),
    template: { h: 48, w: 68, displayName: '表格', icon: 'table_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.CALENDAR]: {
    type: COMPONENT_TYPE.CALENDAR,
    schema: cloneDeep(BasicSchema.XCalendar),
    template: { h: 48, w: 68, displayName: '日历', icon: 'calendar_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.TIMELINE]: {
    type: COMPONENT_TYPE.TIMELINE,
    schema: cloneDeep(BasicSchema.XTimeline),
    template: { h: 48, w: 68, displayName: '时间轴', icon: 'timeline_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.LIST_COLLAPSE]: {
    type: COMPONENT_TYPE.LIST_COLLAPSE,
    schema: cloneDeep(BasicSchema.XCollapse),
    template: { h: 48, w: 68, displayName: '看板', icon: 'kanban_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.LIST_CAROUSEL]: {
    type: COMPONENT_TYPE.LIST_CAROUSEL,
    schema: cloneDeep(BasicSchema.XCarousel),
    template: { h: 48, w: 68, displayName: '图片轮播', icon: 'carousel_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.LIST]: {
    type: COMPONENT_TYPE.LIST,
    schema: cloneDeep(BasicSchema.XList),
    template: { h: 48, w: 68, displayName: '画布列表', icon: 'canvas_list_cp.svg', category: 'list' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.INFO_NOTICE]: {
    type: COMPONENT_TYPE.INFO_NOTICE,
    schema: cloneDeep(BasicSchema.XInfoNotice),
    template: { h: 48, w: 68, displayName: '信息公告', icon: 'info_notice_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.TEXT]: {
    type: COMPONENT_TYPE.TEXT,
    schema: cloneDeep(BasicSchema.XText),
    template: { h: 48, w: 68, displayName: '静态文本', icon: 'static_text_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.IMAGE]: {
    type: COMPONENT_TYPE.IMAGE,
    schema: cloneDeep(BasicSchema.XImage),
    template: { h: 48, w: 68, displayName: '静态图片', icon: 'static_image_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.FILE]: {
    type: COMPONENT_TYPE.FILE,
    schema: cloneDeep(BasicSchema.XFile),
    template: { h: 48, w: 68, displayName: '静态文件', icon: 'static_file_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.WEB_VIEW]: {
    type: COMPONENT_TYPE.WEB_VIEW,
    schema: cloneDeep(BasicSchema.XWebView),
    template: { h: 48, w: 68, displayName: '网页组件', icon: 'web_component_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.DIVIDER]: {
    type: COMPONENT_TYPE.DIVIDER,
    schema: cloneDeep(BasicSchema.XDivider),
    template: { h: 36, w: 118, displayName: '分割线', icon: 'divider_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
  [COMPONENT_TYPE.PLACEHOLDER]: {
    type: COMPONENT_TYPE.PLACEHOLDER,
    schema: cloneDeep(BasicSchema.XPlaceholder),
    template: { h: 36, w: 118, displayName: '占位符', icon: 'placeholder_cp.svg', category: 'show' },
    fieldMap: [],
    entityMap: []
  },
}

/**
 * 初始化运行期组件实现：按模板分类自动注入内置组件的 React 实现
 */
export function initComponentImplementations(): void {
  for (const [type, descriptor] of Object.entries(COMPONENT_REGISTRY)) {
    if (!descriptor) continue
    const category = descriptor.template.category
    let impl: ReactComponentType<any> | undefined
    if (category === 'form') impl = (FormComp as any)[type]
    else if (category === 'layout') impl = (LayoutComp as any)[type]
    else if (category === 'list') impl = (ListComp as any)[type]
    else if (category === 'show') impl = (ShowComp as any)[type]
    if (impl) descriptor.component = impl
  }
}

/**
 * 获取指定组件类型的注册表条目
 */
/**
 * @param type 组件类型字符串（如 `XInputText`）
 * @returns 对应的 `ComponentDescriptor` 对象
 * @throws 若未找到给定类型的条目则抛出异常
 */
export function getComponentDescriptor(type: ComponentType): ComponentDescriptor {
  const descriptor = COMPONENT_REGISTRY[type]
  if (!descriptor) throw new Error(`未找到组件类型 "${type}" 的配置`)
  return descriptor
}

/**
 * 获取所有已注册的组件类型列表
 */
/**
 * @returns 当前注册表中的组件类型字符串数组
 */
export function listComponentTypes(): ComponentType[] {
  return Object.keys(COMPONENT_REGISTRY) as ComponentType[]
}

/**
 * 构建组件类型到展示名称的映射
 */
/**
 * @returns 映射表：`{ [type]: displayName }`
 */
export function buildDisplayNameMap(): Record<string, string> {
  const displayNameMap: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    displayNameMap[componentType] = descriptor.template.displayName
  }
  return displayNameMap
}

/**
 * 构建物料面板模板数据（按分类聚合）
 */
/**
 * 说明：会过滤掉仅用于预览的布局类型（如 `XPreview*`）
 * @returns 结构化模板数据：`{ base: [layout, form, list, show] }`
 */
export function buildTemplate() {
  const templateGroups: Record<ComponentCategory, { category: ComponentCategory; items: Array<{ type: string; h: number; w: number; displayName: string; icon: string; category: string }> }> = {
    layout: { category: 'layout', items: [] },
    form: { category: 'form', items: [] },
    list: { category: 'list', items: [] },
    show: { category: 'show', items: [] }
  }
  for (const componentType of listComponentTypes()) {
    if (
      componentType === COMPONENT_TYPE.PREVIEW_COLUMN_LAYOUT ||
      componentType === COMPONENT_TYPE.PREVIEW_TABS_LAYOUT ||
      componentType === COMPONENT_TYPE.PREVIEW_COLLAPSE_LAYOUT
    ) {
      continue
    }
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    const item = {
      type: descriptor.type,
      h: descriptor.template.h,
      w: descriptor.template.w,
      displayName: descriptor.template.displayName,
      icon: descriptor.template.icon,
      category: descriptor.template.category
    }
    templateGroups[descriptor.template.category].items.push(item)
  }
  return { base: [templateGroups.layout, templateGroups.form, templateGroups.list, templateGroups.show] }
}

/**
 * 构建 组件类型 → 可绑定字段类型 的映射表
 */
/**
 * @returns 映射表：`{ [type]: string[] }`，数组元素为实体字段类型标识
 */
export function buildComponentFieldMap(): Record<string, string[]> {
  const componentFieldMap: Record<string, string[]> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    componentFieldMap[componentType] = descriptor.fieldMap ?? []
  }
  return componentFieldMap
}

/**
 * 构建 实体字段类型 → 默认组件类型 的映射表
 */
/**
 * 策略：按注册表顺序首次出现即作为该字段类型的默认组件
 * @returns 映射表：`{ [entityFieldType]: componentType }`
 */
export function buildEntityToComponentMap(): Record<string, string> {
  const entityToComponentMap: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    if (descriptor.entityMap) {
      for (const fieldType of descriptor.entityMap) {
        if (entityToComponentMap[fieldType] === undefined) {
          entityToComponentMap[fieldType] = descriptor.type as string
        }
      }
    }
  }
  return entityToComponentMap
}

/**
 * 将组件类型字符串（如 'XInputText'）转换为常量键名（'INPUT_TEXT'）
 */
/**
 * 规则：去掉前缀 `X`，再将驼峰转下划线并大写，例如：
 * - `XInputText` → `INPUT_TEXT`
 * - `XDateRangePicker` → `DATE_RANGE_PICKER`
 * @param typeString 组件类型字符串
 * @returns 常量键名字符串
 */
function typeStringToKey(typeString: string): string {
  const name = typeString.startsWith('X') ? typeString.slice(1) : typeString
  return name.replace(/([a-z0-9])([A-Z])/g, '$1_$2').toUpperCase()
}

/**
 * 从注册表派生表单组件类型常量对象
 */
/**
 * @returns 常量对象：`{ INPUT_TEXT: 'XInputText', ... }`
 */
export function buildFormComponentTypes(): Record<string, string> {
  const formTypes: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    if (descriptor.template.category !== 'form') continue
    const key = typeStringToKey(descriptor.type as string)
    formTypes[key] = descriptor.type as string
  }
  return formTypes
}

/**
 * 从注册表派生布局组件类型常量对象（不含预览类型）
 */
/**
 * @returns 常量对象：`{ COLUMN_LAYOUT: 'XColumnLayout', ... }`
 */
export function buildLayoutComponentTypes(): Record<string, string> {
  const layoutTypes: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    if (descriptor.template.category !== 'layout') continue
    const key = typeStringToKey(descriptor.type as string)
    layoutTypes[key] = descriptor.type as string
  }
  return layoutTypes
}

/**
 * 从注册表派生列表组件类型常量对象
 */
/**
 * @returns 常量对象：`{ TABLE: 'XTable', ... }`
 */
export function buildListComponentTypes(): Record<string, string> {
  const listTypes: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    if (descriptor.template.category !== 'list') continue
    const key = typeStringToKey(descriptor.type as string)
    listTypes[key] = descriptor.type as string
  }
  return listTypes
}

/**
 * 从注册表派生展示组件类型常量对象
 */
/**
 * @returns 常量对象：`{ TEXT: 'XText', ... }`
 */
export function buildShowComponentTypes(): Record<string, string> {
  const showTypes: Record<string, string> = {}
  for (const componentType of listComponentTypes()) {
    const descriptor = COMPONENT_REGISTRY[componentType]
    if (!descriptor) continue
    if (descriptor.template.category !== 'show') continue
    const key = typeStringToKey(descriptor.type as string)
    showTypes[key] = descriptor.type as string
  }
  return showTypes
}

/**
 * 从注册表派生所有组件类型常量对象（合并四类）
 */
/**
 * @returns 常量对象：将表单、布局、列表、展示四类常量合并
 */
export function buildAllComponentTypes(): Record<string, string> {
  return {
    ...buildFormComponentTypes(),
    ...buildLayoutComponentTypes(),
    ...buildListComponentTypes(),
    ...buildShowComponentTypes()
  }
}

/**
 * 获取所有插件注册的组件类型集合
 * @returns 插件组件类型数组（去重）
 */
export function listPluginComponentTypes(): ComponentType[] {
  const set = new Set<ComponentType>()
  for (const types of PLUGIN_COMPONENTS.values()) {
    for (const t of types) set.add(t)
  }
  return [...set]
}

/**
 * 构建插件组件类型常量对象
 * @returns 常量对象：`{ MY_CUSTOM_INPUT: 'XMyCustomInput', ... }`
 */
export function buildPluginComponentTypes(): Record<string, string> {
  const map: Record<string, string> = {}
  for (const t of listPluginComponentTypes()) {
    const key = typeStringToKey(t as string)
    map[key] = t as string
  }
  return map
}

/**
 * 判定是否为插件组件类型
 * @param type 组件类型字符串
 * @returns 若属于插件注册的类型则返回 `true`
 */
export function isPluginComponentType(type: string): type is ComponentType {
  return listPluginComponentTypes().includes(type as ComponentType)
}

/**
 * 组件注册选项
 * - `plugin`：插件 ID，用于建立关联
 * - `override`：允许覆盖已有类型
 */
type RegisterOptions = { plugin?: string; override?: boolean }

/**
 * 插件描述：由插件提供一组组件注册条目
 * - `id`：插件唯一标识
 * - `components`：该插件要注册的组件集合
 */
export type MaterialsPlugin = { id: string; components: ComponentDescriptor[] }

/** 插件 → 组件类型集合 的映射 */
const PLUGIN_COMPONENTS = new Map<string, Set<ComponentType>>()
/** 已登记的插件集合（含静态定义的组件条目） */
const PLUGINS = new Map<string, MaterialsPlugin>()
/** 插件状态：`registered` | `loaded` | `unloaded` | `invalidated` */
const PLUGIN_STATUS = new Map<string, 'registered' | 'loaded' | 'unloaded' | 'invalidated'>()

/**
 * 注册单个组件
 * 注意：默认不允许覆写；传入 `override: true` 可覆写。若传入 `plugin` 会建立插件关联。
 * @param descriptor 组件注册条目
 * @param options 注册选项
 */
export function registerComponent(descriptor: ComponentDescriptor, options?: RegisterOptions): void {
  const { plugin, override } = options ?? {}
  const type = descriptor.type as ComponentType
  if (!override && COMPONENT_REGISTRY[type]) {
    throw new Error(`组件类型 "${type}" 已存在`)
  }
  COMPONENT_REGISTRY[type] = {
    ...descriptor,
    schema: cloneDeep(descriptor.schema)
  }
  if (plugin) {
    let set = PLUGIN_COMPONENTS.get(plugin)
    if (!set) {
      set = new Set<ComponentType>()
      PLUGIN_COMPONENTS.set(plugin, set)
    }
    set.add(type)
  }
}

/**
 * 获取组件的运行期实现（React 组件）
 * @param type 组件类型字符串
 * @returns 对应的 React 组件实现；若未找到则返回 `undefined`
 */
export function getComponentImpl(type: ComponentType): ReactComponentType<any> | undefined {
  const d = COMPONENT_REGISTRY[type]
  if (!d) return undefined
  if (d.component) return d.component
  const category = d.template.category
  if (category === 'form') return (FormComp as any)[type]
  if (category === 'layout') return (LayoutComp as any)[type]
  if (category === 'list') return (ListComp as any)[type]
  if (category === 'show') return (ShowComp as any)[type]
  return undefined
}

/**
 * 批量注册多个组件
 * @param descriptors 一组组件注册条目
 * @param options 注册选项（可选），会传递给每个条目
 */
export function registerComponents(descriptors: ComponentDescriptor[], options?: RegisterOptions): void {
  for (const d of descriptors) registerComponent(d, options)
}

/**
 * 注销（移除）某个组件类型
 * 说明：同时会从插件关联映射中移除该类型，若该插件不再有组件，则清理其记录
 * @param type 组件类型字符串
 */
export function unregisterComponent(type: ComponentType): void {
  delete COMPONENT_REGISTRY[type]
  for (const [pid, set] of PLUGIN_COMPONENTS) {
    if (set.delete(type) && set.size === 0) PLUGIN_COMPONENTS.delete(pid)
  }
}

/**
 * 登记插件（注册其所有组件），并记录插件状态为 `registered`
 * @param plugin 插件描述（含组件集合）
 * @param options 可选项：允许覆写已有类型
 * @returns 成功注册的组件类型数组
 */
export function registerMaterialsPlugin(plugin: MaterialsPlugin, options?: { override?: boolean }): ComponentType[] {
  registerComponents(plugin.components, { plugin: plugin.id, override: options?.override })
  PLUGINS.set(plugin.id, plugin)
  PLUGIN_STATUS.set(plugin.id, 'registered')
  return plugin.components.map((c) => c.type as ComponentType)
}

/**
 * 彻底失效一个插件：移除其组件、清理关联映射、删除插件记录
 * @param id 插件 ID
 */
export function invalidateMaterialsPlugin(id: string): void {
  const set = PLUGIN_COMPONENTS.get(id)
  if (!set) return
  for (const type of set) {
    delete COMPONENT_REGISTRY[type]
  }
  PLUGIN_COMPONENTS.delete(id)
  PLUGINS.delete(id)
  PLUGIN_STATUS.set(id, 'invalidated')
}

/**
 * 加载插件：若 `override` 且已存在，则先失效再重新注册；并标记状态为 `loaded`
 * @param plugin 插件描述
 * @param options 可选项：`override` 表示覆盖已存在的插件
 * @returns 成功加载的组件类型数组
 */
export function loadMaterialsPlugin(plugin: MaterialsPlugin, options?: { override?: boolean }): ComponentType[] {
  if (options?.override && PLUGINS.has(plugin.id)) {
    invalidateMaterialsPlugin(plugin.id)
  }
  const types = registerMaterialsPlugin(plugin, { override: options?.override })
  PLUGIN_STATUS.set(plugin.id, 'loaded')
  return types
}

/**
 * 卸载插件：移除其已注册的组件，但保留插件记录；标记状态为 `unloaded`
 * @param id 插件 ID
 * @returns 被卸载的组件类型数组（可能为空）
 */
export function unloadMaterialsPlugin(id: string): ComponentType[] {
  const set = PLUGIN_COMPONENTS.get(id)
  if (!set) {
    PLUGIN_STATUS.set(id, 'unloaded')
    return []
  }
  const types = [...set]
  for (const type of types) {
    delete COMPONENT_REGISTRY[type]
  }
  PLUGIN_STATUS.set(id, 'unloaded')
  return types
}

/**
 * 重载插件：在已有登记的基础上重新加载
 * @param id 插件 ID
 * @param options 可选项：允许覆写
 * @returns 成功加载的组件类型数组（若插件不存在则为空）
 */
export function reloadMaterialsPlugin(id: string, options?: { override?: boolean }): ComponentType[] {
  const plugin = PLUGINS.get(id)
  if (!plugin) return []
  return loadMaterialsPlugin(plugin, options)
}

/**
 * 列举所有已登记插件及其当前状态与组件类型集合
 * @returns 数组：每项包含 `{ id, status, components }`
 */
export function listMaterialsPlugins(): Array<{ id: string; status: 'registered' | 'loaded' | 'unloaded' | 'invalidated'; components: ComponentType[] }> {
  const result: Array<{ id: string; status: 'registered' | 'loaded' | 'unloaded' | 'invalidated'; components: ComponentType[] }> = []
  for (const [id, plugin] of PLUGINS) {
    const status = PLUGIN_STATUS.get(id) ?? 'registered'
    const set = PLUGIN_COMPONENTS.get(id)
    const components = set ? [...set] : plugin.components.map((c) => c.type as ComponentType)
    result.push({ id, status, components })
  }
  return result
}

/**
 * 获取某插件的当前状态
 * @param id 插件 ID
 * @returns 插件状态；若未登记则返回 `undefined`
 */
export function getMaterialsPluginStatus(id: string): 'registered' | 'loaded' | 'unloaded' | 'invalidated' | undefined {
  return PLUGIN_STATUS.get(id)
}
