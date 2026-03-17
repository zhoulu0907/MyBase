export type OpenApiMethod = 'get' | 'post' | 'put' | 'delete' | 'patch' | 'options' | 'head';

export type OpenApiOperation = {
  method: OpenApiMethod;
  path: string;
  summary?: string;
  operationId?: string;
  raw: unknown;
};

export type ParsedOpenApi = {
  doc: unknown;
  operations: OpenApiOperation[];
  error?: string;
};

export type ParamRow = {
  id?: string;
  key: string;
  fieldName: string;
  fieldType: string;
  required: boolean;
  defaultValue: string;
  description: string;
  expose?: boolean;
  jsonPath?: string;
};

export const OPENAPI_METHODS: OpenApiMethod[] = ['get', 'post', 'put', 'delete', 'patch', 'options', 'head'];

export const isRecord = (v: unknown): v is Record<string, unknown> => typeof v === 'object' && v !== null;

/** 解析 $ref 引用，返回实际的 schema 对象 */
export const resolveRef = (schema: unknown, doc: unknown): unknown => {
  if (!isRecord(schema)) return schema;

  const ref = schema.$ref;
  if (typeof ref !== 'string' || !ref.startsWith('#/')) return schema;

  // 解析路径，如 "#/components/schemas/DataCardRequest"
  const pathParts = ref.slice(2).split('/');
  let current: unknown = doc;

  for (const part of pathParts) {
    if (!isRecord(current)) return schema;
    current = current[part];
  }

  // 递归解析（处理嵌套的 $ref）
  return resolveRef(current, doc);
};

/** 递归解析 schema 中的所有 $ref 引用，保留 examples 字段 */
export const resolveAllRefs = (schema: unknown, doc: unknown): unknown => {
  if (!isRecord(schema)) return schema;

  // 先解析当前层级的 $ref
  const resolved = resolveRef(schema, doc);
  if (!isRecord(resolved)) return resolved;

  // 结果对象，保留所有原始字段
  const result: Record<string, unknown> = { ...resolved };

  // 递归解析 properties 中的 $ref
  if (isRecord(resolved.properties)) {
    const newProperties: Record<string, unknown> = {};
    for (const [key, value] of Object.entries(resolved.properties)) {
      newProperties[key] = resolveAllRefs(value, doc);
    }
    result.properties = newProperties;
  }

  // 递归解析 items 中的 $ref（用于数组类型）
  if (isRecord(resolved) && 'items' in resolved) {
    result.items = resolveAllRefs(resolved.items, doc);
  }

  return result;
};

export const getOperationKey = (op: OpenApiOperation) => `${op.method.toUpperCase()} ${op.path}`;

export const pickFirstServerUrl = (doc: unknown) => {
  if (!isRecord(doc)) return '';
  const servers = doc.servers;
  if (!Array.isArray(servers) || servers.length === 0) return '';
  const first = servers[0];
  if (!isRecord(first)) return '';
  const url = first.url;
  return typeof url === 'string' ? url : '';
};

export const mapOpenApiSchemaTypeToFieldType = (schema: unknown) => {
  const t = isRecord(schema) ? schema.type : undefined;
  if (t === 'number' || t === 'integer') return 'number';
  if (t === 'boolean') return 'boolean';
  if (t === 'array') return 'array';
  if (t === 'object') return 'object';
  return 'string';
};

/** 从 JavaScript 值推断字段类型 */
const inferFieldTypeFromValue = (value: unknown): string => {
  if (Array.isArray(value)) return 'array';
  if (typeof value === 'object' && value !== null) return 'object';
  if (typeof value === 'number') return 'number';
  if (typeof value === 'boolean') return 'boolean';
  return 'string';
};

export const buildParamRowsFromSchemaObject = (schema: unknown, requiredList: unknown, autoExpose: boolean = false): ParamRow[] => {
  if (!isRecord(schema)) return [];
  const properties = schema.properties;
  if (!isRecord(properties)) return [];
  const requiredSet = new Set(Array.isArray(requiredList) ? (requiredList as string[]) : []);
  return Object.entries(properties).map(([key, propSchema]) => {
    const s = isRecord(propSchema) ? propSchema : {};
    const expose = autoExpose && requiredSet.has(key);
    return {
      key,
      fieldName: (typeof s.title === 'string' ? s.title : '') || key,
      fieldType: mapOpenApiSchemaTypeToFieldType(s),
      required: requiredSet.has(key),
      defaultValue: expose ? `\${${key}}` : (s.default != null ? String(s.default) : ''),
      description: typeof s.description === 'string' ? s.description : '',
      expose,
      jsonPath: expose ? `$.${key}` : ''
    };
  });
};

