export const ENTITY_TYPE = {
  MAIN: '主表',
  SUB: '子表',
  INDEP: '独立表'
};

export const ENTITY_TYPE_VALUE = {
  MAIN: 'main',
  SUB: 'sub',
  INDEP: 'indep'
};

export const RELATION_TYPE = {
  MASTER: 'MASTER',
  SLAVE: 'SLAVE',
  NONE: 'NONE'
};

export interface MetadataEntityPair {
  entityId: string;
  entityUuid: string;
  tableName: string;
  entityName: string;
  relationType?: (typeof RELATION_TYPE)[keyof typeof RELATION_TYPE];
  relationshipTypes?: string[];
}

export interface MetadataEntityField {
  id: string;
  entityId: string;
  entityUuid: string;
  fieldName: string;
  displayName: string;
  fieldType: string;
  dataLength: number;
  decimalPlaces?: number;
  defaultValue: any;
  description: string;
  isSystemField: number;
  isPrimaryKey: boolean;
  isRequired: boolean;
  isUnique: number;
  allowNull: boolean;
  sortOrder: number;
  validationRulesId?: string;
  runMode: number;
  appId: string;
  status: number;
  fieldCode?: string;
  disabled?: boolean;
  options?: any[];
  constraints?: any;
  autoNumberConfig?: any;
  dataSelectionConfig?: any;
}

export interface AppEntities {
  entities: AppEntity[];
}

export interface AppEntity {
  entityId: string; // 实体ID
  entityUuid: string; // 实体UUID
  tableName: string; // 表名
  entityName: string; // 实体名称
  entityType: string; // 实体类型，如“主表”、“子表”、“独立表”、“主子表”
  fields: AppEntityField[];
}

export interface ChildEntity {
  childEntityCode: string;
  childEntityId: string;
  childEntityUuid: string;
  childEntityName: string;
  childFields: AppEntityField[];
  childTableName: string;
  relationshipId: string;
  relationshipType: string;
  sourceFieldName: string;
  targetFieldName: string;
  dictTypeId?: string;
}

export interface AppEntityField extends MetadataEntityField {
  id: string;
  fieldId: string; // 字段ID
  fieldName: string; // 字段名称
  fieldType: string; // 字段类型，如 BIGINT、VARCHAR、INT、DECIMAL、TIMESTAMP、TEXT、NUMBER 等
  isSystemField: number; // 是否是系统字段
  displayName: string; // 显示名称
  dictTypeId?: string;

  fieldKey?: string; // 字段键，如 tableName.fieldName
}

export interface EntityWithChildren {
  entityId: string;
  entityName: string;
  entityCode: string;
  parentFields: AppEntityField[];
  childEntities: AppEntity[];
}

// 实体相关接口类型定义
export interface CreateEntityReqVO {
  code: string;
  displayName: string;
  description: string;
}

export interface UpdateEntityReqVO extends CreateEntityReqVO {
  id: string;
}

export interface GetEntityPageParams {
  pageNo: number;
  pageSize: number;
  datasourceId: string;
  code?: string;
  title?: string;
}

export interface CreateFieldReqVO {
  appId: string;
  entityId: string;
  fieldCode?: string;
  fieldName: string;
  description: string;
  fieldType: string;
  isSystemField?: number;
  displayName: string;
}

export interface UpdateFieldReqVO extends CreateFieldReqVO {
  id: string;
}

export interface CreateMasterChildReqVO {
  parentEntityId: string;
  parentFieldId: string;
  childEntityId: string;
  childFieldId: string;
  childTableCode: string;
  childTableName: string;
  childTableDescription: string;
  appId: string;
  datasourceId: string;
}

export interface CreateRelationReqVO {
  sourceEntityId: string;
  sourceFieldId: string;
  targetEntityId: string;
  targetFieldId: string;
  relationshipType?: string;
  relationName?: string;
}

export interface UpdateRelationReqVO extends CreateRelationReqVO {
  id: string;
}

// 数据规则相关接口
export interface ConditionRow {
  fieldId: string;
  operator: string;
  valueType: string;
  fieldValue: string;
  logicOperator: 'AND' | 'OR';
  logicType: 'CONDITION';
}

export interface CreateRuleReqVO {
  entityId: string;
  rgName: string;
  rgDesc?: string;
  validationType?: string;
  formatValidationType?: string;
  popPrompt?: string;
  popType?: string;
  valueRules?: ConditionRow[][];
}

export interface UpdateRuleReqVO extends CreateRuleReqVO {
  id: string;
}

export const FilterEntityFields = ['lock_version', 'deleted', 'parent_id'];

// 字段验证类型(大于、小于、等于、包含 ...)
export interface EntityFieldValidationTypes {
  fieldId: string;
  //   TODO(mickey): 需要卞老师补充字段名称
  fieldName: string;
  fieldKey: string;
  fieldTypeCode: string;
  validationTypes: ValidationTypeItem[];
}

export interface ValidationTypeItem {
  code: string;
  name: string;
  description: string;
  sortOrder: number;
}

export interface EntityFieldOption {
  id: string;
  optionUuid: string;
  fieldId: string;
  optionValue: string;
  optionLabel: string;
  optionOrder: number;
  isEnable: number;
}
