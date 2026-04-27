import { isRecord, upsertHeaderRow } from '../openapi';
import type { HttpActionConfig, OpenApiParameter, OneBaseExtension } from './types';
import type { ExposedField } from './types';
import {
  buildJsonBodyRows,
  buildResponseBodyFromJson,
  extractVariables,
  getTabArray,
  getTabString,
  normalizeMethodForRequest,
  replaceVariables
} from './utils';

const isObject = (v: unknown): v is Record<string, unknown> =>
  typeof v === 'object' && v !== null && !Array.isArray(v);

export const scanExposedFields = (values: Record<string, unknown>): ExposedField[] => {
  const fields: ExposedField[] = [];
  const addedKeys = new Set<string>();

  const addFieldFromVariable = (row: Record<string, unknown>, mapKind: string) => {
    if (!isRecord(row)) return;

    const defaultValue = typeof row.defaultValue === 'string' ? row.defaultValue : '';
    // 从 defaultValue 中提取 ${xxx} 变量
    const vars = extractVariables(defaultValue);
    if (vars.length === 0) return;

    vars.forEach((v) => {
      if (addedKeys.has(v)) return;
      addedKeys.add(v);

      const key = typeof row.key === 'string' ? row.key : '';
      const fieldName = typeof row.fieldName === 'string' ? row.fieldName : key;
      const fieldType = typeof row.fieldType === 'string' ? row.fieldType : 'string';
      const description = typeof row.description === 'string' ? row.description : '';
      const required = row.required === true;

      fields.push({
        key: v,                          // 变量名作为入参 key
        fieldName: fieldName || v,       // 显示名称
        fieldType,
        description,
        mapKind,
        mapKey: key,                     // 映射到底层的 key
        required,
        defaultValue
      });
    });
  };

  // 扫描各区域的变量
  const headers = getTabArray(values, 'requestHeaders');
  headers.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'header'));

  const queryParams = getTabArray(values, 'queryParams');
  queryParams.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'query'));

  const pathParams = getTabArray(values, 'pathParams');
  pathParams.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'path'));

  const bodyMode = getTabString(values, 'bodyMode');
  if (bodyMode === 'kv') {
    const bodyRows = getTabArray(values, 'requestBody');
    bodyRows.forEach((row) => addFieldFromVariable(row as Record<string, unknown>, 'body'));
  } else if (bodyMode === 'json') {
    // JSON body 中也可能有 ${xxx} 变量
    const jsonBody = getTabString(values, 'requestBodyJson');
    if (jsonBody) {
      const vars = extractVariables(jsonBody);
      vars.forEach((v) => {
        if (addedKeys.has(v)) return;
        addedKeys.add(v);
        fields.push({
          key: v,
          fieldName: v,
          fieldType: 'string',
          description: '',
          mapKind: 'body',
          mapKey: v,
          required: false,
          defaultValue: `\${${v}}`
        });
      });
    }
  }

  return fields;
};

export const scanExposedOutputFields = (values: Record<string, unknown>): ExposedField[] => {
  // 输出参数不自动扫描，用户需要在 Step3 手动配置
  return [];
};

