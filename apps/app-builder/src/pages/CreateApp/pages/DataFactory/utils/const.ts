export const DS_RESOURCE_TYPE = {
  INTERNAL: 'internal',
  EXTERNAL: 'external',
  EXTERNAL_WITH_INTERNAL: 'external_with_internal'
};

export const DS_RESOURCE_TYPE_LABEL = {
  [DS_RESOURCE_TYPE.INTERNAL]: '内部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL]: '外部数据源',
  [DS_RESOURCE_TYPE.EXTERNAL_WITH_INTERNAL]: '外部数据源中引用自有数据源已有资产'
};

export const ENTITY_FIELD_TYPE = {
  TEXT: '常规文本',
  LONG_TEXT: '长文本内容',
  EMAIL: '邮箱地址',
  PHONE: '电话号码',
  URL: '网址链接',
  ADDRESS: '详细地址',
  NUMBER: '通用数字',
  CURRENCY: '货币金额',
  DATE: '日期',
  DATETIME: '日期时间',
  BOOLEAN: '布尔值',
  PICKLIST: '单选列表',
  MULTI_PICKLIST: '多选列表',
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
