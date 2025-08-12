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
    isSystemField: boolean;
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