/** 从响应 Schema 或 JSON 生成出参字段 */
export const generateOutputsFromValues = (values: Record<string, unknown>): ExposedField[] => {
  const fields: ExposedField[] = [];
  const addedKeys = new Set<string>();

  // 从 responseTabs.responseBodyTab 获取响应数据
  const responseTabs = isRecord(values.responseTabs) ? (values.responseTabs as Record<string, unknown>) : {};
  const responseBodyTab = isRecord(responseTabs.responseBodyTab) ? (responseTabs.responseBodyTab as Record<string, unknown>) : {};
  const responseBodyMode = typeof responseBodyTab.responseBodyMode === 'string' ? responseBodyTab.responseBodyMode : 'json';

  // 优先使用 responseSchema 提取字段
  const responseSchema = responseBodyTab.responseSchema;

  /** 从 Schema 结构提取字段 */
  const extractFieldsFromSchema = (schema: unknown, prefix = '$'): void => {
    if (!isRecord(schema)) return;

    // 处理数组类型
    if (schema.type === 'array' && isRecord(schema.items)) {
      extractFieldsFromSchema(schema.items, prefix);
      return;
    }

    // 处理对象类型
    if (schema.type === 'object' && isRecord(schema.properties)) {
      const properties = schema.properties as Record<string, unknown>;
      const requiredList = Array.isArray(schema.required) ? schema.required : [];

      for (const [key, propSchema] of Object.entries(properties)) {
        if (!isRecord(propSchema)) continue;

        const path = prefix === '$' ? `$.${key}` : `${prefix}.${key}`;
        const propType = typeof propSchema.type === 'string' ? propSchema.type : 'string';
        const fieldType = propType === 'integer' || propType === 'number' ? 'number' :
                          propType === 'boolean' ? 'boolean' :
                          propType === 'array' ? 'array' :
                          propType === 'object' ? 'object' : 'string';
        const description = typeof propSchema.description === 'string' ? propSchema.description : '';
        const fieldName = typeof propSchema.title === 'string' ? propSchema.title : key;

        if (!addedKeys.has(key)) {
          addedKeys.add(key);
          fields.push({
            key,
            fieldName,
            fieldType,
            description,
            mapKind: 'body',
            mapKey: path,
            required: requiredList.includes(key),
            defaultValue: ''
          });
        }

        // 递归处理嵌套对象
        if (fieldType === 'object' && isRecord(propSchema.properties)) {
          extractFieldsFromSchema(propSchema, path);
        } else if (fieldType === 'array' && isRecord(propSchema.items)) {
          // 数组内部可能也是对象
          if (isRecord(propSchema.items) && propSchema.items.type === 'object') {
            extractFieldsFromSchema(propSchema.items, path);
          }
        }
      }
    }
  };

  // 如果有 responseSchema，优先从中提取字段
  if (isRecord(responseSchema) && responseSchema.type) {
    extractFieldsFromSchema(responseSchema);
  }

  // 如果 schema 没有提取到字段，尝试从 responseBodyJson 解析
  if (fields.length === 0 && responseBodyMode === 'json') {
    const responseBodyJsonWrapper = isRecord(responseBodyTab.responseBodyJsonWrapper) ? (responseBodyTab.responseBodyJsonWrapper as Record<string, unknown>) : {};
    const responseBodyJson = typeof responseBodyJsonWrapper.responseBodyJson === 'string' ? responseBodyJsonWrapper.responseBodyJson : '';

    if (responseBodyJson) {
      try {
        const parsed = JSON.parse(responseBodyJson);

        // 扁平化对象
        const flattenObject = (obj: unknown, prefix = ''): { key: string; path: string; type: string }[] => {
          const result: { key: string; path: string; type: string }[] = [];
          if (!isRecord(obj) && !Array.isArray(obj)) return result;

          if (Array.isArray(obj)) {
            // 对于数组，取第一个元素作为模板
            const firstItem = obj[0];
            if (isRecord(firstItem)) {
              result.push(...flattenObject(firstItem, prefix));
            }
          } else if (isRecord(obj)) {
            for (const [k, v] of Object.entries(obj)) {
              const path = prefix ? `${prefix}.${k}` : k;
              const type = Array.isArray(v) ? 'array' : typeof v === 'object' && v !== null ? 'object' : typeof v === 'number' ? 'number' : typeof v === 'boolean' ? 'boolean' : 'string';
              result.push({ key: k, path: `$.${path}`, type });
              // 如果是对象或数组，递归处理
              if (isRecord(v) || Array.isArray(v)) {
                result.push(...flattenObject(v, path));
              }
            }
          }
          return result;
        };

        const flattened = flattenObject(parsed);
        flattened.forEach((item) => {
          if (addedKeys.has(item.key)) return;
          addedKeys.add(item.key);
          fields.push({
            key: item.key,
            fieldName: item.key,
            fieldType: item.type,
            description: '',
            mapKind: 'body',
            mapKey: item.path,
            required: false,
            defaultValue: ''
          });
        });
      } catch {
        // JSON 解析失败，忽略
      }
    }
  }

  // 从响应头获取
  const responseHeaders = Array.isArray(responseTabs.responseHeaders) ? responseTabs.responseHeaders : [];
  responseHeaders.forEach((row) => {
    if (!isRecord(row)) return;
    const key = typeof row.key === 'string' ? row.key : '';
    if (!key || addedKeys.has(key)) return;
    addedKeys.add(key);
    fields.push({
      key,
      fieldName: typeof row.fieldName === 'string' ? row.fieldName : key,
      fieldType: typeof row.fieldType === 'string' ? row.fieldType : 'string',
      description: typeof row.description === 'string' ? row.description : '',
      mapKind: 'header',
      mapKey: key,
      required: false,
      defaultValue: ''
    });
  });

  return fields;
};

