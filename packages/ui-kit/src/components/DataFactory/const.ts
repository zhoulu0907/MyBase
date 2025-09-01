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
export const ENTITY_FIELD_TYPE_LABEL = {
  TEXT: 'VARCHAR',
  LONG_TEXT: 'TEXT',
  INTEGER: 'INTEGER',
  BIGINT: 'BIGINT',
  DECIMAL: 'DECIMAL',
  EMAIL: 'EMAIL',
  PHONE: 'PHONE',
  URL: 'URL',
  ADDRESS: 'ADDRESS',
  NUMBER: 'NUMBER',
  CURRENCY: 'CURRENCY',
  DATE: 'DATE',
  DATETIME: 'DATETIME',
  TIME: 'TIME',
  BOOLEAN: 'BOOLEAN',
  PICKLIST: 'SINGLE_SELECT',
  MULTI_PICKLIST: 'MULTI_SELECT',
  JSON: 'JSON',
  AUTO_CODE: 'AUTO_NUMBER',
  USER: 'USER',
  DEPARTMENT: 'DEPARTMENT',
  DATA_SELECTION: 'DATA_SELECTION',
  // RELATION: 'RELATION',
  STRUCTURE: 'STRUCTURE',
  ARRAY: 'ARRAY',
  FILE: 'FILE',
  IMAGE: 'IMAGE',
  GEOGRAPHY: 'GEOGRAPHY',
  PASSWORD: 'PASSWORD',
  ENCRYPTED: 'ENCRYPTED',
  AGGREGATE: 'AGGREGATE'
};

// 实体字段类型
export const ENTITY_FIELD_TYPE = {
  VARCHAR: '常规文本',
  TEXT: '长文本内容',
  INTEGER: '整数',
  BIGINT: '长整数',
  DECIMAL: '小数',
  EMAIL: '邮箱地址',
  PHONE: '电话号码',
  URL: '网址链接',
  ADDRESS: '详细地址',
  NUMBER: '通用数字',
  CURRENCY: '货币金额',
  DATE: '日期',
  DATETIME: '日期时间',
  TIME: '时间',
  BOOLEAN: '布尔值',
  SINGLE_SELECT: '单选列表',
  MULTI_SELECT: '多选列表',
  JSON: 'JSON',
  AUTO_CODE: '自动编码',
  USER: '用户引用',
  DEPARTMENT: '部门引用',
  DATA_SELECTION: '数据选择',
  // RELATION: '关联关系',
  STRUCTURE: '结构化对象',
  ARRAY: '数组列表',
  FILE: '文件',
  IMAGE: '图片',
  GEOGRAPHY: '地理位置',
  PASSWORD: '密码',
  ENCRYPTED: '加密字段',
  AGGREGATE: '聚合统计'
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
