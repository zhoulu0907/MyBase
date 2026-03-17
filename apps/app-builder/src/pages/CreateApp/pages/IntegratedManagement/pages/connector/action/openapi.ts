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
    return {
      key,
      fieldName: key,
      fieldType: mapOpenApiSchemaTypeToFieldType(schema),
      required: Boolean(pr.required),
      defaultValue: schema && isRecord(schema) && schema.default != null ? String(schema.default) : '',
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

const extractRequestBodyRows = (op: Record<string, unknown>) => {
  const requestBody = isRecord(op.requestBody) ? (op.requestBody as Record<string, unknown>) : undefined;
  const requestBodyContent =
    requestBody && isRecord(requestBody.content) ? (requestBody.content as Record<string, unknown>) : undefined;
  const jsonContent =
    requestBodyContent && isRecord(requestBodyContent['application/json'])
      ? (requestBodyContent['application/json'] as Record<string, unknown>)
      : undefined;
  const reqSchema = jsonContent ? jsonContent.schema : undefined;

  const rows =
    isRecord(reqSchema) && reqSchema.type === 'object'
      ? buildParamRowsFromSchemaObject(reqSchema, (reqSchema as Record<string, unknown>).required)
      : reqSchema
        ? ([
            {
              key: 'body',
              fieldName: 'body',
              fieldType: mapOpenApiSchemaTypeToFieldType(reqSchema),
              required: true,
              defaultValue: '',
              description: jsonContent && typeof jsonContent.description === 'string' ? jsonContent.description : ''
            }
          ] satisfies ParamRow[])
        : ([] as ParamRow[]);

  const requestBodyJson = reqSchema ? JSON.stringify(reqSchema, null, 2) : '';

  return { rows, isJsonBody: Boolean(jsonContent), requestBodyJson };
};

const extractResponse = (op: Record<string, unknown>) => {
  const responseObj = isRecord(op.responses) ? (op.responses as Record<string, unknown>) : {};
  const status =
    ['200', '201', 'default'].find((k) => isRecord(responseObj) && k in responseObj) || Object.keys(responseObj)[0];
  const resp = status && isRecord(responseObj) ? responseObj[status] : undefined;
  const respRec = isRecord(resp) ? (resp as Record<string, unknown>) : undefined;
  const respContent = respRec && isRecord(respRec.content) ? (respRec.content as Record<string, unknown>) : undefined;
  const respJson =
    respContent && isRecord(respContent['application/json'])
      ? (respContent['application/json'] as Record<string, unknown>)
      : undefined;
  const respSchema = respJson ? respJson.schema : undefined;

  const responseBody =
    isRecord(respSchema) && respSchema.type === 'object'
      ? buildParamRowsFromSchemaObject(respSchema, (respSchema as Record<string, unknown>).required, true)
      : respSchema
        ? ([
            {
              key: 'body',
              fieldName: 'body',
              fieldType: mapOpenApiSchemaTypeToFieldType(respSchema),
              required: false,
              defaultValue: '',
              description: respJson && typeof respJson.description === 'string' ? respJson.description : '',
              expose: false,
              jsonPath: '$'
            }
          ] satisfies ParamRow[])
        : ([] as ParamRow[]);

  const responseBodyJson = respSchema ? JSON.stringify(respSchema, null, 2) : '';

  const responseHeadersRaw =
    respRec && isRecord(respRec.headers) ? (respRec.headers as Record<string, unknown>) : undefined;
  const responseHeaders = responseHeadersRaw
    ? Object.entries(responseHeadersRaw).map(([k, v]) => {
        const vr = isRecord(v) ? (v as Record<string, unknown>) : {};
        const schema = isRecord(vr.schema) ? vr.schema : undefined;
        return {
          key: k,
          fieldName: k,
          fieldType: mapOpenApiSchemaTypeToFieldType(schema),
          required: false,
          defaultValue: schema && isRecord(schema) && schema.default != null ? String(schema.default) : '',
          description: typeof vr.description === 'string' ? vr.description : '',
          expose: false,
          jsonPath: `$.${k}`
        } satisfies ParamRow;
      })
    : [];

  let successConditionPath = '';
  let successConditionValue = '';
  let errorMessagePath = '';

  if (isRecord(respSchema) && respSchema.type === 'object') {
    const properties = isRecord(respSchema.properties) ? respSchema.properties as Record<string, unknown> : {};
    
    if ('code' in properties) {
      successConditionPath = '$.code';
      successConditionValue = '0';
    } else if ('status' in properties) {
      successConditionPath = '$.status';
      successConditionValue = 'success';
    } else if ('success' in properties) {
      successConditionPath = '$.success';
      successConditionValue = 'true';
    }
    
    if ('message' in properties) {
      errorMessagePath = '$.message';
    } else if ('msg' in properties) {
      errorMessagePath = '$.msg';
    } else if ('error' in properties) {
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
  const { rows: requestBody, isJsonBody, requestBodyJson } = extractRequestBodyRows(op);
  const { responseHeaders, responseBody, responseBodyJson: respBodyJson, successConditionPath, successConditionValue, errorMessagePath } = extractResponse(op);

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
  const { rows: requestBody, isJsonBody, requestBodyJson } = extractRequestBodyRows(op);
  const { responseHeaders, responseBody } = extractResponse(op);

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