export const upsertHeaderRow = (rows: unknown[], name: string, value: string) => {
  const next = Array.isArray(rows) ? [...rows] : [];
  const idx = next.findIndex((r) => {
    if (!isRecord(r)) return false;
    const k = typeof r.key === 'string' ? r.key : undefined;
    const fieldName = typeof r.fieldName === 'string' ? r.fieldName : undefined;
    return (k || fieldName) === name;
  });
  const base: ParamRow = {
    key: name,
    fieldName: name,
    fieldType: 'string',
    required: false,
    defaultValue: value,
    description: ''
  };
  if (idx >= 0) {
    next[idx] = { ...base, ...(isRecord(next[idx]) ? next[idx] : {}) };
  } else {
    next.push(base);
  }
  return next;
};

export const buildActionNameFromOpenApi = (op: OpenApiOperation) => {
  const base =
    (op.operationId && String(op.operationId)) ||
    (op.summary && String(op.summary)) ||
    `${op.method.toUpperCase()}_${op.path}`;
  const cleaned = base
    .replace(/[^\w\u4e00-\u9fa5]+/g, '_')
    .replace(/^_+|_+$/g, '')
    .slice(0, 128);
  return cleaned || 'http_action';
};

export const parseOpenApiOperations = (raw: string): ParsedOpenApi => {
  try {
    const doc = JSON.parse(raw || '{}') as unknown;
    if (!isRecord(doc) || !isRecord(doc.paths)) {
      return { doc, operations: [], error: '未识别到 OpenAPI paths' };
    }

    const operations: OpenApiOperation[] = [];
    for (const [path, methods] of Object.entries(doc.paths)) {
      if (!isRecord(methods)) continue;
      for (const m of OPENAPI_METHODS) {
        const op = methods[m];
        if (!op) continue;
        operations.push({
          method: m,
          path,
          summary: isRecord(op) && typeof op.summary === 'string' ? op.summary : undefined,
          operationId: isRecord(op) && typeof op.operationId === 'string' ? op.operationId : undefined,
          raw: op
        });
      }
    }

    return { doc, operations, error: operations.length === 0 ? '未解析到任何接口操作' : undefined };
  } catch {
    return { doc: {}, operations: [], error: 'OpenAPI 内容不是合法 JSON' };
  }
};

const extractParameters = (op: Record<string, unknown>) => {
  const parameters = Array.isArray(op.parameters) ? (op.parameters as unknown[]) : [];
  const mapParam = (p: unknown) => {
    const pr = isRecord(p) ? p : {};
    const key = typeof pr.name === 'string' ? pr.name : '';
    const schema = isRecord(pr.schema) ? pr.schema : undefined;

    // 优先使用 parameter 级别的 example，其次 schema.default
    let defaultValue = '';
    if (pr.example !== undefined && pr.example !== null && pr.example !== '') {
      defaultValue = String(pr.example);
    } else if (schema && isRecord(schema) && schema.default != null) {
      defaultValue = String(schema.default);
    }

    return {
      key,
      fieldName: key,
      fieldType: mapOpenApiSchemaTypeToFieldType(schema),
      required: Boolean(pr.required),
      defaultValue,
      description: typeof pr.description === 'string' ? pr.description : ''
    } satisfies ParamRow;
  };

  const headers = parameters
    .filter((p) => isRecord(p) && p.in === 'header')
    .map(mapParam)
    .filter((r) => r.key);
  const query = parameters
    .filter((p) => isRecord(p) && p.in === 'query')
    .map(mapParam)
    .filter((r) => r.key);
  const path = parameters
    .filter((p) => isRecord(p) && p.in === 'path')
    .map(mapParam)
    .filter((r) => r.key);

  return { headers, query, path };
};

