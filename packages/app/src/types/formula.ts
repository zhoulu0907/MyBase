/**
 * 计算公式接口配置
 */
export interface formulaParams {
    formula: string;
    parameters: {
        [key: string]: string | number;
    }
}

/**变量列表的字段配置 */
export interface VariablesList {
  variableId: string; // 实体ID
  variableName: string; // 实体名称
  tableName: string;
  fields?: ChildVariablesField[];
}

export interface ChildVariablesField {
  appId: string;
  description: string;
  displayName: string;
  entityId: string;
  fieldType: string;
  fieldCode: string;
  fieldName: string;
  id: string;
  isNode: boolean;
  isUnique: number;
  isRequired: number;
  isSystemField: number;
  sortOrder?: number;
  status?: boolean;
  runMode?: boolean;
  value?: string;
  constraints?: {
    lengthEnabled: number;
    minLength: number;
    maxLength: number;
    lengthPrompt: string;
    regexEnabled: number;
    regexPattern: string;
    regexPrompt: string;
  };
}

export interface variableItem{
  fieldName: string, 
  fieldId: string, 
  fieldType: string
}

export interface fieldListWithNodeData {
  [key: string] : {
    fieldList: variableItem[]
  }
}