export function actionConfigToFormValues(config: unknown): Record<string, unknown> {
  const cfg = isRecord(config) ? config : {};
  const basic = (isRecord(cfg.basicInfo) ? cfg.basicInfo : {}) as Record<string, unknown>;
  const request = (isRecord(cfg.inputConfig) ? cfg.inputConfig : {}) as Record<string, unknown>;
  const response = (isRecord(cfg.outputConfig) ? cfg.outputConfig : {}) as Record<string, unknown>;
  const debug = (isRecord(cfg.debugConfig) ? cfg.debugConfig : {}) as Record<string, unknown>;
  const io = (isRecord(cfg.ioConfig) ? cfg.ioConfig : {}) as Record<string, unknown>;

  const requestHeaders = (request.requestHeaders ?? []) as unknown[];
  const requestBody = (request.requestBody ?? []) as unknown[];
  const queryParams = (request.queryParams ?? []) as unknown[];
  const pathParams = (request.pathParams ?? []) as unknown[];

  let bodyMode = requestBody.length > 0 ? 'kv' : 'none';
  let requestBodyJson = '';
  let requestBodyRows = requestBody;
  const bodyRow = requestBody.find((row) => {
    if (!isRecord(row)) return false;
    const k = typeof row.key === 'string' ? row.key : '';
    const fn = typeof row.fieldName === 'string' ? row.fieldName : '';
    return k === 'body' || fn === 'body';
  });
  if (isRecord(bodyRow) && (bodyRow.fieldType === 'object' || bodyRow.fieldType === 'array')) {
    const dv = typeof bodyRow.defaultValue === 'string' ? bodyRow.defaultValue : '';
    if (dv) {
      try {
        JSON.parse(dv);
        bodyMode = 'json';
        requestBodyJson = dv;
        requestBodyRows = [];
      } catch {
        bodyMode = 'kv';
      }
    }
  }

  const responseHeaders = Array.isArray(response.responseHeaders) ? response.responseHeaders : [];
  const responseBody = Array.isArray(response.responseBody) ? response.responseBody : [];
  const responseSchema = isRecord(response.responseSchema) ? response.responseSchema : {};

  const oldTabs = isRecord(cfg.tabs) ? (cfg.tabs as Record<string, unknown>) : {};
  const oldResponseHeaders = Array.isArray(oldTabs.responseHeaders) ? oldTabs.responseHeaders : [];
  const oldResponseBody = Array.isArray(oldTabs.responseBody) ? oldTabs.responseBody : [];

  const finalResponseHeaders = responseHeaders.length > 0 ? responseHeaders : oldResponseHeaders;
  const finalResponseBody = responseBody.length > 0 ? responseBody : oldResponseBody;

  let responseBodyMode = 'json';
  let responseBodyJson = '';
  let responseBodyText = '';
  if (finalResponseBody.length > 0) {
    const bodyRow = finalResponseBody.find((row) => {
      if (!isRecord(row)) return false;
      const k = typeof row.key === 'string' ? row.key : '';
      const fn = typeof row.fieldName === 'string' ? row.fieldName : '';
      return k === 'body' || fn === 'body';
    });
    if (isRecord(bodyRow)) {
      const dv = typeof bodyRow.defaultValue === 'string' ? bodyRow.defaultValue : '';
      if (dv) {
        try {
          JSON.parse(dv);
          responseBodyMode = 'json';
          responseBodyJson = dv;
        } catch {
          responseBodyMode = 'text';
          responseBodyText = dv;
        }
      }
    }
  }

  return {
    basic,
    tabs: {
      params: {
        queryParams,
        pathParams
      },
      headers: {
        requestHeaders
      },
      body: {
        bodyMode,
        requestBody: requestBodyRows,
        jsonBody: {
          requestBodyJson
        }
      }
    },
    successCondition: {
      successConditions: Array.isArray(cfg.successConditions) ? cfg.successConditions : [],
      errorMessagePath: typeof cfg.errorMessagePath === 'string' ? cfg.errorMessagePath : ''
    },
    responseTabs: {
      responseHeaders: finalResponseHeaders,
      responseBodyTab: {
        responseBodyMode,
        responseBodyJsonWrapper: {
          responseBodyJson
        },
        responseBodyTextWrapper: {
          responseBodyText
        },
        responseSchema
      },
      responseBody: finalResponseBody
    },
    inputs: Array.isArray(io.inputs) ? io.inputs : [],
    outputs: Array.isArray(io.outputs) ? io.outputs : [],
    url: debug.url ?? '',
    method: debug.method ?? 'GET',
    baseUrl: '',
    authType: ''
  };
}

