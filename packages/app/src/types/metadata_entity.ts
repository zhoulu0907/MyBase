export interface MetadataEntityPair {
  entityId: string;

  entityName: string;
}

export interface MetadataEntityField {
    id: string;
    entityId: string;
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
    isUnique: boolean;
    allowNull: boolean;
    sortOrder: number;
    validationRulesId?: string;
    runMode: number;
    appId: string;
    status: number;
    fieldCode?: string;
}

export interface AppEntities {
  entities: AppEntity[];
}

export interface AppEntity {
    entityID: string; // 实体ID
    entityName: string; // 实体名称
    entityType: string; // 实体类型，如“主表”、“子表”、“独立表”、“主子表”
    fields: AppEntityField[];
}

export interface AppEntityField {
    fieldID: string; // 字段ID
    fieldName: string; // 字段名称
    fieldType: string; // 字段类型，如 BIGINT、VARCHAR、INT、DECIMAL、TIMESTAMP、TEXT、NUMBER 等
    isSystemField: number; // 是否是系统字段
    displayName: string; // 显示名称
}

export interface EntityWithChildren {
    entityId: string;
    entityName: string;
    entityCode: string;
    parentFields: AppEntityField[];
    childEntities: AppEntity[];
}