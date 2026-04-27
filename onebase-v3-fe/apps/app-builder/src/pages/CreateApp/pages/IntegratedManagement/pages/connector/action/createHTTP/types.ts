// ========== OpenAPI 兼容的数据结构 ==========

/** OpenAPI 参数位置 */
export type ParameterIn = 'header' | 'query' | 'path';

/** OpenAPI 参数对象 */
export interface OpenApiParameter {
  name: string;
  in: ParameterIn;
  required?: boolean;
  description?: string;
  schema?: {
    type: string;
    default?: string;
    example?: string;
  };
}

/** OpenAPI 请求体 */
export interface OpenApiRequestBody {
  description?: string;
  required?: boolean;
  content: {
    'application/json'?: {
      schema?: object;
      example?: object;
    };
    'multipart/form-data'?: {
      schema?: object;
    };
  };
}

/** OpenAPI 响应 */
export interface OpenApiResponse {
  description?: string;
  content?: {
    'application/json'?: {
      schema?: object;
      example?: object;
    };
  };
}

/** 动作入参映射 */
export interface ActionInputMapping {
  in: 'header' | 'query' | 'path' | 'body';
  name: string;
}

/** 动作出参映射 */
export interface ActionOutputMapping {
  in: 'header' | 'body';
  path: string;  // JSON Path，如 "$.data.id"
}

/** 成功条件 */
export interface SuccessCondition {
  conditions: Array<{
    field: string;        // 如 "$.success" 或 "statusCode"
    operator: string;     // eq, ne, contains 等
    value: string;        // 期望值
  }>;
  errorMessagePath?: string;  // 错误消息路径，如 "$.message"
}

/** OneBase 扩展字段 */
export interface OneBaseExtension {
  /** 动作名称 */
  actionName: string;
  /** 动作描述 */
  actionDescription?: string;
  /** 成功条件 */
  successCondition?: SuccessCondition;
  /** 动作入参（对外暴露的参数） */
  inputs?: Array<{
    key: string;
    fieldName: string;
    fieldType: string;
    required: boolean;
    defaultValue?: string;
    description?: string;
    /** 映射关系 */
    map: ActionInputMapping;
  }>;
  /** 动作出参（对外暴露的响应字段） */
  outputs?: Array<{
    key: string;
    fieldName: string;
    fieldType: string;
    description?: string;
    /** 映射关系 */
    map: ActionOutputMapping;
  }>;
  /** 调试配置 */
  debug?: {
    /** 调试时的参数值 */
    paramValues?: Record<string, string>;
  };
}

/** HTTP 动作配置 - 兼容 OpenAPI 格式 */
export interface HttpActionConfig {
  // ========== OpenAPI 标准字段 ==========
  /** 请求路径（相对于服务器地址） */
  path: string;

  /** HTTP 方法 */
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

  /** 操作摘要 */
  summary?: string;

  /** 操作ID（可作为动作名） */
  operationId?: string;

  /** 描述 */
  description?: string;

  /** 标签 */
  tags?: string[];

  /** 请求参数（包含 header, query, path） */
  parameters?: OpenApiParameter[];

  /** 请求体 */
  requestBody?: OpenApiRequestBody;

  /** 响应定义 */
  responses?: {
    [statusCode: string]: OpenApiResponse;
  };

  // ========== OneBase 扩展字段 ==========
  'x-onebase'?: OneBaseExtension;
}

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