const extractRequestBodyRows = (op: Record<string, unknown>, doc: unknown) => {
  const requestBody = isRecord(op.requestBody) ? (op.requestBody as Record<string, unknown>) : undefined;
  const requestBodyContent =
    requestBody && isRecord(requestBody.content) ? (requestBody.content as Record<string, unknown>) : undefined;
  const jsonContent =
    requestBodyContent && isRecord(requestBodyContent['application/json'])
      ? (requestBodyContent['application/json'] as Record<string, unknown>)
      : undefined;

  let rows: ParamRow[] = [];
  let requestBodyJson = '';
  let isJsonBody = Boolean(jsonContent);

  if (jsonContent) {
    // 优先使用 content 级别的 example
    const example = jsonContent.example;

    // 其次检查 schema 级别的 examples 数组
    const reqSchemaRaw = jsonContent.schema;
    const reqSchema = resolveAllRefs(reqSchemaRaw, doc);
    const schemaExamples = isRecord(reqSchema) && Array.isArray(reqSchema.examples) ? reqSchema.examples : [];
    const firstSchemaExample = schemaExamples.length > 0 ? schemaExamples[0] : null;

    if (example !== undefined && example !== null) {
      requestBodyJson = JSON.stringify(example, null, 2);

      // 从示例数据推断字段结构
      if (Array.isArray(example)) {
        // 数组类型：取第一个元素作为模板
        const firstItem = example[0];
        if (isRecord(firstItem)) {
          rows = Object.entries(firstItem).map(([key, value]) => ({
            key,
            fieldName: key,
            fieldType: inferFieldTypeFromValue(value),
            required: false,
            defaultValue: `\${${key}}`,
            description: ''
          }));
        } else {
          // 数组元素是基本类型
          rows = [{
            key: 'body',
            fieldName: 'body',
            fieldType: 'array',
            required: true,
            defaultValue: requestBodyJson,
            description: '请求体 (JSON 数组)'
          }];
        }
      } else if (isRecord(example)) {
        // 对象类型
        rows = Object.entries(example).map(([key, value]) => ({
          key,
          fieldName: key,
          fieldType: inferFieldTypeFromValue(value),
          required: false,
          defaultValue: `\${${key}}`,
          description: ''
        }));
      }
    } else if (firstSchemaExample !== null) {
      // 使用 schema 级别的 examples
      requestBodyJson = JSON.stringify(firstSchemaExample, null, 2);

      if (Array.isArray(firstSchemaExample)) {
        const firstItem = firstSchemaExample[0];
        if (isRecord(firstItem)) {
          rows = Object.entries(firstItem).map(([key, value]) => ({
            key,
            fieldName: key,
            fieldType: inferFieldTypeFromValue(value),
            required: false,
            defaultValue: `\${${key}}`,
            description: ''
          }));
        } else {
          rows = [{
            key: 'body',
            fieldName: 'body',
            fieldType: 'array',
            required: true,
            defaultValue: requestBodyJson,
            description: '请求体 (JSON 数组)'
          }];
        }
      } else if (isRecord(firstSchemaExample)) {
        rows = Object.entries(firstSchemaExample).map(([key, value]) => ({
          key,
          fieldName: key,
          fieldType: inferFieldTypeFromValue(value),
          required: false,
          defaultValue: `\${${key}}`,
          description: ''
        }));
      }
    } else if (isRecord(reqSchema)) {
      // 没有 example，使用已解析的 schema
      requestBodyJson = JSON.stringify(reqSchema, null, 2);

      if (reqSchema.type === 'object' && isRecord(reqSchema.properties)) {
        rows = buildParamRowsFromSchemaObject(reqSchema, (reqSchema as Record<string, unknown>).required);
      } else if (reqSchema.type === 'array') {
        rows = [{
          key: 'body',
          fieldName: 'body',
          fieldType: 'array',
          required: true,
          defaultValue: requestBodyJson,
          description: '请求体 (JSON 数组)'
        }];
      } else {
        rows = [{
          key: 'body',
          fieldName: 'body',
          fieldType: mapOpenApiSchemaTypeToFieldType(reqSchema),
          required: true,
          defaultValue: '',
          description: ''
        }];
      }
    }
  }

  return { rows, isJsonBody, requestBodyJson };
};

