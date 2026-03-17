export interface SuccessConditionRow {
  id?: string;
  fieldType: 'statusCode' | 'responseHeader' | 'responseBody';
  fieldName: string;
  dataType: 'string' | 'number' | 'boolean';
  operator: 'eq' | 'ne' | 'in' | 'notIn' | 'gt' | 'lt' | 'gte' | 'lte';
  valueSource: 'custom';
  value: string;
}

export interface ExposedField {
  key: string;
  fieldName: string;
  fieldType: string;
  description: string;
  mapKind: string;
  mapKey: string;
  required: boolean;
  defaultValue: string;
}

export interface CreateHTTPActionPageProps {
  editActionName?: string;
  onSuccess?: () => void;
  defaultOpenApiModal?: boolean;
  openApiImport?: { token: number; raw: string; opKey?: string };
}