export const buildActionConfig = (values: Record<string, unknown>) => {
  const methodInput = values.method ?? 'GET';
  const { actualMethod } = normalizeMethodForRequest(methodInput);

  const bodyMode =
    getTabString(values, 'bodyMode') || (getTabArray(values, 'requestBody').length > 0 ? 'kv' : 'none');
  const requestBody =
    bodyMode === 'json'
      ? buildJsonBodyRows(getTabString(values, 'requestBodyJson'))
      : bodyMode === 'none'
        ? []
        : getTabArray(values, 'requestBody');
  const requestHeaders = getTabArray(values, 'requestHeaders');
  const queryParams = getTabArray(values, 'queryParams');
  const pathParams = getTabArray(values, 'pathParams');

  const responseTabs = isRecord(values.responseTabs) ? (values.responseTabs as Record<string, unknown>) : {};
  const responseHeaders = Array.isArray(responseTabs.responseHeaders) ? responseTabs.responseHeaders : [];
  const responseBodyTab = isRecord(responseTabs.responseBodyTab) ? (responseTabs.responseBodyTab as Record<string, unknown>) : {};
  const responseBodyMode = typeof responseBodyTab.responseBodyMode === 'string' ? responseBodyTab.responseBodyMode : 'json';

  // 读取 responseBodyJson（可能在 wrapper 内或直接在 responseBodyTab）
  const responseBodyJsonWrapper = isRecord(responseBodyTab.responseBodyJsonWrapper) ? (responseBodyTab.responseBodyJsonWrapper as Record<string, unknown>) : {};
  const responseBodyJson = typeof responseBodyJsonWrapper.responseBodyJson === 'string' ? responseBodyJsonWrapper.responseBodyJson :
                            typeof responseBodyTab.responseBodyJson === 'string' ? responseBodyTab.responseBodyJson : '';

  // 读取 responseBodyText（可能在 wrapper 内或直接在 responseBodyTab）
  const responseBodyTextWrapper = isRecord(responseBodyTab.responseBodyTextWrapper) ? (responseBodyTab.responseBodyTextWrapper as Record<string, unknown>) : {};
  const responseBodyText = typeof responseBodyTextWrapper.responseBodyText === 'string' ? responseBodyTextWrapper.responseBodyText :
                            typeof responseBodyTab.responseBodyText === 'string' ? responseBodyTab.responseBodyText : '';

  // 读取 responseSchema
  const responseSchema = isRecord(responseBodyTab.responseSchema) ? responseBodyTab.responseSchema : {};

  const responseBody =
    responseBodyMode === 'json' && responseBodyJson
      ? buildResponseBodyFromJson(responseBodyJson)
      : responseBodyMode === 'text' && responseBodyText
        ? [{ key: 'body', fieldName: 'body', fieldType: 'string', defaultValue: responseBodyText, description: '' }]
        : Array.isArray(responseTabs.responseBody)
          ? responseTabs.responseBody
          : [];

  const ioInputs = Array.isArray(values.inputs) ? (values.inputs as unknown[]) : [];
  const ioOutputs = Array.isArray(values.outputs) ? (values.outputs as unknown[]) : [];
  const debugInputs = Array.isArray(values.debugInputs) ? (values.debugInputs as unknown[]) : [];

  const normalizeDebugRows = (arr: unknown[]) =>
    Array.isArray(arr)
      ? arr.map((row) =>
          typeof row === 'object' && row !== null
            ? {
                ...row,
                fieldValue:
                  (row as Record<string, unknown>).fieldValue ?? (row as Record<string, unknown>).defaultValue ?? ''
              }
            : { fieldValue: '' }
        )
      : [];

  const debugRequestHeaders = normalizeDebugRows(requestHeaders);
  const debugQueryParams = normalizeDebugRows(queryParams);
  const debugPathParams = normalizeDebugRows(pathParams);
  const debugRequestBody = normalizeDebugRows(requestBody);

  const debugInputValueMap = new Map<string, string>();
  (Array.isArray(debugInputs) ? debugInputs : []).forEach((row) => {
    if (!isRecord(row)) return;
    const k = typeof row.key === 'string' ? row.key : '';
    const v = typeof row.fieldValue === 'string' ? row.fieldValue : typeof row.defaultValue === 'string' ? row.defaultValue : '';
    if (k) debugInputValueMap.set(k, v);
  });

  const applyMappedValue = (arr: any[], mapKey: string, v: string) => {
    const idx = arr.findIndex((r) => isRecord(r) && ((r.key as string) === mapKey || (r.fieldName as string) === mapKey));
    if (idx >= 0) {
      arr[idx] = { ...arr[idx], fieldValue: v };
    }
  };

  const applyVariableReplacement = (arr: any[]) => {
    arr.forEach((row) => {
      if (!isRecord(row)) return;
      const fieldValue = typeof row.fieldValue === 'string' ? row.fieldValue : '';
      const defaultValue = typeof row.defaultValue === 'string' ? row.defaultValue : '';
      if (fieldValue && fieldValue.includes('${')) {
        row.fieldValue = replaceVariables(fieldValue, debugInputValueMap);
      }
      if (defaultValue && defaultValue.includes('${')) {
        row.defaultValue = replaceVariables(defaultValue, debugInputValueMap);
      }
    });
  };

  const mappingInputs = ioInputs.length > 0 ? ioInputs : debugInputs;
  mappingInputs.forEach((row) => {
    if (!isRecord(row)) return;
    const inputKey = typeof row.key === 'string' ? row.key : '';
    const mapKind = typeof row.mapKind === 'string' ? row.mapKind : '';
    const mapKey = typeof row.mapKey === 'string' ? row.mapKey : '';
    if (!inputKey || !mapKind || !mapKey) return;
    const v = debugInputValueMap.get(inputKey) ?? '';
    switch (mapKind) {
      case 'header':
        applyMappedValue(debugRequestHeaders as any[], mapKey, v);
        break;
      case 'query':
        applyMappedValue(debugQueryParams as any[], mapKey, v);
        break;
      case 'path':
        applyMappedValue(debugPathParams as any[], mapKey, v);
        break;
      case 'body':
        applyMappedValue(debugRequestBody as any[], mapKey, v);
        break;
    }
  });

  applyVariableReplacement(debugRequestHeaders as any[]);
  applyVariableReplacement(debugQueryParams as any[]);
  applyVariableReplacement(debugPathParams as any[]);
  applyVariableReplacement(debugRequestBody as any[]);

  // 如果 bodyMode 是 json，自动添加 Content-Type: application/json header
  const withJsonHeaders = bodyMode === 'json'
    ? { requestHeaders: upsertHeaderRow(debugRequestHeaders, 'Content-Type', 'application/json') }
    : { requestHeaders: debugRequestHeaders };

  const debugRequestHeadersFinal = withJsonHeaders.requestHeaders;

  const successCondition = isRecord(values.successCondition) ? (values.successCondition as Record<string, unknown>) : {};

  return {
    basic: values.basic ?? {},
    request: {
      requestHeaders: withJsonHeaders.requestHeaders,
      requestBody: requestBody,
      queryParams: queryParams,
      pathParams: pathParams
    },
    response: {
      responseHeaders,
      responseBody,
      responseSchema
    },
    successConditions: Array.isArray(successCondition.successConditions) ? successCondition.successConditions : [],
    errorMessagePath: typeof successCondition.errorMessagePath === 'string' ? successCondition.errorMessagePath : '',
    ioConfig: {
      inputs: ioInputs,
      outputs: ioOutputs
    },
    debug: {
      url: values.url ?? '',
      method: actualMethod,

      requestHeaders: debugRequestHeadersFinal,
      requestBody: debugRequestBody,
      queryParams: debugQueryParams,
      pathParams: debugPathParams
    }
  };
};