const extractResponse = (op: Record<string, unknown>, doc: unknown) => {
  const responseObj = isRecord(op.responses) ? (op.responses as Record<string, unknown>) : {};
  console.log('[extractResponse] responseObj:', responseObj);
  const status =
    ['200', '201', 'default'].find((k) => isRecord(responseObj) && k in responseObj) || Object.keys(responseObj)[0];
  console.log('[extractResponse] status:', status);
  const resp = status && isRecord(responseObj) ? responseObj[status] : undefined;
  const respRec = isRecord(resp) ? (resp as Record<string, unknown>) : undefined;
  const respContent = respRec && isRecord(respRec.content) ? (respRec.content as Record<string, unknown>) : undefined;
  const respJson =
    respContent && isRecord(respContent['application/json'])
      ? (respContent['application/json'] as Record<string, unknown>)
      : undefined;

  console.log('[extractResponse] respJson:', respJson);

  let responseBody: ParamRow[] = [];
  let responseBodyJson = '';
  let firstSchemaExample: unknown = null;

  if (respJson) {
    // 优先使用 content 级别的 example
    const example = respJson.example;
    console.log('[extractResponse] content-level example:', example);
    // 其次检查 schema 级别的 examples 数组
    const respSchemaRaw = respJson.schema;
    const respSchema = resolveAllRefs(respSchemaRaw, doc);
    console.log('[extractResponse] respSchema:', respSchema);
    console.log('[extractResponse] respSchema.examples:', isRecord(respSchema) ? (respSchema as Record<string, unknown>).examples : 'not a record');
    const schemaExamples = isRecord(respSchema) && Array.isArray(respSchema.examples) ? respSchema.examples : [];
    console.log('[extractResponse] schemaExamples:', schemaExamples);
    firstSchemaExample = schemaExamples.length > 0 ? schemaExamples[0] : null;
    console.log('[extractResponse] firstSchemaExample:', firstSchemaExample);

    if (example !== undefined && example !== null) {
      responseBodyJson = JSON.stringify(example, null, 2);

      // 从示例数据推断字段结构
      if (Array.isArray(example)) {
        // 数组类型：取第一个元素作为模板
        const firstItem = example[0];
        if (isRecord(firstItem)) {
          responseBody = Object.entries(firstItem).map(([key, value]) => ({
            key,
            fieldName: key,
            fieldType: inferFieldTypeFromValue(value),
            required: false,
            defaultValue: '',
            description: '',
            expose: false,
            jsonPath: `$.${key}`
          }));
        } else {
          responseBody = [{
            key: 'body',
            fieldName: 'body',
            fieldType: 'array',
            required: false,
            defaultValue: '',
            description: '响应体 (JSON 数组)',
            expose: false,
            jsonPath: '$'
          }];
        }
      } else if (isRecord(example)) {
        responseBody = Object.entries(example).map(([key, value]) => ({
          key,
          fieldName: key,
          fieldType: inferFieldTypeFromValue(value),
          required: false,
          defaultValue: '',
          description: '',
          expose: false,
          jsonPath: `$.${key}`
        }));
      }
    } else if (firstSchemaExample !== null) {
      // 使用 schema 级别的 examples
      responseBodyJson = JSON.stringify(firstSchemaExample, null, 2);

      if (Array.isArray(firstSchemaExample)) {
        const firstItem = firstSchemaExample[0];
        if (isRecord(firstItem)) {
          responseBody = Object.entries(firstItem).map(([key, value]) => ({
            key,
            fieldName: key,
            fieldType: inferFieldTypeFromValue(value),
            required: false,
            defaultValue: '',
            description: '',
            expose: false,
            jsonPath: `$.${key}`
          }));
        } else {
          responseBody = [{
            key: 'body',
            fieldName: 'body',
            fieldType: 'array',
            required: false,
            defaultValue: '',
            description: '响应体 (JSON 数组)',
            expose: false,
            jsonPath: '$'
          }];
        }
      } else if (isRecord(firstSchemaExample)) {
        responseBody = Object.entries(firstSchemaExample).map(([key, value]) => ({
          key,
          fieldName: key,
          fieldType: inferFieldTypeFromValue(value),
          required: false,
          defaultValue: '',
          description: '',
          expose: false,
          jsonPath: `$.${key}`
        }));
      }
    } else if (isRecord(respSchema)) {
      // 没有 example，使用已解析的 schema
      responseBodyJson = JSON.stringify(respSchema, null, 2);

      if (respSchema.type === 'object' && isRecord(respSchema.properties)) {
        responseBody = buildParamRowsFromSchemaObject(respSchema, (respSchema as Record<string, unknown>).required, true);
      } else if (respSchema.type === 'array' && isRecord(respSchema.items)) {
        const itemsSchema = respSchema.items;
        // items 已经被 resolveAllRefs 解析过了
        if (isRecord(itemsSchema) && itemsSchema.type === 'object' && isRecord(itemsSchema.properties)) {
          const itemRows = buildParamRowsFromSchemaObject(itemsSchema, (itemsSchema as Record<string, unknown>).required, true);
          responseBody = itemRows.map(row => ({
            ...row,
            jsonPath: row.jsonPath || `$.${row.key}`
          }));
        } else {
          responseBody = [{
            key: 'body',
            fieldName: 'body',
            fieldType: 'array',
            required: false,
            defaultValue: '',
            description: '响应体 (JSON 数组)',
            expose: false,
            jsonPath: '$'
          }];
        }
      } else {
        responseBody = [{
          key: 'body',
          fieldName: 'body',
          fieldType: mapOpenApiSchemaTypeToFieldType(respSchema),
          required: false,
          defaultValue: '',
          description: '',
          expose: false,
          jsonPath: '$'
        }];
      }
    }
  }

  console.log('[extractResponse] final responseBody:', responseBody);
  console.log('[extractResponse] final responseBodyJson:', responseBodyJson);

  const responseHeadersRaw =
    respRec && isRecord(respRec.headers) ? (respRec.headers as Record<string, unknown>) : undefined;
  const responseHeaders = responseHeadersRaw
    ? Object.entries(responseHeadersRaw).map(([k, v]) => {
        const vr = isRecord(v) ? (v as Record<string, unknown>) : {};
        const schema = isRecord(vr.schema) ? vr.schema : undefined;

        // 优先使用 example，其次 schema.default
        let defaultValue = '';
        if (vr.example !== undefined && vr.example !== null && vr.example !== '') {
          defaultValue = String(vr.example);
        } else if (schema && isRecord(schema) && schema.default != null) {
          defaultValue = String(schema.default);
        }

        return {
          key: k,
          fieldName: k,
          fieldType: mapOpenApiSchemaTypeToFieldType(schema),
          required: false,
          defaultValue,
          description: typeof vr.description === 'string' ? vr.description : '',
          expose: false,
          jsonPath: `$.${k}`
        } satisfies ParamRow;
      })
    : [];

  let successConditionPath = '';
  let successConditionValue = '';
  let errorMessagePath = '';

  // 从示例数据中检查成功条件
  const exampleToCheck = respJson?.example ?? firstSchemaExample;
  const dataToCheck = Array.isArray(exampleToCheck) ? exampleToCheck[0] : exampleToCheck;

  if (isRecord(dataToCheck)) {
    if ('success' in dataToCheck) {
      successConditionPath = '$.success';
      successConditionValue = 'true';
    } else if ('code' in dataToCheck) {
      successConditionPath = '$.code';
      successConditionValue = '0';
    } else if ('status' in dataToCheck) {
      successConditionPath = '$.status';
      successConditionValue = 'success';
    }

    if ('message' in dataToCheck) {
      errorMessagePath = '$.message';
    } else if ('msg' in dataToCheck) {
      errorMessagePath = '$.msg';
    } else if ('error' in dataToCheck) {
      errorMessagePath = '$.error';
    }
  }

  return { responseHeaders, responseBody, responseBodyJson, successConditionPath, successConditionValue, errorMessagePath };
};

