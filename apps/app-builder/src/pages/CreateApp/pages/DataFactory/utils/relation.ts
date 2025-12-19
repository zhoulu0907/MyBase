// 关联关系类型

export const RELATIONSHIP_TYPE = {
  DEFINE_ONE_TO_ONE: 'DEFINE_ONE_TO_ONE',
  DEFINE_ONE_TO_MANY: 'DEFINE_ONE_TO_MANY',
  DEFINE_MANY_TO_ONE: 'DEFINE_MANY_TO_ONE',
  DEFINE_MANY_TO_MANY: 'DEFINE_MANY_TO_MANY',
  SUBTABLE_ONE_TO_MANY: 'SUBTABLE_ONE_TO_MANY',
  DATA_SELECT: 'DATA_SELECT',
  DATA_SELECT_MULTI: 'DATA_SELECT_MULTI'
};

export type RelationshipType = (typeof RELATIONSHIP_TYPE)[keyof typeof RELATIONSHIP_TYPE];

export const ALL_RELATIONSHIP_TYPE_MAP: Record<RelationshipType, string> = {
  DEFINE_ONE_TO_ONE: '一对一',
  DEFINE_ONE_TO_MANY: '一对多',
  DEFINE_MANY_TO_ONE: '多对一',
  DEFINE_MANY_TO_MANY: '多对多',
  SUBTABLE_ONE_TO_MANY: '主子表',
  DATA_SELECT: '数据选择',
  DATA_SELECT_MULTI: '数据选择多选'
};

export const ALL_RELATIONSHIP_OPTIONS = Object.entries(ALL_RELATIONSHIP_TYPE_MAP).map(([value, label]) => ({
  label,
  value
}));

export const DEFINE_RELATIONSHIP_OPTIONS = Object.entries(ALL_RELATIONSHIP_TYPE_MAP)
  .map(([value, label]) => ({
    label,
    value
  }))
  .filter((item) => item.value.includes('DEFINE'));

export const RELATIONSHIP_TYPE_LABEL_MAP: Partial<Record<RelationshipType, string>> = {
  DEFINE_ONE_TO_ONE: '1:1',
  DEFINE_ONE_TO_MANY: '1:N',
  DEFINE_MANY_TO_ONE: 'N:1',
  DEFINE_MANY_TO_MANY: 'M:N',
  SUBTABLE_ONE_TO_MANY: '主子',
  DATA_SELECT: '数据选择',
  DATA_SELECT_MULTI: '数据选择多选'
};
