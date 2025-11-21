// 数据源类型
export const DS_RESOURCE_TYPE = {
  INTERNAL: 'internal',
  EXTERNAL: 'external',
  EXTERNAL_WITH_INTERNAL: 'external_with_internal'
};

// 数据源类型标签
export const DS_RESOURCE_TYPE_LABEL = {
  [DS_RESOURCE_TYPE.INTERNAL]: '内部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL]: '外部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL_WITH_INTERNAL]: '外部数据源中引用自有数据源已有资产'
};

// 实体字段类型标签
export const ENTITY_FIELD_TYPE = {
  TEXT: { VALUE: 'TEXT', LABEL: '常规文本', COLOR: '#1979FF' },
  LONG_TEXT: { VALUE: 'LONG_TEXT', LABEL: '长文本内容', COLOR: '#1979FF' },
  EMAIL: { VALUE: 'EMAIL', LABEL: '邮箱地址', COLOR: '#F53F3F' },
  PHONE: { VALUE: 'PHONE', LABEL: '电话号码', COLOR: '#1979FF' },
  URL: { VALUE: 'URL', LABEL: '网址链接', COLOR: '#24B28F' },
  ADDRESS: { VALUE: 'ADDRESS', LABEL: '详细地址', COLOR: '#7E5AEA' },
  NUMBER: { VALUE: 'NUMBER', LABEL: '通用数字', COLOR: '#24B28F' },
  DATE: { VALUE: 'DATE', LABEL: '日期', COLOR: '#7E5AEA' },
  DATETIME: { VALUE: 'DATETIME', LABEL: '日期时间', COLOR: '#EBBC00' },
  TIME: { VALUE: 'TIME', LABEL: '时间', COLOR: '#EBBC00' },
  BOOLEAN: { VALUE: 'BOOLEAN', LABEL: '布尔值', COLOR: '#EB693A' },
  SELECT: { VALUE: 'SELECT', LABEL: '单选列表', COLOR: '#008699' },
  MULTI_SELECT: { VALUE: 'MULTI_SELECT', LABEL: '多选列表', COLOR: '#1979FF' },
  AUTO_CODE: { VALUE: 'AUTO_CODE', LABEL: '自动编码', COLOR: '#008699' },
  USER: { VALUE: 'USER', LABEL: '用户引用', COLOR: '#24B28F' },
  DEPARTMENT: { VALUE: 'DEPARTMENT', LABEL: '部门引用', COLOR: '#7E5AEA' },
  DATA_SELECTION: { VALUE: 'DATA_SELECTION', LABEL: '数据选择', COLOR: '#EBBC00' },
  RELATION: { VALUE: 'RELATION', LABEL: '关联关系', COLOR: '#008699' },
  STRUCTURE: { VALUE: 'STRUCTURE', LABEL: '结构化对象', COLOR: '#F53F3F' },
  ARRAY: { VALUE: 'ARRAY', LABEL: '数组列表', COLOR: '#F53F3F' },
  FILE: { VALUE: 'FILE', LABEL: '文件', COLOR: '#EB693A' },
  IMAGE: { VALUE: 'IMAGE', LABEL: '图片', COLOR: '#EBBC00' },
  GEOGRAPHY: { VALUE: 'GEOGRAPHY', LABEL: '地理位置', COLOR: '#1979FF' },
  PASSWORD: { VALUE: 'PASSWORD', LABEL: '密码', COLOR: '#24B28F' },
  ENCRYPTED: { VALUE: 'ENCRYPTED', LABEL: '加密字段', COLOR: '#7E5AEA' },
  AGGREGATE: { VALUE: 'AGGREGATE', LABEL: '聚合统计', COLOR: '#EB693A' },
  ID: { VALUE: 'ID', LABEL: 'ID', COLOR: '#1979FF' },
  MULTI_USER: { VALUE: 'MULTI_USER', LABEL: '用户多选', COLOR: '#24B28F' },
  MULTI_DEPARTMENT: { VALUE: 'MULTI_DEPARTMENT', LABEL: '部门多选', COLOR: '#7E5AEA' },
  MULTI_DATA_SELECTION: { VALUE: 'MULTI_DATA_SELECTION', LABEL: '数据多选', COLOR: '#EBBC00' },
  RADIO: { VALUE: 'RADIO', LABEL: '单选框', COLOR: '#008699' },
  CHECKBOX: { VALUE: 'CHECKBOX', LABEL: '复选框', COLOR: '#1979FF' },
};

// 系统字段对应
export const SYSTEM_FIELD_MAP = {
  id: '数据ID',
  owner_id: '所属人',
  owner_dept: '所属人部门',
  creator: '创建人',
  created_time: '创建时间',
  updated_time: '更新时间',
  updater: '更新人',
  update_by: '更新人',
  parent_id: '主表ID',
  deleted: '软删除标记',
  lock_version: '乐观锁版本'
};

// 字段类型常量
export const FIELD_TYPE = {
  /** 自定义字段 */
  CUSTOM: 0,
  /** 系统字段 */
  SYSTEM: 1
} as const;

export type FieldType = (typeof FIELD_TYPE)[keyof typeof FIELD_TYPE];

// 字段类型标签
export const FIELD_TYPE_LABEL = {
  [FIELD_TYPE.CUSTOM]: '自定义字段',
  [FIELD_TYPE.SYSTEM]: '系统字段'
} as const;

// 检查字段是否为系统字段
export const isSystemField = (fieldType: number): boolean => {
  return fieldType === FIELD_TYPE.SYSTEM;
};

// 检查字段是否为自定义字段
export const isCustomField = (fieldType: number): boolean => {
  return fieldType === FIELD_TYPE.CUSTOM;
};

// 实体是否开启 0禁用 1开启
export const ENTITY_STATUS = {
  DISABLE: 0,
  ENABLE: 1
} as const;

export type EntityStatus = (typeof ENTITY_STATUS)[keyof typeof ENTITY_STATUS];

export type FieldConstraintLengthEnabled =
  (typeof FIELD_CONSTRAINT_LENGTH_ENABLED)[keyof typeof FIELD_CONSTRAINT_LENGTH_ENABLED];

// 字段约束 长度 0禁用 1开启
export const FIELD_CONSTRAINT_LENGTH_ENABLED = {
  DISABLE: 0,
  ENABLE: 1
} as const;

// 字段约束 正则 0禁用 1开启
export const FIELD_CONSTRAINT_REGEX_ENABLED = {
  DISABLE: 0,
  ENABLE: 1
} as const;

// 字段约束 长度 0禁用 1开启
export const FIELD_CONSTRAINT_LENGTH_PROMPT = {
  DISABLE: 0,
  ENABLE: 1
} as const;

export type FieldConstraintRegexEnabled =
  (typeof FIELD_CONSTRAINT_REGEX_ENABLED)[keyof typeof FIELD_CONSTRAINT_REGEX_ENABLED];