export const buildHttpFormValuesFromOpenApi = (doc: unknown, operation: OpenApiOperation) => {
  const serverUrl = pickFirstServerUrl(doc);
  const url = `${serverUrl || ''}${operation.path}`;

  const op = isRecord(operation.raw) ? (operation.raw as Record<string, unknown>) : {};
  const { headers, query, path } = extractParameters(op);
  const { rows: requestBody, isJsonBody, requestBodyJson } = extractRequestBodyRows(op, doc);
  const { responseHeaders, responseBody, responseBodyJson: respBodyJson, successConditionPath, successConditionValue, errorMessagePath } = extractResponse(op, doc);

  const method = operation.method.toUpperCase() === 'POST' && isJsonBody ? 'POST_JSON' : operation.method.toUpperCase();

  const requestHeaders = isJsonBody ? upsertHeaderRow(headers, 'Content-Type', 'application/json') : headers;
  const bodyMode = isJsonBody ? 'json' : (requestBody.length > 0 ? 'kv' : 'none');
  const respBodyMode = respBodyJson ? 'json' : 'text';

  const successConditions = [];
  
  if (successConditionPath && successConditionValue) {
    successConditions.push({
      id: `cond-${Date.now()}-${Math.random().toString(36).slice(2)}`,
      fieldType: successConditionPath.startsWith('$.') ? 'responseBody' : 'responseBody',
      fieldName: successConditionPath,
      dataType: 'string',
      operator: 'eq',
      valueSource: 'custom',
      value: successConditionValue
    });
  }
  
  successConditions.push({
    id: `cond-${Date.now()}-${Math.random().toString(36).slice(2)}`,
    fieldType: 'statusCode',
    fieldName: '',
    dataType: 'number',
    operator: 'eq',
    valueSource: 'custom',
    value: '200'
  });

  return {
    url,
    method,
    tabs: {
      params: {
        queryParams: query,
        pathParams: path
      },
      headers: {
        requestHeaders
      },
      body: {
        bodyMode,
        kvBody: {
          requestBody: isJsonBody ? [] : requestBody
        },
        jsonBody: {
          requestBodyJson: isJsonBody ? requestBodyJson : ''
        }
      }
    },
    successCondition: {
      successConditions,
      errorMessagePath
    },
    responseTabs: {
      responseHeaders,
      responseBodyTab: {
        responseBodyMode: respBodyMode,
        responseBodyJson: respBodyJson,
        responseBodyText: ''
      },
      responseBody
    }
  };
};