// ========== OpenAPI 兼容格式转换 ==========

/**
 * 从 URL 中提取 path 部分
 * 支持格式：
 * - 相对路径: /api/users → /api/users
 * - 不带斜杠: api/users → /api/users
 * - 完整 URL: https://api.example.com/api/users → /api/users
 */
function extractPathFromUrl(url: string): string {
  if (!url) return '/';

  // 尝试解析为完整 URL
  try {
    const parsed = new URL(url);
    return parsed.pathname || '/';
  } catch {
    // 不是完整 URL，当作相对路径处理
  }

  // 相对路径处理
  if (url.startsWith('/')) {
    return url;
  }

  // 不带斜杠，添加斜杠
  return '/' + url;
}

/** 将表单数据转换为 OpenAPI 兼容格式 */
export function formValuesToOpenApiConfig(values: Record<string, unknown>): HttpActionConfig {
  const url = typeof values.url === 'string' ? values.url : '';
  const method = (typeof values.method === 'string' ? values.method : 'GET') as HttpActionConfig['method'];

  // 提取 path（支持相对路径和完整 URL）
  const path = extractPathFromUrl(url);

  // 构建参数列表
  const parameters: OpenApiParameter[] = [];

  // 请求头参数
  const requestHeaders = getTabArray(values, 'requestHeaders');
  requestHeaders.forEach((row) => {
    if (!isRecord(row)) return;
    const name = typeof row.key === 'string' ? row.key : '';
    if (!name) return;
    parameters.push({
      name,
      in: 'header',
      required: row.required === true,
      description: typeof row.description === 'string' ? row.description : '',
      schema: {
        type: typeof row.fieldType === 'string' ? row.fieldType : 'string',
        default: typeof row.defaultValue === 'string' ? row.defaultValue : ''
      }
    });
  });

  // Query 参数
  const queryParams = getTabArray(values, 'queryParams');
  queryParams.forEach((row) => {
    if (!isRecord(row)) return;
    const name = typeof row.key === 'string' ? row.key : '';
    if (!name) return;
    parameters.push({
      name,
      in: 'query',
      required: row.required === true,
      description: typeof row.description === 'string' ? row.description : '',
      schema: {
        type: typeof row.fieldType === 'string' ? row.fieldType : 'string',
        default: typeof row.defaultValue === 'string' ? row.defaultValue : ''
      }
    });
  });

  // Path 参数
  const pathParams = getTabArray(values, 'pathParams');
  pathParams.forEach((row) => {
    if (!isRecord(row)) return;
    const name = typeof row.key === 'string' ? row.key : '';
    if (!name) return;
    parameters.push({
      name,
      in: 'path',
      required: true, // path 参数总是必需的
      description: typeof row.description === 'string' ? row.description : '',
      schema: {
        type: typeof row.fieldType === 'string' ? row.fieldType : 'string',
        default: typeof row.defaultValue === 'string' ? row.defaultValue : ''
      }
    });
  });

  // 请求体
  const bodyMode = getTabString(values, 'bodyMode');
  let requestBody: HttpActionConfig['requestBody'] = undefined;

  if (bodyMode === 'json') {
    const requestBodyJson = getTabString(values, 'requestBodyJson');
    let example: object | undefined;
    if (requestBodyJson) {
      try {
        example = JSON.parse(requestBodyJson);
      } catch {
        // 解析失败，忽略
      }
    }
    requestBody = {
      required: true,
      content: {
        'application/json': {
          example
        }
      }
    };
  } else if (bodyMode === 'kv') {
    const requestBodyRows = getTabArray(values, 'requestBody');
    const properties: Record<string, unknown> = {};
    requestBodyRows.forEach((row) => {
      if (!isRecord(row)) return;
      const name = typeof row.key === 'string' ? row.key : '';
      if (!name) return;
      properties[name] = {
        type: typeof row.fieldType === 'string' ? row.fieldType : 'string',
        description: typeof row.description === 'string' ? row.description : ''
      };
    });
    requestBody = {
      required: true,
      content: {
        'application/json': {
          schema: {
            type: 'object',
            properties
          }
        }
      }
    };
  }

  // 响应定义
  const responseTabs = isRecord(values.responseTabs) ? (values.responseTabs as Record<string, unknown>) : {};
  const responseBodyTab = isRecord(responseTabs.responseBodyTab) ? (responseTabs.responseBodyTab as Record<string, unknown>) : {};
  const responseBodyJsonWrapper = isRecord(responseBodyTab.responseBodyJsonWrapper) ? (responseBodyTab.responseBodyJsonWrapper as Record<string, unknown>) : {};
  const responseBodyJson = typeof responseBodyJsonWrapper.responseBodyJson === 'string' ? responseBodyJsonWrapper.responseBodyJson : '';
  const responseSchema = isRecord(responseBodyTab.responseSchema) ? responseBodyTab.responseSchema : undefined;

  let responseExample: object | undefined;
  if (responseBodyJson) {
    try {
      responseExample = JSON.parse(responseBodyJson);
    } catch {
      // 解析失败，忽略
    }
  }

  const responses: HttpActionConfig['responses'] = {
    '200': {
      description: '成功',
      content: responseExample || responseSchema ? {
        'application/json': {
          schema: responseSchema,
          example: responseExample
        }
      } : undefined
    }
  };

  // 成功条件
  const successCondition = isRecord(values.successCondition) ? (values.successCondition as Record<string, unknown>) : {};
  const successConditions = Array.isArray(successCondition.successConditions) ? successCondition.successConditions : [];
  const errorMessagePath = typeof successCondition.errorMessagePath === 'string' ? successCondition.errorMessagePath : '';

  // 转换成功条件格式
  const conditions = successConditions.map((cond) => {
    if (!isRecord(cond)) return null;
    const fieldType = typeof cond.fieldType === 'string' ? cond.fieldType : 'responseBody';
    const fieldName = typeof cond.fieldName === 'string' ? cond.fieldName : '';
    const operator = typeof cond.operator === 'string' ? cond.operator : 'eq';
    const value = typeof cond.value === 'string' ? cond.value : '';

    // 转换 field 格式
    let field = fieldName;
    if (fieldType === 'statusCode') {
      field = 'statusCode';
    } else if (fieldType === 'responseBody' && fieldName) {
      field = fieldName.startsWith('$.') ? fieldName : `$.${fieldName}`;
    } else if (fieldType === 'responseHeader' && fieldName) {
      field = `header.${fieldName}`;
    }

    return { field, operator, value };
  }).filter((c): c is { field: string; operator: string; value: string } => c !== null);

  // 动作入参
  const inputs = Array.isArray(values.inputs) ? (values.inputs as unknown[]).map((row) => {
    if (!isRecord(row)) return null;
    return {
      key: typeof row.key === 'string' ? row.key : '',
      fieldName: typeof row.fieldName === 'string' ? row.fieldName : '',
      fieldType: typeof row.fieldType === 'string' ? row.fieldType : 'string',
      required: row.required === true,
      defaultValue: typeof row.defaultValue === 'string' ? row.defaultValue : '',
      description: typeof row.description === 'string' ? row.description : '',
      map: {
        in: (typeof row.mapKind === 'string' ? row.mapKind : 'body') as 'header' | 'query' | 'path' | 'body',
        name: typeof row.mapKey === 'string' ? row.mapKey : ''
      }
    };
  }).filter((i): i is NonNullable<typeof i> => i !== null && i.key) : [];

  // 动作出参
  const outputs = Array.isArray(values.outputs) ? (values.outputs as unknown[]).map((row) => {
    if (!isRecord(row)) return null;
    return {
      key: typeof row.key === 'string' ? row.key : '',
      fieldName: typeof row.fieldName === 'string' ? row.fieldName : '',
      fieldType: typeof row.fieldType === 'string' ? row.fieldType : 'string',
      description: typeof row.description === 'string' ? row.description : '',
      map: {
        in: (typeof row.mapKind === 'string' ? row.mapKind : 'body') as 'header' | 'body',
        path: typeof row.mapKey === 'string' ? row.mapKey : ''
      }
    };
  }).filter((o): o is NonNullable<typeof o> => o !== null && o.key) : [];

  // 基本信息
  const basic = isRecord(values.basic) ? (values.basic as Record<string, unknown>) : {};
  const actionName = typeof basic.actionName === 'string' ? basic.actionName : '';
  const actionDescription = typeof basic.actionDescription === 'string' ? basic.actionDescription : '';

  // 构建 x-onebase 扩展
  const xOnebase: OneBaseExtension = {
    actionName,
    actionDescription
  };

  if (conditions.length > 0 || errorMessagePath) {
    xOnebase.successCondition = {
      conditions,
      errorMessagePath: errorMessagePath || undefined
    };
  }

  if (inputs.length > 0) {
    xOnebase.inputs = inputs;
  }

  if (outputs.length > 0) {
    xOnebase.outputs = outputs;
  }

  // 保存调试参数值
  const debugHeaders = Array.isArray(values.debugHeaders) ? values.debugHeaders : [];
  const debugBody = typeof values.debugBody === 'string' ? values.debugBody : '';

  if (debugHeaders.length > 0 || debugBody) {
    const paramValues: Record<string, string> = {};

    // 保存 headers 的值
    debugHeaders.forEach((h: { key?: string; value?: string }) => {
      if (h.key && h.value) {
        paramValues[`header.${h.key}`] = h.value;
      }
    });

    // 保存 body
    if (debugBody) {
      paramValues['body'] = debugBody;
    }

    xOnebase.debug = { paramValues };
  }

  // 构建最终配置
  const config: HttpActionConfig = {
    path,
    method,
    summary: actionName,
    operationId: actionName ? actionName.replace(/\s+/g, '_') : undefined,
    description: actionDescription
  };

  if (parameters.length > 0) {
    config.parameters = parameters;
  }

  if (requestBody) {
    config.requestBody = requestBody;
  }

  config.responses = responses;

  // 只有有扩展字段时才添加 x-onebase
  if (Object.keys(xOnebase).some(k => xOnebase[k as keyof OneBaseExtension] !== undefined)) {
    config['x-onebase'] = xOnebase;
  }

  return config;
}

