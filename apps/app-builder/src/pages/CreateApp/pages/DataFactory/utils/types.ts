// 关联关系类型
export type RelationshipType =
  | 'DEFINE_ONE_TO_ONE'
  | 'DEFINE_ONE_TO_MANY'
  | 'DEFINE_MANY_TO_ONE'
  | 'DEFINE_MANY_TO_MANY';

export const RELATIONSHIP_TYPE_MAP: Record<RelationshipType, string> = {
  DEFINE_ONE_TO_ONE: '一对一',
  DEFINE_ONE_TO_MANY: '一对多',
  DEFINE_MANY_TO_ONE: '多对一',
  DEFINE_MANY_TO_MANY: '多对多'
};

export const RELATIONSHIP_OPTIONS = Object.entries(RELATIONSHIP_TYPE_MAP).map(([value, label]) => ({
  label,
  value
}));

export const RELATIONSHIP_TYPE_LABEL_MAP: Record<RelationshipType, string> = {
  DEFINE_ONE_TO_ONE: '1:1',
  DEFINE_ONE_TO_MANY: '1:N',
  DEFINE_MANY_TO_ONE: 'N:1',
  DEFINE_MANY_TO_MANY: 'M:N'
};