export const buildHttpActionConfigFromOpenApi = (doc: unknown, operation: OpenApiOperation, actionName?: string) => {
  const serverUrl = pickFirstServerUrl(doc);
  const url = `${serverUrl || ''}${operation.path}`;

  const op = isRecord(operation.raw) ? (operation.raw as Record<string, unknown>) : {};
  const { headers, query, path } = extractParameters(op);
  const { rows: requestBody, isJsonBody } = extractRequestBodyRows(op, doc);
  const { responseHeaders, responseBody } = extractResponse(op, doc);

  const name = (actionName || '').trim() || buildActionNameFromOpenApi(operation);
  const method = operation.method.toUpperCase();
  const methodMode = method === 'POST' && isJsonBody ? 'POST_JSON' : undefined;
  const debugMethod = methodMode === 'POST_JSON' ? 'POST' : method;

  const requestHeaders =
    methodMode === 'POST_JSON' ? upsertHeaderRow(headers, 'Content-Type', 'application/json') : headers;

  const toDebugRows = (rows: unknown[]) =>
    Array.isArray(rows)
      ? rows.map((row) =>
          typeof row === 'object' && row !== null
            ? {
                ...row,
                fieldValue:
                  (row as Record<string, unknown>).fieldValue ?? (row as Record<string, unknown>).defaultValue ?? ''
              }
            : { fieldValue: '' }
        )
      : [];

  return {
    actionName: name,
    actionConfig: {
      basic: { actionName: name },
      request: {
        requestHeaders,
        requestBody,
        queryParams: query,
        pathParams: path
      },
      response: {
        responseHeaders,
        responseBody
      },
      debug: {
        url,
        method: debugMethod,
        methodMode,
        requestHeaders: toDebugRows(requestHeaders),
        requestBody: toDebugRows(requestBody),
        queryParams: toDebugRows(query),
        pathParams: toDebugRows(path)
      }
    }
  };
};