/** 将 OpenAPI 兼容格式转换为表单数据 */
export function openApiConfigToFormValues(config: HttpActionConfig): Record<string, unknown> {
  const path = config.path || '';
  const method = config.method || 'GET';

  // 解析参数
  const requestHeaders: unknown[] = [];
  const queryParams: unknown[] = [];
  const pathParams: unknown[] = [];

  if (Array.isArray(config.parameters)) {
    config.parameters.forEach((param) => {
      const row = {
        key: param.name,
        fieldName: param.name,
        fieldType: param.schema?.type || 'string',
        required: param.required || false,
        defaultValue: param.schema?.default || '',
        description: param.description || ''
      };

      switch (param.in) {
        case 'header':
          requestHeaders.push(row);
          break;
        case 'query':
          queryParams.push(row);
          break;
        case 'path':
          pathParams.push(row);
          break;
      }
    });
  }

  // 解析请求体
  let bodyMode = 'none';
  let requestBody: unknown[] = [];
  let requestBodyJson = '';

  if (config.requestBody?.content?.['application/json']) {
    const jsonContent = config.requestBody.content['application/json'];
    bodyMode = 'json';

    if (jsonContent.example) {
      requestBodyJson = JSON.stringify(jsonContent.example, null, 2);
    } else if (jsonContent.schema) {
      // 从 schema 生成示例
      requestBodyJson = JSON.stringify(jsonContent.schema, null, 2);
    }
  }

  // 解析响应
  let responseBodyJson = '';
  let responseSchema: unknown = undefined;

  const response200 = config.responses?.['200'];
  if (response200?.content?.['application/json']) {
    const jsonContent = response200.content['application/json'];
    if (jsonContent.example) {
      responseBodyJson = JSON.stringify(jsonContent.example, null, 2);
    }
    if (jsonContent.schema) {
      responseSchema = jsonContent.schema;
    }
  }

  // 解析 x-onebase 扩展
  const xOnebase = config['x-onebase'] || {};
  const actionName = xOnebase.actionName || config.summary || '';
  const actionDescription = xOnebase.actionDescription || config.description || '';

  // 解析成功条件
  let successConditions: unknown[] = [];
  let errorMessagePath = '';

  if (xOnebase.successCondition) {
    errorMessagePath = xOnebase.successCondition.errorMessagePath || '';
    successConditions = (xOnebase.successCondition.conditions || []).map((cond) => {
      // 转换 field 格式回表单格式
      let fieldType: 'statusCode' | 'responseHeader' | 'responseBody' = 'responseBody';
      let fieldName = cond.field;

      if (cond.field === 'statusCode') {
        fieldType = 'statusCode';
        fieldName = '';
      } else if (cond.field.startsWith('header.')) {
        fieldType = 'responseHeader';
        fieldName = cond.field.replace('header.', '');
      } else if (cond.field.startsWith('$.')) {
        fieldType = 'responseBody';
        fieldName = cond.field;
      }

      return {
        id: `cond-${Date.now()}-${Math.random().toString(36).slice(2)}`,
        fieldType,
        fieldName,
        dataType: 'string' as const,
        operator: cond.operator as 'eq' | 'ne' | 'in' | 'notIn' | 'gt' | 'lt' | 'gte' | 'lte',
        valueSource: 'custom' as const,
        value: cond.value
      };
    });
  }

  // 解析入参
  const inputs = (xOnebase.inputs || []).map((input) => ({
    id: `input-${Date.now()}-${Math.random().toString(36).slice(2)}`,
    key: input.key,
    fieldName: input.fieldName,
    fieldType: input.fieldType,
    required: input.required,
    defaultValue: input.defaultValue || '',
    description: input.description || '',
    mapKind: input.map.in,
    mapKey: input.map.name
  }));

  // 解析出参
  const outputs = (xOnebase.outputs || []).map((output) => ({
    id: `output-${Date.now()}-${Math.random().toString(36).slice(2)}`,
    key: output.key,
    fieldName: output.fieldName,
    fieldType: output.fieldType,
    description: output.description || '',
    mapKind: output.map.in,
    mapKey: output.map.path
  }));

  // 解析调试参数值
  const debugParamValues = xOnebase.debug?.paramValues || {};
  const debugHeaders: { key: string; value: string }[] = [];
  let debugBody = '';

  // 恢复 headers 值
  requestHeaders.forEach((row: { key?: string }) => {
    const key = row.key || '';
    const savedValue = debugParamValues[`header.${key}`] || '';
    debugHeaders.push({ key, value: savedValue });
  });

  // 恢复 body 值
  debugBody = debugParamValues['body'] || '';

  return {
    basic: {
      actionName,
      actionDescription
    },
    tabs: {
      params: {
        queryParams,
        pathParams
      },
      headers: {
        requestHeaders
      },
      body: {
        bodyMode,
        requestBody,
        jsonBody: {
          requestBodyJson
        }
      }
    },
    successCondition: {
      successConditions,
      errorMessagePath
    },
    responseTabs: {
      responseBodyTab: {
        responseBodyMode: 'json',
        responseBodyJsonWrapper: {
          responseBodyJson
        },
        responseSchema
      }
    },
    inputs,
    outputs,
    url: path,
    method,
    baseUrl: '',
    authType: '',
    debugHeaders,
    debugBody
  };
}
